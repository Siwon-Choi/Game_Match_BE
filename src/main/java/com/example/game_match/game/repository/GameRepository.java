package com.example.game_match.game.repository;

import com.example.game_match.game.domain.Game;
import com.example.game_match.game.domain.vo.GameNameVo;
import com.example.game_match.game.domain.vo.GameSortVo;
import java.util.List;
import java.util.Optional;

public interface GameRepository {
    List<Game> findAll();

    List<Game> findBySort(GameSortVo sort);

    Optional<Game> findById(Integer id);

    Optional<Game> findByName(GameNameVo name);

    Game save(Game game);
}
