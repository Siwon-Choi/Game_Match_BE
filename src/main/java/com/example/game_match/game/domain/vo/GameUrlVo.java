package com.example.game_match.game.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class GameUrlVo {
    private static final int MAX_LENGTH = 255;

    private final String value;

    private GameUrlVo(String value) {
        if (value == null || value.isBlank()) {
            this.value = null;
            return;
        }

        String trimmedValue = value.trim();
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("game url은 255자 이하여야 합니다.");
        }

        this.value = trimmedValue;
    }

    public static GameUrlVo from(String value) {
        return new GameUrlVo(value);
    }
}
