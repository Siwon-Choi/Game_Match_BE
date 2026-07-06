package com.hd.gamematch.game.adapter.out.persistence;

import com.hd.gamematch.game.domain.Game;

public class GamePersistenceMapper {

    private GamePersistenceMapper() {
    }

    public static Game toDomain(GameJpaEntity entity){
        return Game.of(
                entity.getId(),
                entity.getName(),
                entity.getSort(),
                entity.getUrl()
        );
    }
}
