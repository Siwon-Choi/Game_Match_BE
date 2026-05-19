package com.example.game_match.matchrequest.repository.jpa;

import com.example.game_match.matchrequest.domain.MatchRequest;
import com.example.game_match.matchrequest.repository.MatchRequestRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MatchRequestRepositoryJpaAdapter implements MatchRequestRepository {
    private final MatchRequestJpaRepository matchRequestJpaRepository;

    @Override
    public Optional<MatchRequest> findById(Integer id) {
        return matchRequestJpaRepository.findById(id);
    }

    @Override
    public Optional<Integer> findFriendlyMatchIdByRequestId(Integer requestId) {
        return matchRequestJpaRepository.findFriendlyMatchIdByRequestId(requestId);
    }

    @Override
    public List<MatchRequest> findByFriendlyMatchId(Integer friendlyMatchId) {
        return matchRequestJpaRepository.findByFriendlyMatch_Id(friendlyMatchId);
    }

    @Override
    public List<MatchRequest> findByFriendlyMatchIdOrderByIdDesc(Integer friendlyMatchId) {
        return matchRequestJpaRepository.findByFriendlyMatch_IdOrderByIdDesc(friendlyMatchId);
    }

    @Override
    public List<MatchRequest> findByGameUserIdOrderByIdDesc(Integer gameUserId) {
        return matchRequestJpaRepository.findByGameUser_IdOrderByIdDesc(gameUserId);
    }

    @Override
    public Optional<MatchRequest> findByGameUserIdAndFriendlyMatchId(Integer gameUserId, Integer friendlyMatchId) {
        return matchRequestJpaRepository.findByGameUser_IdAndFriendlyMatch_Id(gameUserId, friendlyMatchId);
    }

    @Override
    public MatchRequest save(MatchRequest matchRequest) {
        return matchRequestJpaRepository.save(matchRequest);
    }
}
