package com.hd.gamematch.game.application.port.in;


import com.hd.gamematch.game.domain.Game;

import java.util.List;

// 게임 목록 조회 기능은 이런 입력을 받고 이런 결과를 돌려준다
public interface FindGamesUseCase {
    List<Game> findGames(FindGamesQuery query);
}
        