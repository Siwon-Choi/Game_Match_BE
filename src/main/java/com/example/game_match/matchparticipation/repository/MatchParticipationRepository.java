package com.example.game_match.matchparticipation.repository;

import com.example.game_match.matchparticipation.domain.MatchParticipation;
import com.example.game_match.matchparticipation.domain.vo.MatchParticipationRole;
import java.util.List;

public interface MatchParticipationRepository {
    List<MatchParticipation> findByFriendlyMatchId(Integer friendlyMatchId);

    List<MatchParticipation> findByGameUserId(Integer gameUserId);

    List<MatchParticipation> findByFriendlyMatchIdAndRole(Integer friendlyMatchId, MatchParticipationRole role);

    boolean existsByGameUserIdAndFriendlyMatchId(Integer gameUserId, Integer friendlyMatchId);

    MatchParticipation save(MatchParticipation matchParticipation);

    void deleteAll(List<MatchParticipation> matchParticipations);
}
