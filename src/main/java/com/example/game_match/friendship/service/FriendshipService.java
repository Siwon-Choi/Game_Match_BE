package com.example.game_match.friendship.service;

import com.example.game_match.friendship.domain.Friendship;
import com.example.game_match.friendship.repository.FriendshipRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;

    // 특정 게임 유저와 연결된 친구 관계를 조회한다.
    // 친구 관계는 양방향을 한 row로 저장하므로, 요청한 유저의 반대편 gameUserId만 반환한다.
    @Transactional(readOnly = true)
    public List<Integer> getFriendsByGameUserId(Integer gameUserId) {
        return friendshipRepository.findByGameUserId(gameUserId).stream()
                .map(friendship -> friendship.getFriendId(gameUserId))
                .distinct()
                .toList();
    }
}
