package com.example.game_match.gamegroup.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class GameGroupNameVo {
    private static final int MAX_LENGTH = 255;

    private final String value;

    private GameGroupNameVo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("group name은 필수입니다.");
        }

        String trimmedValue = value.trim();
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("group name은 255자 이하여야 합니다.");
        }

        this.value = trimmedValue;
    }

    public static GameGroupNameVo from(String value) {
        return new GameGroupNameVo(value);
    }
}
