package com.example.game_match.user.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class UserNameVo {
    private static final int MAX_LENGTH = 255;

    private final String value;

    private UserNameVo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("name은 필수입니다.");
        }

        String trimmedValue = value.trim();
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("name은 255자 이하여야 합니다.");
        }

        this.value = trimmedValue;
    }

    public static UserNameVo from(String value) {
        return new UserNameVo(value);
    }
}
