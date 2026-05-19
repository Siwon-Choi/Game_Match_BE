package com.example.game_match.friendlymatch.repository.jpa;

import com.example.game_match.friendlymatch.domain.FriendlyMatch;
import com.example.game_match.friendlymatch.domain.vo.FriendlyMatchStateVo;
import com.example.game_match.friendlymatch.repository.FriendlyMatchRepository;
import com.example.game_match.game.domain.Game;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FriendlyMatchRepositoryJpaAdapter implements FriendlyMatchRepository {
    private final FriendlyMatchJpaRepository friendlyMatchJpaRepository;

    @Override
    public Optional<FriendlyMatch> findById(Integer id) {
        return friendlyMatchJpaRepository.findWithRelationsById(id);
    }

    @Override
    public List<FriendlyMatch> findByDateAndTimeAndGame(LocalDate date, LocalTime time, Game game) {
        return friendlyMatchJpaRepository.findByDateAndTimeAndGame(date, time, game);
    }

    @Override
    public List<FriendlyMatch> findByHostIdOrderByDateDescTimeDesc(Integer hostId) {
        return friendlyMatchJpaRepository.findByHost_IdOrderByDateDescTimeDesc(hostId);
    }

    @Override
    public FriendlyMatch save(FriendlyMatch friendlyMatch) {
        return friendlyMatchJpaRepository.save(friendlyMatch);
    }

    @Override
    public void updateExpiredMatches(LocalDate nowDate, LocalTime nowTime) {
        friendlyMatchJpaRepository.updateExpiredMatches(nowDate, nowTime, FriendlyMatchStateVo.closed());
    }

    @Override
    public long countActiveRequestsByMatchId(Integer matchId) {
        return friendlyMatchJpaRepository.countActiveRequestsByMatchId(matchId);
    }

    @Override
    public long countPendingRequestsByMatchId(Integer matchId) {
        return friendlyMatchJpaRepository.countPendingRequestsByMatchId(matchId);
    }

    @Override
    public long countApprovedRequestsByMatchId(Integer matchId) {
        return friendlyMatchJpaRepository.countApprovedRequestsByMatchId(matchId);
    }

    @Override
    public Optional<LocalDateTime> findLatestRequestUpdatedAtByMatchId(Integer matchId) {
        return friendlyMatchJpaRepository.findLatestRequestUpdatedAtByMatchId(matchId);
    }
}
