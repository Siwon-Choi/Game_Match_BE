package com.example.game_match.game.repository.jpa;

import com.example.game_match.game.domain.Game;
import com.example.game_match.game.domain.vo.GameNameVo;
import com.example.game_match.game.domain.vo.GameSortVo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameJpaRepository extends JpaRepository<Game, Integer> {
    List<Game> findBySort(GameSortVo sort);

    Optional<Game> findByName(GameNameVo name);
}
