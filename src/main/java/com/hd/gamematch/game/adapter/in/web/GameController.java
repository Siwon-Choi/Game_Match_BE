package com.hd.gamematch.game.adapter.in.web;


import com.hd.gamematch.game.application.port.in.FindGameQuery;
import com.hd.gamematch.game.application.port.in.FindGameUseCase;
import com.hd.gamematch.game.application.port.in.FindGamesQuery;
import com.hd.gamematch.game.application.port.in.FindGamesUseCase;
import com.hd.gamematch.game.domain.Game;
import com.hd.gamematch.global.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {
    private final FindGamesUseCase findGamesUseCase;
    private final FindGameUseCase findGameUseCase;

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


    @GetMapping("/{gameId}")
    public CommonResponse<GameResponse> findGame(@PathVariable Long gameId){
        Game game = findGameUseCase.findGame(FindGameQuery.of(gameId));

        return CommonResponse.success(GameResponse.from(game));
    }
}
