package com.example.game_match.matchrequest.repository.jpa;

import com.example.game_match.matchrequest.domain.MatchRequestParticipation;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRequestParticipationJpaRepository extends JpaRepository<MatchRequestParticipation, Integer> {
    @EntityGraph(attributePaths = {
            "matchRequest",
            "matchRequest.gameUser",
            "matchRequest.friendlyMatch",
            "matchRequest.friendlyMatch.game",
            "gameUser",
            "gameUser.user",
            "gameUser.game",
            "gameUser.group"
    })
    List<MatchRequestParticipation> findByMatchRequest_Id(Integer matchRequestId);

    @EntityGraph(attributePaths = {
            "matchRequest",
            "matchRequest.gameUser",
            "matchRequest.friendlyMatch",
            "matchRequest.friendlyMatch.game",
            "gameUser",
            "gameUser.user",
            "gameUser.game",
            "gameUser.group"
    })
    List<MatchRequestParticipation> findByGameUser_Id(Integer gameUserId);

    void deleteByMatchRequest_Id(Integer matchRequestId);
}
