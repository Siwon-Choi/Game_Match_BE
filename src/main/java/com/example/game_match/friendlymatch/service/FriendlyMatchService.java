package com.example.game_match.friendlymatch.service;

import com.example.game_match.friendlymatch.domain.FriendlyMatch;
import com.example.game_match.friendlymatch.domain.vo.FriendlyMatchCommentVo;
import com.example.game_match.friendlymatch.domain.vo.FriendlyMatchRecruitVo;
import com.example.game_match.friendlymatch.domain.vo.FriendlyMatchSortVo;
import com.example.game_match.friendlymatch.domain.vo.FriendlyMatchStateVo;
import com.example.game_match.friendlymatch.dto.FriendlyMatchCreateDto;
import com.example.game_match.friendlymatch.dto.FriendlyMatchHostedSummaryResponseDto;
import com.example.game_match.friendlymatch.dto.FriendlyMatchResponseDto;
import com.example.game_match.friendlymatch.repository.FriendlyMatchRepository;
import com.example.game_match.game.domain.Game;
import com.example.game_match.game.domain.vo.GameNameVo;
import com.example.game_match.game.repository.GameRepository;
import com.example.game_match.gameuser.domain.GameUser;
import com.example.game_match.gameuser.repository.GameUserRepository;
import com.example.game_match.global.exception.BusinessException;
import com.example.game_match.global.exception.ErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendlyMatchService {
    private final FriendlyMatchRepository friendlyMatchRepository;
    private final GameUserRepository gameUserRepository;
    private final GameRepository gameRepository;

    // 친선 경기 모집글을 새로 생성한다.
    @Transactional
    public FriendlyMatchResponseDto createFriendlyMatch(FriendlyMatchCreateDto dto) {
        if (dto.hostId() == null) {
            throw new IllegalArgumentException("호스트 게임 프로필이 필요합니다.");
        }

        if (dto.gameId() == null) {
            throw new IllegalArgumentException("게임 정보가 필요합니다.");
        }

        GameUser host = gameUserRepository.findById(dto.hostId())
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_USER_NOT_FOUND));
        Game game = gameRepository.findById(dto.gameId())
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND));

        if (!Objects.equals(host.getGameId(), game.getId())) {
            throw new IllegalArgumentException("호스트 게임 프로필과 경기 게임이 일치하지 않습니다.");
        }

        FriendlyMatch friendlyMatch = FriendlyMatch.create(
                host,
                game,
                dto.date(),
                dto.time(),
                FriendlyMatchSortVo.from(dto.sort()),
                dto.state() == null ? FriendlyMatchStateVo.open() : FriendlyMatchStateVo.from(dto.state()),
                FriendlyMatchRecruitVo.from(dto.recruit()),
                FriendlyMatchCommentVo.from(dto.comment())
        );

        return FriendlyMatchResponseDto.from(friendlyMatchRepository.save(friendlyMatch));
    }

    // 날짜 목록, 시간 목록, 게임 이름을 기준으로 친선 경기 목록을 검색한다.
    @Transactional(readOnly = true)
    public List<FriendlyMatchResponseDto> searchByDatesTimesAndGameName(
            List<LocalDate> dates,
            List<String> times,
            String gameName
    ) {
        if (dates == null || times == null || dates.size() != times.size()) {
            throw new IllegalArgumentException("dates와 times는 같은 개수로 전달해야 합니다.");
        }

        Game game = gameRepository.findByName(GameNameVo.from(gameName))
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND));

        List<FriendlyMatch> result = new ArrayList<>();
        for (int i = 0; i < dates.size(); i++) {
            result.addAll(friendlyMatchRepository.findByDateAndTimeAndGame(
                    dates.get(i),
                    LocalTime.parse(times.get(i)),
                    game
            ));
        }

        return result.stream()
                .map(FriendlyMatchResponseDto::from)
                .toList();
    }

    // matchId로 친선 경기 상세 정보를 조회한 뒤 응답 DTO로 변환한다.
    // 내부적으로 findFriendlyMatchById에서 모집 상태를 최신 상태로 갱신한다.
    @Transactional
    public FriendlyMatchResponseDto getMatchById(Integer matchId) {
        return FriendlyMatchResponseDto.from(findFriendlyMatchById(matchId));
    }

    // 특정 게임 유저가 호스트로 생성한 친선 경기 목록을 조회한다.
    // 각 경기의 상태를 갱신한 뒤 신청 집계 정보를 포함한 요약 DTO로 변환한다.
    @Transactional
    public List<FriendlyMatchHostedSummaryResponseDto> getHostedMatches(Integer hostGameUserId) {
        return friendlyMatchRepository.findByHostIdOrderByDateDescTimeDesc(hostGameUserId).stream()
                .map(match -> {
                    refreshMatchState(match);
                    return toHostedSummary(match);
                })
                .toList();
    }



    // 현재 날짜/시간 기준으로 이미 지난 친선 경기의 모집 상태를 일괄 마감 처리한다.
    // 스케줄러에서 서버 시작 시와 매 정각마다 호출한다.
    @Transactional
    public void updateExpiredMatches() {
        LocalDateTime now = LocalDateTime.now();
        friendlyMatchRepository.updateExpiredMatches(now.toLocalDate(), now.toLocalTime());
    }


    // matchId로 FriendlyMatch 엔티티를 조회한다.
    // 없으면 비즈니스 예외를 발생시키고, 있으면 모집 상태를 최신 상태로 갱신한 뒤 반환한다.
    @Transactional
    public FriendlyMatch findFriendlyMatchById(Integer matchId) {

        FriendlyMatch friendlyMatch = friendlyMatchRepository.findById(matchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIENDLY_MATCH_NOT_FOUND));

        refreshMatchState(friendlyMatch);

        return friendlyMatch;
    }

    // matchId로 FriendlyMatch 엔티티를 조회한다.
    @Transactional
    public FriendlyMatch findById(Integer matchId) {
        return friendlyMatchRepository.findById(matchId)
                .map(match -> {
                    refreshMatchState(match);
                    return match;
                })
                .orElse(null);
    }


    // 친선 경기 ID로 연결된 게임 ID만 조회한다.
    // 다른 매칭/참여 로직에서 같은 게임인지 검증할 때 사용할 수 있다.
    @Transactional
    public Integer getGameId(Integer matchId) {
        return findFriendlyMatchById(matchId).getGame().getId();
    }


    // 호스트가 만든 친선 경기 목록 화면에 필요한 요약 정보를 만든다.
    // 경기 기본 정보와 신청 상태별 집계, 최근 신청 변경 시각을 함께 담는다.
    private FriendlyMatchHostedSummaryResponseDto toHostedSummary(FriendlyMatch friendlyMatch) {
        Integer matchId = friendlyMatch.getId();

        return new FriendlyMatchHostedSummaryResponseDto(
                matchId,
                friendlyMatch.getGame().getName(),
                friendlyMatch.getDate(),
                friendlyMatch.getTime(),
                friendlyMatch.getSort(),
                friendlyMatch.getState(),
                friendlyMatch.getRecruit(),
                friendlyMatch.getComment(),
                friendlyMatchRepository.countActiveRequestsByMatchId(matchId),
                friendlyMatchRepository.countPendingRequestsByMatchId(matchId),
                friendlyMatchRepository.countApprovedRequestsByMatchId(matchId),
                friendlyMatchRepository.findLatestRequestUpdatedAtByMatchId(matchId).orElse(null)
        );
    }


    // 경기 시간이 지났거나 승인된 신청이 있으면 모집 마감 상태로 바꾼다.
    // 현재 상태와 계산된 상태가 다를 때만 엔티티를 수정하고 저장한다.
    private void refreshMatchState(FriendlyMatch friendlyMatch) {
        FriendlyMatchStateVo nextState = shouldCloseRecruitment(friendlyMatch)
                ? FriendlyMatchStateVo.closed()
                : FriendlyMatchStateVo.open();

        if (!Objects.equals(friendlyMatch.getState(), nextState.getValue())) {
            friendlyMatch.updateState(nextState);
            friendlyMatchRepository.save(friendlyMatch);
        }
    }


    // 친선 경기 모집을 마감해야 하는지 판단한다.
    // 경기 시간이 현재보다 이전이거나, 승인된 신청이 하나라도 있으면 마감으로 본다.
    private boolean shouldCloseRecruitment(FriendlyMatch friendlyMatch) {
        if (!friendlyMatch.getScheduledAt().isAfter(LocalDateTime.now())) {
            return true;
        }

        return friendlyMatchRepository.countApprovedRequestsByMatchId(friendlyMatch.getId()) > 0;
    }
}

