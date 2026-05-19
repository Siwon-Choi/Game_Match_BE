package com.example.game_match.gamegroup.repository.jpa;

import com.example.game_match.gamegroup.domain.GameGroup;
import com.example.game_match.gamegroup.repository.GameGroupRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameGroupRepositoryJpaAdapter implements GameGroupRepository {
    private final GameGroupJpaRepository gameGroupJpaRepository;

    @Override
    public List<GameGroup> findAll() {
        return gameGroupJpaRepository.findAllByOrderByIdAsc();
    }

    @Override
    public List<GameGroup> findByGameId(Integer gameId) {
        return gameGroupJpaRepository.findByGame_IdOrderByIdAsc(gameId);
    }

    @Override
    public Optional<GameGroup> findById(Integer id) {
        return gameGroupJpaRepository.findById(id);
    }

    @Override
    public GameGroup save(GameGroup gameGroup) {
        return gameGroupJpaRepository.save(gameGroup);
    }
}
