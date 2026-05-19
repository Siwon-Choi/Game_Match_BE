package com.example.game_match.gamegroup.repository.jpa;

import com.example.game_match.gamegroup.domain.GameGroup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameGroupJpaRepository extends JpaRepository<GameGroup, Integer> {
    List<GameGroup> findAllByOrderByIdAsc();

    List<GameGroup> findByGame_IdOrderByIdAsc(Integer gameId);
}
