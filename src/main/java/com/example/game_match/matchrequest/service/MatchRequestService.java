package com.example.game_match.matchrequest.service;

import com.example.game_match.friendlymatch.domain.FriendlyMatch;
import com.example.game_match.friendlymatch.repository.FriendlyMatchRepository;
import com.example.game_match.gameuser.domain.GameUser;
import com.example.game_match.gameuser.repository.GameUserRepository;
import com.example.game_match.global.exception.BusinessException;
import com.example.game_match.global.exception.ErrorCode;
import com.example.game_match.matchrequest.domain.MatchRequest;
import com.example.game_match.matchrequest.domain.MatchRequestParticipation;
import com.example.game_match.matchrequest.domain.vo.MatchRequestStatus;
import com.example.game_match.matchrequest.dto.MatchRequestAppliedSummaryResponseDto;
import com.example.game_match.matchrequest.dto.MatchRequestApplyDto;
import com.example.game_match.matchrequest.dto.MatchRequestDetailResponseDto;
import com.example.game_match.matchrequest.dto.MatchRequestMemberResponseDto;
import com.example.game_match.matchrequest.repository.MatchRequestParticipationRepository;
import com.example.game_match.matchrequest.repository.MatchRequestRepository;
import com.example.game_match.matchrequest.service.status.MatchRequestStatusHandler;
import com.example.game_match.matchrequest.service.status.MatchRequestStatusOperations;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchRequestService {
    private final MatchRequestRepository matchRequestRepository;
    private final MatchRequestParticipationRepository matchRequestParticipationRepository;
    private final FriendlyMatchRepository friendlyMatchRepository;
    private final GameUserRepository gameUserRepository;
    private final MatchRequestStatusOperations statusOperations;
    private final List<MatchRequestStatusHandler> statusHandlers;

    // requestId로 친선 경기 신청 엔티티를 조회한다.
    @Transactional(readOnly = true)
    public MatchRequest findMatchRequestById(Integer requestId) {
        return matchRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MATCH_REQUEST_NOT_FOUND));
    }

    // requestId에 연결된 친선 경기 ID를 조회한다.
    @Transactional(readOnly = true)
    public Integer getFriendlyMatchIdByRequestId(Integer requestId) {
        return matchRequestRepository.findFriendlyMatchIdByRequestId(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MATCH_REQUEST_NOT_FOUND));
    }

    // 특정 친선 경기의 활성 신청 개수를 계산한다.
    // await 또는 approve 상태인 신청만 활성 신청으로 본다.
    @Transactional(readOnly = true)
    public long countActiveRequestsByMatchId(Integer matchId) {
        return matchRequestRepository.findByFriendlyMatchId(matchId).stream()
                .filter(MatchRequest::isActive)
                .count();
    }

    // requestId로 현재 신청 상태만 조회한다.
    @Transactional(readOnly = true)
    public MatchRequestStatus getRequestStatusByRequestId(Integer requestId) {
        return findMatchRequestById(requestId).getStatus();
    }

    // 친선 경기 신청 상세 정보를 조회한다.
    // 조회 전에 경기 모집 상태를 현재 시간과 승인 여부 기준으로 동기화한 뒤 DTO로 변환한다.
    @Transactional
    public MatchRequestDetailResponseDto getRequestDetail(Integer requestId) {
        MatchRequest request = findMatchRequestById(requestId);
        statusOperations.syncMatchState(request.getFriendlyMatch());
        return toDetailDto(request);
    }

    // 특정 친선 경기에 들어온 신청 목록을 조회한다.
    // hostGameUserId가 해당 경기의 호스트인지 검증한 뒤, 신청 목록을 최신순으로 반환한다.
    @Transactional
    public List<MatchRequestDetailResponseDto> getRequestsByMatchId(Integer matchId, Integer hostGameUserId) {
        FriendlyMatch friendlyMatch = friendlyMatchRepository.findById(matchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIENDLY_MATCH_NOT_FOUND));
        validateHostAuthority(friendlyMatch, hostGameUserId);
        statusOperations.syncMatchState(friendlyMatch);

        return matchRequestRepository.findByFriendlyMatchIdOrderByIdDesc(matchId).stream()
                .map(this::toDetailDto)
                .toList();
    }

    // 특정 게임 유저가 보낸 친선 경기 신청 목록을 조회한다.
    // 각 신청의 경기 상태를 동기화한 뒤, 내 신청 목록 화면용 요약 DTO로 변환한다.
    @Transactional
    public List<MatchRequestAppliedSummaryResponseDto> getRequestsByGameUserId(Integer gameUserId) {
        return matchRequestRepository.findByGameUserIdOrderByIdDesc(gameUserId).stream()
                .map(request -> {
                    statusOperations.syncMatchState(request.getFriendlyMatch());
                    return toAppliedSummaryDto(request);
                })
                .toList();
    }

    // 호스트가 신청 상태를 변경한다.
    // 호스트는 approve 또는 reject만 처리할 수 있고, 실제 상태 변경은 Handler 전략에 위임한다.
    @Transactional
    public MatchRequestDetailResponseDto updateRequestStatus(
            Integer requestId,
            Integer hostGameUserId,
            MatchRequestStatus status
    ) {
        if (status == MatchRequestStatus.await || status == MatchRequestStatus.cancel) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "호스트는 해당 상태로 변경할 수 없습니다.");
        }

        MatchRequest request = findMatchRequestById(requestId);
        validateHostAuthority(request.getFriendlyMatch(), hostGameUserId);
        statusOperations.syncMatchState(request.getFriendlyMatch());

        findStatusHandler(request, status).handle(request, statusOperations);

        statusOperations.syncMatchState(request.getFriendlyMatch());
        return toDetailDto(request);
    }

    // 신청자가 본인의 친선 경기 신청을 취소한다.
    // 신청자 권한을 확인한 뒤 cancel 상태 변경 Handler를 통해 처리한다.
    @Transactional
    public MatchRequestDetailResponseDto cancelRequest(Integer requestId, Integer requesterGameUserId) {
        MatchRequest request = findMatchRequestById(requestId);
        validateRequesterAuthority(request, requesterGameUserId);

        findStatusHandler(request, MatchRequestStatus.cancel).handle(request, statusOperations);
        statusOperations.syncMatchState(request.getFriendlyMatch());
        return toDetailDto(request);
    }

    // 친선 경기 신청을 생성하거나, 과거 취소/거절된 신청을 다시 대기 상태로 되살린다.
    // 신청 팀원 목록은 기존 데이터를 삭제한 뒤 새 요청 기준으로 다시 저장한다.
    @Transactional
    public Integer applyMatchAndRegisterTeam(MatchRequestApplyDto dto) {
        if (dto.gameUserId() == null) {
            throw new IllegalArgumentException("게임 유저 프로필이 필요합니다.");
        }

        if (dto.matchId() == null) {
            throw new IllegalArgumentException("친선 경기 정보가 필요합니다.");
        }

        GameUser gameUser = gameUserRepository.findById(dto.gameUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_USER_NOT_FOUND));
        FriendlyMatch friendlyMatch = friendlyMatchRepository.findById(dto.matchId())
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIENDLY_MATCH_NOT_FOUND));

        statusOperations.syncMatchState(friendlyMatch);
        if (Objects.equals(friendlyMatch.getState(), (byte) 1)) {
            throw new BusinessException(ErrorCode.MATCH_REQUEST_CLOSED);
        }

        MatchRequest savedRequest = matchRequestRepository
                .findByGameUserIdAndFriendlyMatchId(gameUser.getId(), friendlyMatch.getId())
                .map(existingRequest -> resetCancelledOrRejectedRequest(existingRequest, dto.comment()))
                .orElseGet(() -> createNewRequest(gameUser, friendlyMatch, dto.comment()));

        matchRequestParticipationRepository.deleteByMatchRequestId(savedRequest.getId());
        Set<Integer> teamMemberIds = new LinkedHashSet<>(dto.teamMemberIds() == null ? List.of() : dto.teamMemberIds());
        for (Integer memberId : teamMemberIds) {
            matchRequestParticipationRepository.save(createParticipation(memberId, savedRequest));
        }

        return savedRequest.getId();
    }

    // 특정 사용자가 현재 친선 경기에 신청 가능한지 확인한다.
    // 사용자가 가진 모든 게임 프로필을 기준으로 같은 경기 신청 또는 같은 시간대 신청이 있는지 검사한다.
    @Transactional(readOnly = true)
    public String checkRequestParticipationConflict(Integer userId, Integer currentFriendlyMatchId) {
        FriendlyMatch currentFriendlyMatch = friendlyMatchRepository.findById(currentFriendlyMatchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIENDLY_MATCH_NOT_FOUND));

        for (GameUser gameUser : gameUserRepository.findByUserId(userId)) {
            List<MatchRequest> activeRequests = activeRequestsForGameUser(gameUser.getId());

            if (hasSameMatch(activeRequests, currentFriendlyMatchId)) {
                throw new BusinessException(ErrorCode.DUPLICATE_MATCH_REQUEST);
            }

            if (hasSameSchedule(activeRequests, currentFriendlyMatch)) {
                throw new BusinessException(ErrorCode.MATCH_REQUEST_SCHEDULE_CONFLICT);
            }
        }

        return "User can request participation.";
    }




    // 새 친선 경기 신청 엔티티를 생성하고 저장한다.
    // 기본 상태는 await이다.
    private MatchRequest createNewRequest(GameUser gameUser, FriendlyMatch friendlyMatch, String comment) {
        return matchRequestRepository.save(MatchRequest.create(gameUser, friendlyMatch, comment));
    }

    // 이전에 cancel 또는 reject 상태였던 신청을 다시 await 상태로 되살린다.
    // 이미 await/approve 상태라면 중복 신청으로 보고 예외를 발생시킨다.
    private MatchRequest resetCancelledOrRejectedRequest(MatchRequest request, String comment) {
        if (request.isActive()) {
            throw new BusinessException(ErrorCode.DUPLICATE_MATCH_REQUEST);
        }

        request.updateComment(comment);
        request.updateStatus(MatchRequestStatus.await);
        return matchRequestRepository.save(request);
    }

    // 신청서에 포함될 팀원 참여 엔티티를 생성한다.
    // memberId로 GameUser를 조회한 뒤 MatchRequestParticipation으로 묶는다.
    private MatchRequestParticipation createParticipation(Integer memberId, MatchRequest savedRequest) {
        GameUser teamMember = gameUserRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_USER_NOT_FOUND));

        return MatchRequestParticipation.create(savedRequest, teamMember);
    }

    // 현재 신청 상태와 목표 상태를 처리할 수 있는 Handler를 찾는다.
    // 예를 들어 await -> reject와 approve -> reject는 처리 방식이 달라 서로 다른 Handler가 담당한다.
    private MatchRequestStatusHandler findStatusHandler(MatchRequest request, MatchRequestStatus targetStatus) {
        return statusHandlers.stream()
                .filter(handler -> handler.supports(request, targetStatus))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INVALID_REQUEST,
                        "처리할 수 없는 신청 상태 변경입니다."
                ));
    }

    // 내가 신청한 경기 목록 화면에 필요한 요약 응답 DTO로 변환한다.
    private MatchRequestAppliedSummaryResponseDto toAppliedSummaryDto(MatchRequest request) {
        FriendlyMatch match = request.getFriendlyMatch();

        return new MatchRequestAppliedSummaryResponseDto(
                request.getId(),
                match.getId(),
                match.getGame().getName(),
                match.getHost().getNickname(),
                match.getDate(),
                match.getTime(),
                match.getSort(),
                match.getState(),
                match.getRecruit(),
                request.getComment(),
                request.getStatus(),
                request.getUpdatedAt()
        );
    }

    // 신청 상세/관리 화면에 필요한 응답 DTO로 변환한다.
    // 신청자 본인과 신청서에 포함된 팀원을 중복 없이 teamMembers에 담는다.
    private MatchRequestDetailResponseDto toDetailDto(MatchRequest request) {
        FriendlyMatch match = request.getFriendlyMatch();
        GameUser requester = request.getGameUser();
        Map<Integer, MatchRequestMemberResponseDto> members = new LinkedHashMap<>();
        members.put(requester.getId(), MatchRequestMemberResponseDto.from(requester));
        matchRequestParticipationRepository.findByMatchRequestId(request.getId()).stream()
                .map(MatchRequestParticipation::getGameUser)
                .forEach(gameUser -> members.putIfAbsent(gameUser.getId(), MatchRequestMemberResponseDto.from(gameUser)));

        return new MatchRequestDetailResponseDto(
                request.getId(),
                match.getId(),
                match.getGame().getName(),
                match.getHost().getNickname(),
                requester.getId(),
                requester.getNickname(),
                requester.getGroupId(),
                requester.getGroup() == null ? null : requester.getGroup().getName(),
                request.getComment(),
                request.getStatus(),
                match.getDate(),
                match.getTime(),
                match.getSort(),
                match.getState(),
                match.getRecruit(),
                List.copyOf(members.values()),
                request.getUpdatedAt()
        );
    }

    // 특정 게임 유저가 신청자이거나 팀원으로 포함된 활성 신청 목록을 조회한다.
    // 같은 신청이 중복으로 잡히지 않도록 requestId 기준 Map으로 합친다.
    private List<MatchRequest> activeRequestsForGameUser(Integer gameUserId) {
        Map<Integer, MatchRequest> requests = new LinkedHashMap<>();

        matchRequestRepository.findByGameUserIdOrderByIdDesc(gameUserId)
                .forEach(request -> requests.put(request.getId(), request));
        matchRequestParticipationRepository.findByGameUserId(gameUserId).stream()
                .map(MatchRequestParticipation::getMatchRequest)
                .forEach(request -> requests.put(request.getId(), request));

        return requests.values().stream()
                .filter(MatchRequest::isActive)
                .toList();
    }

    // 활성 신청 목록 중 현재 친선 경기와 같은 경기 신청이 있는지 확인한다.
    private boolean hasSameMatch(List<MatchRequest> activeRequests, Integer currentFriendlyMatchId) {
        return activeRequests.stream()
                .anyMatch(request -> Objects.equals(request.getFriendlyMatchId(), currentFriendlyMatchId));
    }

    // 활성 신청 목록 중 현재 친선 경기와 날짜/시간이 같은 신청이 있는지 확인한다.
    private boolean hasSameSchedule(List<MatchRequest> activeRequests, FriendlyMatch currentFriendlyMatch) {
        return activeRequests.stream()
                .map(MatchRequest::getFriendlyMatch)
                .anyMatch(friendlyMatch -> friendlyMatch.getDate().equals(currentFriendlyMatch.getDate())
                        && friendlyMatch.getTime().equals(currentFriendlyMatch.getTime()));
    }

    // 요청한 게임 유저가 해당 친선 경기의 호스트인지 검증한다.
    // 호스트가 아니면 FORBIDDEN 예외를 발생시킨다.
    private void validateHostAuthority(FriendlyMatch friendlyMatch, Integer hostGameUserId) {
        if (!Objects.equals(friendlyMatch.getHost().getId(), hostGameUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "해당 경기를 관리할 권한이 없습니다.");
        }
    }

    // 요청한 게임 유저가 해당 신청의 신청자인지 검증한다.
    // 신청자가 아니면 FORBIDDEN 예외를 발생시킨다.
    private void validateRequesterAuthority(MatchRequest request, Integer requesterGameUserId) {
        if (!Objects.equals(request.getGameUser().getId(), requesterGameUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "해당 신청을 관리할 권한이 없습니다.");
        }
    }
}