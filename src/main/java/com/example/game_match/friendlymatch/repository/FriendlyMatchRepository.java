package com.example.game_match.friendlymatch.repository;

import com.example.game_match.friendlymatch.domain.FriendlyMatch;
import com.example.game_match.game.domain.Game;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface FriendlyMatchRepository {
    Optional<FriendlyMatch> findById(Integer id);

    List<FriendlyMatch> findByDateAndTimeAndGame(LocalDate date, LocalTime time, Game game);

    List<FriendlyMatch> findByHostIdOrderByDateDescTimeDesc(Integer hostId);

    FriendlyMatch save(FriendlyMatch friendlyMatch);

    void updateExpiredMatches(LocalDate nowDate, LocalTime nowTime);

    long countActiveRequestsByMatchId(Integer matchId);

    long countPendingRequestsByMatchId(Integer matchId);

    long countApprovedRequestsByMatchId(Integer matchId);

    Optional<LocalDateTime> findLatestRequestUpdatedAtByMatchId(Integer matchId);
}
