package com.example.game_match.friendship.repository.jpa;

import com.example.game_match.friendship.domain.Friendship;
import com.example.game_match.friendship.repository.FriendshipRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FriendshipRepositoryJpaAdapter implements FriendshipRepository {
    private final FriendshipJpaRepository friendshipJpaRepository;

    @Override
    public List<Friendship> findByGameUserId(Integer gameUserId) {
        return friendshipJpaRepository.findByGameUser1_IdOrGameUser2_Id(gameUserId, gameUserId);
    }
}
