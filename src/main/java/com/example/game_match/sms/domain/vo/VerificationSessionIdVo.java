package com.example.game_match.sms.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class VerificationSessionIdVo {
    private static final int MAX_LENGTH = 100;

    private final String value;

    private VerificationSessionIdVo(String value) {
        validate(value);
        this.value = value.trim();
    }

    public static VerificationSessionIdVo from(String value) {
        return new VerificationSessionIdVo(value);
    }

    private static void validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("세션 ID는 필수입니다.");
        }

        if (value.trim().length() > MAX_LENGTH) {
            throw new IllegalArgumentException("세션 ID는 100자 이하여야 합니다.");
        }
    }
}
