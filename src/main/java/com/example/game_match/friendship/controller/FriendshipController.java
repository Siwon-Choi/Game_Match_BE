package com.example.game_match.friendship.controller;

import com.example.game_match.friendship.service.FriendshipService;
import com.example.game_match.global.response.CommonResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;

    // 특정 게임 유저의 친구 게임 유저 ID 목록을 조회한다.
    @GetMapping("/game-users/{gameUserId}/friends")
    public ResponseEntity<CommonResponse<List<Integer>>> getFriendsByGameUserId(
            @PathVariable Integer gameUserId
    ) {
        return ResponseEntity.ok(CommonResponse.success(friendshipService.getFriendsByGameUserId(gameUserId)));
    }
}
