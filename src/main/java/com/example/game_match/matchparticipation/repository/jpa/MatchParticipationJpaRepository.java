package com.example.game_match.matchparticipation.repository.jpa;

import com.example.game_match.matchparticipation.domain.MatchParticipation;
import com.example.game_match.matchparticipation.domain.vo.MatchParticipationRole;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchParticipationJpaRepository extends JpaRepository<MatchParticipation, Integer> {
    @EntityGraph(attributePaths = {
            "gameUser",
            "gameUser.user",
            "gameUser.game",
            "gameUser.group",
            "friendlyMatch",
            "friendlyMatch.game",
            "friendlyMatch.host"
    })
    List<MatchParticipation> findByFriendlyMatch_Id(Integer friendlyMatchId);

    @EntityGraph(attributePaths = {
            "gameUser",
            "gameUser.user",
            "gameUser.game",
            "gameUser.group",
            "friendlyMatch",
            "friendlyMatch.game",
            "friendlyMatch.host"
    })
    List<MatchParticipation> findByGameUser_Id(Integer gameUserId);

    @EntityGraph(attributePaths = {
            "gameUser",
            "gameUser.user",
            "gameUser.game",
            "gameUser.group",
            "friendlyMatch",
            "friendlyMatch.game",
            "friendlyMatch.host"
    })
    List<MatchParticipation> findByFriendlyMatch_IdAndRole(
            Integer friendlyMatchId,
            MatchParticipationRole role
    );

    boolean existsByGameUser_IdAndFriendlyMatch_Id(Integer gameUserId, Integer friendlyMatchId);
}
