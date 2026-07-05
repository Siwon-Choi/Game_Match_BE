package com.hd.gamematch.game.domain;

public record GameName(String value) {
    public GameName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("게임 이름은 필수입니다.");
        }

        if (value.length() > 100) {
            throw new IllegalArgumentException("게임 이름은 100자를 넘을 수 없습니다.");
        }

    }
}
