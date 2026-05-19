package com.example.game_match.matchrequest.repository;

import com.example.game_match.matchrequest.domain.MatchRequestParticipation;
import java.util.List;

public interface MatchRequestParticipationRepository {
    List<MatchRequestParticipation> findByMatchRequestId(Integer matchRequestId);

    List<MatchRequestParticipation> findByGameUserId(Integer gameUserId);

    void deleteByMatchRequestId(Integer matchRequestId);

    MatchRequestParticipation save(MatchRequestParticipation matchRequestParticipation);
}
