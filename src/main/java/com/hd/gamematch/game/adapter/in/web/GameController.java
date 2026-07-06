package com.hd.gamematch.game.adapter.in.web;


import com.hd.gamematch.game.application.port.in.FindGamesQuery;
import com.hd.gamematch.game.application.port.in.FindGamesUseCase;
import com.hd.gamematch.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {
    private final FindGamesUseCase findGamesUseCase;

    @GetMapping
    public CommonResponse<List<GameResponse>> findGames(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sort
    ){
        List<GameResponse> response = findGamesUseCase.findGames(FindGamesQuery.of(name, sort))
                .stream()
                .map(GameResponse::from)
                .toList();

        return CommonResponse.success(response);
    }
}
