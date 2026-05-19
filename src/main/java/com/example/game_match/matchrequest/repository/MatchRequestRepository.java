package com.example.game_match.matchrequest.repository;

import com.example.game_match.matchrequest.domain.MatchRequest;
import java.util.List;
import java.util.Optional;

public interface MatchRequestRepository {
    Optional<MatchRequest> findById(Integer id);

    Optional<Integer> findFriendlyMatchIdByRequestId(Integer requestId);

    List<MatchRequest> findByFriendlyMatchId(Integer friendlyMatchId);

    List<MatchRequest> findByFriendlyMatchIdOrderByIdDesc(Integer friendlyMatchId);

    List<MatchRequest> findByGameUserIdOrderByIdDesc(Integer gameUserId);

    Optional<MatchRequest> findByGameUserIdAndFriendlyMatchId(Integer gameUserId, Integer friendlyMatchId);

    MatchRequest save(MatchRequest matchRequest);
}
