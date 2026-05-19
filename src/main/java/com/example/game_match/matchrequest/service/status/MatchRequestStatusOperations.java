package com.example.game_match.matchrequest.service.status;

import com.example.game_match.friendlymatch.domain.FriendlyMatch;
import com.example.game_match.friendlymatch.domain.vo.FriendlyMatchStateVo;
import com.example.game_match.gameuser.domain.GameUser;
import com.example.game_match.gameuser.repository.GameUserRepository;
import com.example.game_match.global.exception.BusinessException;
import com.example.game_match.global.exception.ErrorCode;
import com.example.game_match.matchparticipation.domain.MatchParticipation;
import com.example.game_match.matchparticipation.domain.vo.MatchParticipationRole;
import com.example.game_match.matchparticipation.repository.MatchParticipationRepository;
import com.example.game_match.matchrequest.domain.MatchRequest;
import com.example.game_match.matchrequest.domain.MatchRequestParticipation;
import com.example.game_match.matchrequest.domain.vo.MatchRequestStatus;
import com.example.game_match.matchrequest.repository.MatchRequestParticipationRepository;
import com.example.game_match.matchrequest.repository.MatchRequestRepository;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MatchRequestStatusOperations {
    private final MatchRequestRepository matchRequestRepository;
    private final MatchRequestParticipationRepository matchRequestParticipationRepository;
    private final MatchParticipationRepository matchParticipationRepository;
    private final GameUserRepository gameUserRepository;

    // 대기 중인 친선 경기 신청을 승인한다.
    // 승인 가능 상태인지 검증한 뒤, 신청 팀원을 실제 경기 참여자(client)로 등록한다.
    public void approveRequest(MatchRequest request) {
        FriendlyMatch friendlyMatch = request.getFriendlyMatch();

        if (request.getStatus() == MatchRequestStatus.approve) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미 매칭이 확정된 신청입니다.");
        }

        if (request.getStatus() != MatchRequestStatus.await) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "대기 중인 신청만 승인할 수 있습니다.");
        }

        if (isRecruitDeadlineClosed(friendlyMatch)) {
            throw new BusinessException(ErrorCode.MATCH_REQUEST_CLOSED, "모집이 마감되어 더 이상 승인할 수 없습니다.");
        }

        if (hasAnotherSelectedRequest(friendlyMatch.getId(), request.getId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_MATCH_REQUEST, "이미 승인된 신청이 있는 경기입니다.");
        }

        finalizeApprovedRequest(request);
    }

    // 이미 승인된 신청을 다시 대기 상태로 되돌린다.
    // 승인으로 추가된 client 참여자를 제거하고, 승인 때문에 자동 거절된 다른 신청들을 복구한다.
    public void revokeApprovedRequest(MatchRequest request) {
        LocalDateTime approvedAt = request.getUpdatedAt();

        removeClientParticipantsForRequest(request);
        request.updateStatus(MatchRequestStatus.await);
        restoreRequestsClosedByApproval(request.getFriendlyMatch().getId(), request.getId(), approvedAt);
    }

    // 호스트가 대기 중인 신청을 거절한다.
    // 승인된 신청이 reject되는 경우도 들어올 수 있으므로, client 참여자 제거 후 reject 상태로 바꾼다.
    public void rejectRequestByHost(MatchRequest request) {
        if (request.getStatus() == MatchRequestStatus.reject) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미 거절된 신청입니다.");
        }

        if (request.getStatus() == MatchRequestStatus.cancel) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "신청자가 이미 취소한 신청입니다.");
        }

        if (request.getStatus() != MatchRequestStatus.await
                && request.getStatus() != MatchRequestStatus.approve) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "처리할 수 없는 신청 상태입니다.");
        }

        removeClientParticipantsForRequest(request);
        request.updateStatus(MatchRequestStatus.reject);
    }

    // 신청자가 본인의 신청을 취소한다.
    // 승인된 신청이었다면 client 참여자를 제거하고, 승인 때문에 닫혔던 다른 신청들을 복구한다.
    public void cancelRequest(MatchRequest request) {
        LocalDateTime approvedAt = request.getStatus() == MatchRequestStatus.approve
                ? request.getUpdatedAt()
                : null;

        if (request.getStatus() == MatchRequestStatus.reject) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미 거절된 신청입니다.");
        }

        if (request.getStatus() == MatchRequestStatus.cancel) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미 취소된 신청입니다.");
        }

        removeClientParticipantsForRequest(request);
        request.updateStatus(MatchRequestStatus.cancel);
        restoreRequestsClosedByApproval(request.getFriendlyMatch().getId(), request.getId(), approvedAt);
    }

    // 친선 경기의 모집 상태를 현재 시간과 승인된 신청 여부 기준으로 동기화한다.
    // 시간이 지났거나 승인된 신청이 있으면 모집 마감, 아니면 모집 중으로 둔다.
    public void syncMatchState(FriendlyMatch friendlyMatch) {
        boolean shouldClose = isRecruitDeadlineClosed(friendlyMatch)
                || hasAnotherSelectedRequest(friendlyMatch.getId(), null);

        friendlyMatch.updateState(shouldClose ? FriendlyMatchStateVo.closed() : FriendlyMatchStateVo.open());
    }

    // 신청을 최종 승인 처리한다.
    // 신청자 본인과 신청서에 포함된 팀원을 실제 경기 참여자(client)로 등록한다.
    private void finalizeApprovedRequest(MatchRequest request) {
        FriendlyMatch friendlyMatch = request.getFriendlyMatch();
        Integer matchId = friendlyMatch.getId();

        Set<Integer> clientGameUserIds = new LinkedHashSet<>();
        clientGameUserIds.add(request.getGameUser().getId());
        matchRequestParticipationRepository.findByMatchRequestId(request.getId()).stream()
                .map(MatchRequestParticipation::getGameUser)
                .map(GameUser::getId)
                .forEach(clientGameUserIds::add);

        Set<Integer> existingClientIds = matchParticipationRepository
                .findByFriendlyMatchIdAndRole(matchId, MatchParticipationRole.client).stream()
                .map(MatchParticipation::getGameUser)
                .map(GameUser::getId)
                .collect(Collectors.toSet());

        for (Integer gameUserId : clientGameUserIds) {
            if (existingClientIds.contains(gameUserId)) {
                continue;
            }

            GameUser gameUser = gameUserRepository.findById(gameUserId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.GAME_USER_NOT_FOUND));
            matchParticipationRepository.save(MatchParticipation.create(
                    gameUser,
                    friendlyMatch,
                    MatchParticipationRole.client
            ));
        }

        request.updateStatus(MatchRequestStatus.approve);
    }

    // 승인 때문에 자동 reject 처리됐던 다른 신청들을 다시 await 상태로 복구한다.
    // approvedAt 근처에 변경된 reject 신청만 복구해서, 호스트가 직접 거절한 과거 신청까지 되살리지 않도록 한다.
    private void restoreRequestsClosedByApproval(
            Integer matchId,
            Integer selectedRequestId,
            LocalDateTime approvedAt
    ) {
        if (approvedAt == null) {
            return;
        }

        for (MatchRequest request : matchRequestRepository.findByFriendlyMatchIdOrderByIdDesc(matchId)) {
            if (!Objects.equals(request.getId(), selectedRequestId)
                    && request.getStatus() == MatchRequestStatus.reject
                    && wasClosedByApproval(request, approvedAt)) {
                request.updateStatus(MatchRequestStatus.await);
            }
        }
    }

    // 특정 reject 신청이 승인 처리 시점에 자동으로 닫힌 신청인지 판단한다.
    // 승인 시점과 1초 이내로 updatedAt이 가까우면 자동 reject로 본다.
    private boolean wasClosedByApproval(MatchRequest request, LocalDateTime approvedAt) {
        LocalDateTime updatedAt = request.getUpdatedAt();
        if (updatedAt == null) {
            return false;
        }

        return !updatedAt.isBefore(approvedAt.minusSeconds(1))
                && !updatedAt.isAfter(approvedAt.plusSeconds(1));
    }

    // 신청자와 신청 팀원에 해당하는 client 경기 참여자를 제거한다.
    // host 참여자는 삭제 대상이 아니고, client 역할만 삭제한다.
    private void removeClientParticipantsForRequest(MatchRequest request) {
        Integer matchId = request.getFriendlyMatch().getId();

        Set<Integer> requestMemberIds = new LinkedHashSet<>();
        requestMemberIds.add(request.getGameUser().getId());
        matchRequestParticipationRepository.findByMatchRequestId(request.getId()).stream()
                .map(MatchRequestParticipation::getGameUser)
                .map(GameUser::getId)
                .forEach(requestMemberIds::add);

        List<MatchParticipation> clientParticipations = matchParticipationRepository
                .findByFriendlyMatchIdAndRole(matchId, MatchParticipationRole.client).stream()
                .filter(participation -> requestMemberIds.contains(participation.getGameUser().getId()))
                .toList();

        if (!clientParticipations.isEmpty()) {
            matchParticipationRepository.deleteAll(clientParticipations);
        }
    }

    // 특정 친선 경기에서 이미 승인된 다른 신청이 있는지 확인한다.
    // excludedRequestId가 있으면 해당 신청은 검사 대상에서 제외한다.
    private boolean hasAnotherSelectedRequest(Integer matchId, Integer excludedRequestId) {
        return matchRequestRepository.findByFriendlyMatchId(matchId).stream()
                .filter(request -> !Objects.equals(request.getId(), excludedRequestId))
                .anyMatch(MatchRequest::isSelected);
    }

    // 친선 경기 예정 시간이 현재 시간보다 지났는지 확인한다.
    // 예정 시간이 현재보다 이후가 아니면 모집 마감으로 본다.
    private boolean isRecruitDeadlineClosed(FriendlyMatch friendlyMatch) {
        return !friendlyMatch.getScheduledAt().isAfter(LocalDateTime.now());
    }
}
