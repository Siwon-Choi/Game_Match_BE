package com.hd.gamematch.game.application.port.out;

import com.hd.gamematch.game.domain.Game;

import java.util.List;

public interface LoadGamesPort {

    List<Game> loadAllGames();

    List<Game> loadGamesByNameAndSort(String name, String sort);

    List<Game> loadGamesByName(String name);

    List<Game> loadGamesBySort(String sort);
}
