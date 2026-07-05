package com.hd.gamematch.game.domain;

public record GameSort(String value) {
    public GameSort {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("게임 분류는 필수입니다.");
        }

        if (value.length() > 50) {
            throw new IllegalArgumentException("게임 분류는 50자를 넘을 수 없습니다.");
        }
    }
}
