package com.hd.gamematch.game.application.port.out;

import com.hd.gamematch.game.domain.Game;

import java.util.Optional;

public interface LoadGamePort {
    Optional<Game> loadGameById(Long gameId);
}
