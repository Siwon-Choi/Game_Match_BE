package com.hd.gamematch.game.application.service;

import com.hd.gamematch.game.application.port.in.FindGameQuery;
import com.hd.gamematch.game.application.port.in.FindGameUseCase;
import com.hd.gamematch.game.application.port.out.LoadGamePort;
import com.hd.gamematch.game.domain.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindGameService implements FindGameUseCase {

    private final LoadGamePort loadGamePort;

    @Override
    public Game findGame(FindGameQuery query){
        return loadGamePort.loadGameById(query.gameId())
                .orElseThrow(()->new IllegalArgumentException("게임을 찾을 수 없습니다."));
    }
}
