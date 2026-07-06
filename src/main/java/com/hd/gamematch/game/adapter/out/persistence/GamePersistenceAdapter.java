package com.hd.gamematch.game.adapter.out.persistence;

import com.hd.gamematch.game.application.port.out.LoadGamesPort;
import com.hd.gamematch.game.domain.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GamePersistenceAdapter implements LoadGamesPort {

    private final GameJpaRepository gameJpaRepository;

    @Override
    public List<Game> loadAllGames() {
        return gameJpaRepository.findAll()
                .stream()
                .map(GamePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Game> loadGamesByNameAndSort(String name, String sort) {
        return gameJpaRepository.findByNameContainingIgnoreCaseAndSort(name, sort)
                .stream()
                .map(GamePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Game> loadGamesByName(String name) {
        return gameJpaRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(GamePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Game> loadGamesBySort(String sort) {
        return gameJpaRepository.findBySort(sort)
                .stream()
                .map(GamePersistenceMapper::toDomain)
                .toList();
    }
}
