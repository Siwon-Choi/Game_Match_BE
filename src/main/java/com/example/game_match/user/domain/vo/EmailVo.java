package com.example.game_match.user.domain.vo;

import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class EmailVo {
    private static final int MAX_LENGTH = 255;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final String value;

    private EmailVo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("email은 필수입니다.");
        }

        String trimmedValue = value.trim();
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("email은 255자 이하여야 합니다.");
        }
        if (!EMAIL_PATTERN.matcher(trimmedValue).matches()) {
            throw new IllegalArgumentException("email 형식이 올바르지 않습니다.");
        }

        this.value = trimmedValue;
    }

    public static EmailVo from(String value) {
        return new EmailVo(value);
    }
}
