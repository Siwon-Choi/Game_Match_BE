package com.hd.gamematch.game.domain;

public record GameId(Long value) {
    public GameId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("게임 ID는 양수여야합니다.");
        }
    }
}
