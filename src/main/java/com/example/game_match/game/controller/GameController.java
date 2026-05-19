package com.example.game_match.game.controller;

import com.example.game_match.game.dto.GameResponseDto;
import com.example.game_match.game.service.GameService;
import com.example.game_match.global.response.CommonResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {
    private final GameService gameService;

    // 게임 목록을 조회한다.
    // sort가 있으면 해당 장르/분류의 게임 목록을 조회하고,
    // name이 있으면 해당 이름의 게임을 조회한다.
    // 둘 다 없으면 전체 게임 목록을 조회한다.
    @GetMapping
    public ResponseEntity<CommonResponse<List<GameResponseDto>>> findGames(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String name
    ) {
        return ResponseEntity.ok(CommonResponse.success(gameService.findGames(sort, name)));
    }

    // gameId로 특정 게임 하나를 조회한다.
    @GetMapping("/{gameId}")
    public ResponseEntity<CommonResponse<GameResponseDto>> findGameById(@PathVariable Integer gameId) {
        return ResponseEntity.ok(CommonResponse.success(gameService.findGameById(gameId)));
    }
}
