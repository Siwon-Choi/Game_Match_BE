package com.hd.gamematch.game.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameJpaRepository extends JpaRepository<GameJpaEntity, Long> {

    List<GameJpaEntity> findByNameContainingIgnoreCaseAndSort(String name, String sort);

    List<GameJpaEntity> findByNameContainingIgnoreCase(String name);

    List<GameJpaEntity> findBySort(String sort);
}
