package com.example.game_match.user.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class EncodedPasswordVo {
    private static final int MAX_LENGTH = 255;

    private final String value;

    private EncodedPasswordVo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("encodedPassword는 필수입니다.");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("encodedPassword는 255자 이하여야 합니다.");
        }

        this.value = value;
    }

    public static EncodedPasswordVo from(String value) {
        return new EncodedPasswordVo(value);
    }
}
