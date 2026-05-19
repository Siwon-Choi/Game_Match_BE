package com.example.game_match.game.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class GameNameVo {
    private static final int MAX_LENGTH = 255;

    private final String value;

    private GameNameVo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("game name은 필수입니다.");
        }

        String trimmedValue = value.trim();
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("game name은 255자 이하여야 합니다.");
        }

        this.value = trimmedValue;
    }

    public static GameNameVo from(String value) {
        return new GameNameVo(value);
    }
}
