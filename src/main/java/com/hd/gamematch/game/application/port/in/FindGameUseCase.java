package com.hd.gamematch.game.application.port.in;

import com.hd.gamematch.game.domain.Game;

public interface FindGameUseCase {
    Game findGame(FindGameQuery query);
}
