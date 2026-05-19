package com.example.game_match.friendlymatch.repository.jpa;

import com.example.game_match.friendlymatch.domain.FriendlyMatch;
import com.example.game_match.friendlymatch.domain.vo.FriendlyMatchStateVo;
import com.example.game_match.game.domain.Game;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendlyMatchJpaRepository extends JpaRepository<FriendlyMatch, Integer> {

    // matchId로 친선 경기 단건을 조회한다.
    // 응답 DTO 변환에 필요한 host, host.user, host.game, host.group, game을 EntityGraph로 함께 조회한다.
    @EntityGraph(attributePaths = {"host", "host.user", "host.game", "host.group", "game"})
    @Query("select fm from FriendlyMatch fm where fm.id = :id")
    Optional<FriendlyMatch> findWithRelationsById(@Param("id") Integer id);


    // 날짜, 시간, 게임이 모두 일치하는 친선 경기 목록을 조회한다.
    // 검색 결과를 바로 응답 DTO로 변환하므로 필요한 연관 엔티티를 EntityGraph로 함께 조회한다.
    @EntityGraph(attributePaths = {"host", "host.user", "host.game", "host.group", "game"})
    @Query("""
            select fm
            from FriendlyMatch fm
            where fm.date = :date
              and fm.time = :time
              and fm.game = :game
            order by fm.id asc
            """)
    List<FriendlyMatch> findByDateAndTimeAndGame(
            @Param("date") LocalDate date,
            @Param("time") LocalTime time,
            @Param("game") Game game
    );


    // 특정 게임 유저가 호스트로 만든 친선 경기 목록을 최신 일정 순으로 조회한다.
    // 목록 화면에서 gameName, host 정보 등을 사용하므로 연관 엔티티를 함께 조회한다.
    @EntityGraph(attributePaths = {"host", "host.user", "host.game", "host.group", "game"})
    List<FriendlyMatch> findByHost_IdOrderByDateDescTimeDesc(Integer hostId);


    // 현재 날짜/시간보다 지난 친선 경기의 모집 상태를 일괄 마감 처리한다.
    // 벌크 업데이트이므로 @Modifying을 붙여 select가 아니라 update 쿼리임을 알려준다.
    @Modifying
    @Query("""
            update FriendlyMatch fm
            set fm.state = :closedState
            where fm.date < :nowDate
               or (fm.date = :nowDate and fm.time <= :nowTime)
            """)
    void updateExpiredMatches(
            @Param("nowDate") LocalDate nowDate,
            @Param("nowTime") LocalTime nowTime,
            @Param("closedState") FriendlyMatchStateVo closedState
    );


    // 특정 친선 경기의 활성 신청 수를 계산한다.
    // reject/cancel이 아닌 신청을 활성 신청으로 본다.
    @Query(value = """
            select count(*)
            from friendly_match_request
            where Friendly_Match_Id = :matchId
              and status not in ('reject', 'cancel')
            """, nativeQuery = true)
    long countActiveRequestsByMatchId(@Param("matchId") Integer matchId);


    // 특정 친선 경기의 대기 중 신청 수를 계산한다.
    // status가 await인 신청만 센다.
    @Query(value = """
            select count(*)
            from friendly_match_request
            where Friendly_Match_Id = :matchId
              and status = 'await'
            """, nativeQuery = true)
    long countPendingRequestsByMatchId(@Param("matchId") Integer matchId);


    // 특정 친선 경기의 승인된 신청 수를 계산한다.
    // status가 approve인 신청만 센다.
    @Query(value = """
            select count(*)
            from friendly_match_request
            where Friendly_Match_Id = :matchId
              and status = 'approve'
            """, nativeQuery = true)
    long countApprovedRequestsByMatchId(@Param("matchId") Integer matchId);


    // 특정 친선 경기 신청들 중 가장 최근에 변경된 시각을 조회한다.
    // 호스트의 내가 만든 경기 목록에서 최신 신청 여부를 표시할 때 사용한다.
    @Query(value = """
            select max(Updated_At)
            from friendly_match_request
            where Friendly_Match_Id = :matchId
            """, nativeQuery = true)
    Optional<LocalDateTime> findLatestRequestUpdatedAtByMatchId(@Param("matchId") Integer matchId);
}

