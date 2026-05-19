package com.example.game_match.user.domain.vo;

import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class PhoneNumberVo {
    private static final int MAX_LENGTH = 20;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9\\-]+$");

    private final String value;

    private PhoneNumberVo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("phoneNumber는 필수입니다.");
        }

        String trimmedValue = value.trim();
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("phoneNumber는 20자 이하여야 합니다.");
        }
        if (!PHONE_PATTERN.matcher(trimmedValue).matches()) {
            throw new IllegalArgumentException("phoneNumber 형식이 올바르지 않습니다.");
        }

        this.value = trimmedValue;
    }

    public static PhoneNumberVo from(String value) {
        return new PhoneNumberVo(value);
    }
}
