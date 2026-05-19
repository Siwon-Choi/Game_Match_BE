package com.example.game_match.friendship.repository;

import com.example.game_match.friendship.domain.Friendship;
import java.util.List;

public interface FriendshipRepository {
    List<Friendship> findByGameUserId(Integer gameUserId);
}
