package com.example.game_match.matchparticipation.service;

import com.example.game_match.friendlymatch.domain.FriendlyMatch;
import com.example.game_match.friendlymatch.repository.FriendlyMatchRepository;
import com.example.game_match.gameuser.domain.GameUser;
import com.example.game_match.gameuser.repository.GameUserRepository;
import com.example.game_match.global.exception.BusinessException;
import com.example.game_match.global.exception.ErrorCode;
import com.example.game_match.matchparticipation.domain.MatchParticipation;
import com.example.game_match.matchparticipation.domain.vo.MatchParticipationRole;
import com.example.game_match.matchparticipation.dto.MatchParticipationCreateDto;
import com.example.game_match.matchparticipation.dto.MatchParticipationResponseDto;
import com.example.game_match.matchparticipation.repository.MatchParticipationRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchParticipationService {
    private final MatchParticipationRepository matchParticipationRepository;
    private final FriendlyMatchRepository friendlyMatchRepository;
    private final GameUserRepository gameUserRepository;

    // 특정 친선 경기에 참여자를 등록한다.
    // gameUserId, friendlyMatchId, role을 검증한 뒤 MatchParticipation 엔티티를 생성해 저장한다.
    @Transactional
    public MatchParticipationResponseDto createMatchParticipation(MatchParticipationCreateDto dto) {
        if (dto.gameUserId() == null) {
            throw new IllegalArgumentException("게임 유저 프로필이 필요합니다.");
        }

        if (dto.friendlyMatchId() == null) {
            throw new IllegalArgumentException("친선 경기 정보가 필요합니다.");
        }

        if (dto.role() == null) {
            throw new IllegalArgumentException("참여 역할은 필수입니다.");
        }

        GameUser gameUser = gameUserRepository.findById(dto.gameUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_USER_NOT_FOUND));
        FriendlyMatch friendlyMatch = friendlyMatchRepository.findById(dto.friendlyMatchId())
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIENDLY_MATCH_NOT_FOUND));

        if (!Objects.equals(gameUser.getGameId(), friendlyMatch.getGame().getId())) {
            throw new IllegalArgumentException("게임 유저 프로필과 친선 경기의 게임이 일치하지 않습니다.");
        }

        if (matchParticipationRepository.existsByGameUserIdAndFriendlyMatchId(
                gameUser.getId(),
                friendlyMatch.getId()
        )) {
            throw new BusinessException(ErrorCode.DUPLICATE_MATCH_PARTICIPATION);
        }

        MatchParticipation participation = MatchParticipation.create(gameUser, friendlyMatch, dto.role());
        return MatchParticipationResponseDto.from(matchParticipationRepository.save(participation));
    }


    // 특정 친선 경기의 참여자 목록을 조회한다.
    // 승인된 신청이 있으면 전체 참여자 목록을 반환하고, 아직 승인된 신청이 없으면 host만 보여준다.
    @Transactional(readOnly = true)
    public List<MatchParticipationResponseDto> getParticipationsByMatchId(Integer friendlyMatchId) {
        boolean hasSelectedRequest = friendlyMatchRepository.countApprovedRequestsByMatchId(friendlyMatchId) > 0;
        List<MatchParticipation> participations = matchParticipationRepository.findByFriendlyMatchId(friendlyMatchId);

        if (hasSelectedRequest) {
            return participations.stream()
                    .map(MatchParticipationResponseDto::from)
                    .toList();
        }

        return participations.stream()
                .filter(participation -> participation.getRole() == MatchParticipationRole.host)
                .map(MatchParticipationResponseDto::from)
                .toList();
    }

    // 특정 친선 경기의 참여자 목록에서 GameUser 엔티티만 추출한다.
    // 다른 매칭/신청 로직에서 참여자 GameUser 목록이 필요할 때 사용한다.
    @Transactional(readOnly = true)
    public List<GameUser> getGameUsersByMatchId(Integer friendlyMatchId) {
        return matchParticipationRepository.findByFriendlyMatchId(friendlyMatchId).stream()
                .map(MatchParticipation::getGameUser)
                .toList();
    }

    // 특정 친선 경기의 host 참여자들이 모두 같은 그룹에 속하는지 확인한다.
    // 모두 같은 그룹이면 그룹명을 반환하고, 아니면 null을 반환한다.
    @Transactional(readOnly = true)
    public String getHostGroupName(Integer friendlyMatchId) {
        List<MatchParticipation> hostParticipations = matchParticipationRepository.findByFriendlyMatchIdAndRole(
                friendlyMatchId,
                MatchParticipationRole.host
        );

        if (hostParticipations.isEmpty()) {
            return null;
        }

        Integer firstGroupId = hostParticipations.get(0).getGameUser().getGroupId();
        if (firstGroupId == null) {
            return null;
        }

        String firstGroupName = hostParticipations.get(0).getGameUser().getGroup().getName();
        boolean allSameGroup = hostParticipations.stream()
                .allMatch(participation -> Objects.equals(participation.getGameUser().getGroupId(), firstGroupId));

        return allSameGroup ? firstGroupName : null;
    }

    // 특정 GameUser가 참여자로 등록된 친선 경기 참여 목록을 조회한다.
    @Transactional(readOnly = true)
    public List<MatchParticipation> getParticipationsByGameUserId(Integer gameUserId) {
        return matchParticipationRepository.findByGameUserId(gameUserId);
    }

    // 특정 사용자를 현재 친선 경기 roster에 추가할 수 있는지 검사한다.
    // 사용자는 여러 GameUser 프로필을 가질 수 있으므로, 해당 사용자의 모든 GameUser 기준으로 충돌을 확인한다.
    @Transactional(readOnly = true)
    public String checkRosterConflict(Integer userId, Integer currentFriendlyMatchId) {
        FriendlyMatch currentFriendlyMatch = friendlyMatchRepository.findById(currentFriendlyMatchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIENDLY_MATCH_NOT_FOUND));

        for (GameUser gameUser : gameUserRepository.findByUserId(userId)) {
            List<MatchParticipation> participations = getParticipationsByGameUserId(gameUser.getId());

            if (hasSameMatch(participations, currentFriendlyMatchId)) {
                throw new BusinessException(ErrorCode.DUPLICATE_MATCH_PARTICIPATION);
            }

            if (hasSameSchedule(participations, currentFriendlyMatch)) {
                throw new BusinessException(ErrorCode.MATCH_SCHEDULE_CONFLICT);
            }
        }

        return "User can be added to the roster.";
    }

    // 기존 참여 목록에 현재 친선 경기 ID가 이미 포함되어 있는지 확인한다.
    private boolean hasSameMatch(List<MatchParticipation> participations, Integer currentFriendlyMatchId) {
        return participations.stream()
                .anyMatch(participation -> Objects.equals(
                        participation.getFriendlyMatchId(),
                        currentFriendlyMatchId
                ));
    }

    // 기존 참여 경기 중 현재 경기와 날짜와 시간이 모두 같은 일정이 있는지 확인한다.
    private boolean hasSameSchedule(
            List<MatchParticipation> participations,
            FriendlyMatch currentFriendlyMatch
    ) {
        return participations.stream()
                .map(MatchParticipation::getFriendlyMatch)
                .anyMatch(friendlyMatch -> friendlyMatch.getDate().equals(currentFriendlyMatch.getDate())
                        && friendlyMatch.getTime().equals(currentFriendlyMatch.getTime()));
    }
}
