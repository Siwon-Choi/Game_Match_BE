package com.example.game_match.gameuser.controller;

import com.example.game_match.gameuser.dto.GameUserCreateRequestDto;
import com.example.game_match.gameuser.dto.GameUserPageResponseDto;
import com.example.game_match.gameuser.dto.GameUserResponseDto;
import com.example.game_match.gameuser.dto.GameUserUpdateRequestDto;
import com.example.game_match.gameuser.service.GameUserService;
import com.example.game_match.global.exception.ErrorCode;
import com.example.game_match.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/game-users")
public class GameUserController {
    private final GameUserService gameUserService;

    // 닉네임으로 게임 유저 프로필을 검색한다.
    // page와 size를 받아 페이징된 검색 결과와 전체 개수를 함께 반환한다.
    @GetMapping("/search")
    public ResponseEntity<CommonResponse<GameUserPageResponseDto>> getAllGameUserByNickname(
            @RequestParam String nickname,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(CommonResponse.success(
                gameUserService.getAllNicknamesWithTotalCount(nickname, page, size)
        ));
    }

    // 특정 사용자가 특정 게임에서 사용하는 게임 유저 프로필을 조회한다.
    // userId와 gameId 조합으로 하나의 GameUser를 찾는다.
    @GetMapping("/users/{userId}/games/{gameId}")
    public ResponseEntity<CommonResponse<GameUserResponseDto>> getGameUserByUserIdAndGameId(
            @PathVariable Integer userId,
            @PathVariable Integer gameId
    ) {
        return ResponseEntity.ok(CommonResponse.success(
                gameUserService.getGameUserByUserIdAndGameId(userId, gameId)
        ));
    }

    // gameUserId로 특정 게임 유저 프로필 하나를 조회한다.
    @GetMapping("/{gameUserId}")
    public ResponseEntity<CommonResponse<GameUserResponseDto>> getGameUserById(@PathVariable Integer gameUserId) {
        return ResponseEntity.ok(CommonResponse.success(gameUserService.getGameUserById(gameUserId)));
    }

    // 로그인한 사용자의 게임 유저 프로필을 새로 생성한다.
    @PostMapping
    public ResponseEntity<CommonResponse<GameUserResponseDto>> createGameUser(
            @RequestBody GameUserCreateRequestDto requestDto,
            @AuthenticationPrincipal Integer userId
    ) {
        if (userId == null) {
            return ResponseEntity
                    .status(ErrorCode.UNAUTHORIZED.getStatus())
                    .body(CommonResponse.error(ErrorCode.UNAUTHORIZED));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.success(gameUserService.createGameUser(userId, requestDto.toServiceDto())));
    }

    // 로그인한 사용자의 게임 유저 프로필 중 닉네임과 그룹 정보를 수정한다.
    @PatchMapping("/{gameUserId}")
    public ResponseEntity<CommonResponse<GameUserResponseDto>> updateGameUser(
            @PathVariable Integer gameUserId,
            @RequestBody GameUserUpdateRequestDto requestDto,
            @AuthenticationPrincipal Integer userId
    ) {
        if (userId == null) {
            return ResponseEntity
                    .status(ErrorCode.UNAUTHORIZED.getStatus())
                    .body(CommonResponse.error(ErrorCode.UNAUTHORIZED));
        }

        return ResponseEntity.ok(CommonResponse.success(
                gameUserService.updateGameUser(userId, gameUserId, requestDto.toServiceDto())
        ));
    }
}
