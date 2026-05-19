package com.example.game_match.auth.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class TokenHashVo {
    private static final int MAX_LENGTH = 128;

    private final String value;

    private TokenHashVo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("tokenHash는 필수입니다.");
        }

        String trimmedValue = value.trim();
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("tokenHash는 128자 이하여야 합니다.");
        }

        this.value = trimmedValue;
    }

    public static TokenHashVo from(String value) {
        return new TokenHashVo(value);
    }
}
