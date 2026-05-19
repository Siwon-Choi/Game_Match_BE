package com.example.game_match.user.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class LoginIdVo {
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 255;

    private final String value;

    private LoginIdVo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("loginId는 필수입니다.");
        }

        String trimmedValue = value.trim();
        if (trimmedValue.length() < MIN_LENGTH || trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("loginId는 3자 이상 255자 이하여야 합니다.");
        }

        this.value = trimmedValue;
    }

    public static LoginIdVo from(String value) {
        return new LoginIdVo(value);
    }
}
