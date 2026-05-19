package com.example.game_match.gamegroup.controller;

import com.example.game_match.gamegroup.dto.GameGroupCreateRequestDto;
import com.example.game_match.gamegroup.dto.GameGroupResponseDto;
import com.example.game_match.gamegroup.service.GameGroupService;
import com.example.game_match.gameuser.dto.GameUserResponseDto;
import com.example.game_match.gameuser.service.GameUserService;
import com.example.game_match.global.response.CommonResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game-groups")
public class GameGroupController {
    private final GameGroupService gameGroupService;
    private final GameUserService gameUserService;

    // 게임 그룹 목록을 조회한다.
    // gameId가 있으면 해당 게임에 속한 그룹만 조회하고,
    // gameId가 없으면 전체 게임 그룹 목록을 조회한다.
    @GetMapping
    public ResponseEntity<CommonResponse<List<GameGroupResponseDto>>> findGameGroups(
            @RequestParam(required = false) Integer gameId
    ) {
        return ResponseEntity.ok(CommonResponse.success(gameGroupService.findGameGroups(gameId)));
    }

    // groupId로 특정 게임 그룹 하나를 조회한다.
    @GetMapping("/{groupId}")
    public ResponseEntity<CommonResponse<GameGroupResponseDto>> findGameGroupById(@PathVariable Integer groupId) {
        return ResponseEntity.ok(CommonResponse.success(gameGroupService.findGameGroupById(groupId)));
    }

    // groupId로 해당 게임 그룹에 속한 게임 유저 프로필 목록을 조회한다.
    @GetMapping("/{groupId}/game-users")
    public ResponseEntity<CommonResponse<List<GameUserResponseDto>>> findGameUsersByGroupId(
            @PathVariable Integer groupId
    ) {
        return ResponseEntity.ok(CommonResponse.success(gameUserService.getGameUsersByGroupId(groupId)));
    }

    // 요청 body로 받은 게임 그룹 생성 정보를 기반으로 새 게임 그룹을 생성한다.
    @PostMapping
    public ResponseEntity<CommonResponse<GameGroupResponseDto>> createGameGroup(
            @RequestBody GameGroupCreateRequestDto requestDto
    ) {
        return ResponseEntity.ok(CommonResponse.success(gameGroupService.createGameGroup(requestDto.toServiceDto())));
    }
}
