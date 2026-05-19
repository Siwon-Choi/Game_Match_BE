package com.example.game_match.matchrequest.repository.jpa;

import com.example.game_match.matchrequest.domain.MatchRequest;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRequestJpaRepository extends JpaRepository<MatchRequest, Integer> {
    @NotNull
    @Override
    @EntityGraph(attributePaths = {
            "gameUser",
            "gameUser.user",
            "gameUser.game",
            "gameUser.group",
            "friendlyMatch",
            "friendlyMatch.host",
            "friendlyMatch.host.user",
            "friendlyMatch.host.game",
            "friendlyMatch.host.group",
            "friendlyMatch.game"
    })
    Optional<MatchRequest> findById(@NotNull Integer id);

    @Query("select mr.friendlyMatch.id from MatchRequest mr where mr.id = :requestId")
    Optional<Integer> findFriendlyMatchIdByRequestId(@Param("requestId") Integer requestId);

    @EntityGraph(attributePaths = {
            "gameUser",
            "gameUser.user",
            "gameUser.game",
            "gameUser.group",
            "friendlyMatch",
            "friendlyMatch.host",
            "friendlyMatch.host.user",
            "friendlyMatch.host.game",
            "friendlyMatch.host.group",
            "friendlyMatch.game"
    })
    List<MatchRequest> findByFriendlyMatch_Id(Integer friendlyMatchId);

    @EntityGraph(attributePaths = {
            "gameUser",
            "gameUser.user",
            "gameUser.game",
            "gameUser.group",
            "friendlyMatch",
            "friendlyMatch.host",
            "friendlyMatch.host.user",
            "friendlyMatch.host.game",
            "friendlyMatch.host.group",
            "friendlyMatch.game"
    })
    List<MatchRequest> findByFriendlyMatch_IdOrderByIdDesc(Integer friendlyMatchId);

    @EntityGraph(attributePaths = {
            "gameUser",
            "gameUser.user",
            "gameUser.game",
            "gameUser.group",
            "friendlyMatch",
            "friendlyMatch.host",
            "friendlyMatch.host.user",
            "friendlyMatch.host.game",
            "friendlyMatch.host.group",
            "friendlyMatch.game"
    })
    List<MatchRequest> findByGameUser_IdOrderByIdDesc(Integer gameUserId);

    @EntityGraph(attributePaths = {
            "gameUser",
            "gameUser.user",
            "gameUser.game",
            "gameUser.group",
            "friendlyMatch",
            "friendlyMatch.host",
            "friendlyMatch.host.user",
            "friendlyMatch.host.game",
            "friendlyMatch.host.group",
            "friendlyMatch.game"
    })
    Optional<MatchRequest> findByGameUser_IdAndFriendlyMatch_Id(Integer gameUserId, Integer friendlyMatchId);
}
