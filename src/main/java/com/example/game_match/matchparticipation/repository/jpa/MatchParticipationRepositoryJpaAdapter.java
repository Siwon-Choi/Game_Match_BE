package com.example.game_match.matchparticipation.repository.jpa;

import com.example.game_match.matchparticipation.domain.MatchParticipation;
import com.example.game_match.matchparticipation.domain.vo.MatchParticipationRole;
import com.example.game_match.matchparticipation.repository.MatchParticipationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MatchParticipationRepositoryJpaAdapter implements MatchParticipationRepository {
    private final MatchParticipationJpaRepository matchParticipationJpaRepository;

    @Override
    public List<MatchParticipation> findByFriendlyMatchId(Integer friendlyMatchId) {
        return matchParticipationJpaRepository.findByFriendlyMatch_Id(friendlyMatchId);
    }

    @Override
    public List<MatchParticipation> findByGameUserId(Integer gameUserId) {
        return matchParticipationJpaRepository.findByGameUser_Id(gameUserId);
    }

    @Override
    public List<MatchParticipation> findByFriendlyMatchIdAndRole(
            Integer friendlyMatchId,
            MatchParticipationRole role
    ) {
        return matchParticipationJpaRepository.findByFriendlyMatch_IdAndRole(friendlyMatchId, role);
    }

    @Override
    public boolean existsByGameUserIdAndFriendlyMatchId(Integer gameUserId, Integer friendlyMatchId) {
        return matchParticipationJpaRepository.existsByGameUser_IdAndFriendlyMatch_Id(gameUserId, friendlyMatchId);
    }

    @Override
    public MatchParticipation save(MatchParticipation matchParticipation) {
        return matchParticipationJpaRepository.save(matchParticipation);
    }

    @Override
    public void deleteAll(List<MatchParticipation> matchParticipations) {
        matchParticipationJpaRepository.deleteAll(matchParticipations);
    }
}
