package com.example.game_match.gamegroup.repository;

import com.example.game_match.gamegroup.domain.GameGroup;
import java.util.List;
import java.util.Optional;

public interface GameGroupRepository {
    List<GameGroup> findAll();

    List<GameGroup> findByGameId(Integer gameId);

    Optional<GameGroup> findById(Integer id);

    GameGroup save(GameGroup gameGroup);
}
