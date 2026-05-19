package com.example.game_match.game.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class GameSortVo {
    private static final int MAX_LENGTH = 255;

    private final String value;

    private GameSortVo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("game sort는 필수입니다.");
        }

        String trimmedValue = value.trim();
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("game sort는 255자 이하여야 합니다.");
        }

        this.value = trimmedValue;
    }

    public static GameSortVo from(String value) {
        return new GameSortVo(value);
    }
}
