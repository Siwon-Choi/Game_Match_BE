package com.example.game_match.friendship.repository.jpa;

import com.example.game_match.friendship.domain.Friendship;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipJpaRepository extends JpaRepository<Friendship, Integer> {
    @EntityGraph(attributePaths = {"gameUser1", "gameUser2"})
    List<Friendship> findByGameUser1_IdOrGameUser2_Id(Integer gameUser1Id, Integer gameUser2Id);
}
