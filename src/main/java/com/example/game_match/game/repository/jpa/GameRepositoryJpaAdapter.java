package com.example.game_match.game.repository.jpa;

import com.example.game_match.game.domain.Game;
import com.example.game_match.game.domain.vo.GameNameVo;
import com.example.game_match.game.domain.vo.GameSortVo;
import com.example.game_match.game.repository.GameRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameRepositoryJpaAdapter implements GameRepository {
    private final GameJpaRepository gameJpaRepository;

    @Override
    public List<Game> findAll() {
        return gameJpaRepository.findAll();
    }

    @Override
    public List<Game> findBySort(GameSortVo sort) {
        return gameJpaRepository.findBySort(sort);
    }

    @Override
    public Optional<Game> findById(Integer id) {
        return gameJpaRepository.findById(id);
    }

    @Override
    public Optional<Game> findByName(GameNameVo name) {
        return gameJpaRepository.findByName(name);
    }

    @Override
    public Game save(Game game) {
        return gameJpaRepository.save(game);
    }
}
