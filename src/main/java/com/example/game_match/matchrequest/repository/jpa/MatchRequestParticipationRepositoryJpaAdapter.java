package com.example.game_match.matchrequest.repository.jpa;

import com.example.game_match.matchrequest.domain.MatchRequestParticipation;
import com.example.game_match.matchrequest.repository.MatchRequestParticipationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MatchRequestParticipationRepositoryJpaAdapter implements MatchRequestParticipationRepository {
    private final MatchRequestParticipationJpaRepository matchRequestParticipationJpaRepository;

    @Override
    public List<MatchRequestParticipation> findByMatchRequestId(Integer matchRequestId) {
        return matchRequestParticipationJpaRepository.findByMatchRequest_Id(matchRequestId);
    }

    @Override
    public List<MatchRequestParticipation> findByGameUserId(Integer gameUserId) {
        return matchRequestParticipationJpaRepository.findByGameUser_Id(gameUserId);
    }

    @Override
    public void deleteByMatchRequestId(Integer matchRequestId) {
        matchRequestParticipationJpaRepository.deleteByMatchRequest_Id(matchRequestId);
    }

    @Override
    public MatchRequestParticipation save(MatchRequestParticipation matchRequestParticipation) {
        return matchRequestParticipationJpaRepository.save(matchRequestParticipation);
    }
}
