package com.example.game_match.sms.domain.vo;

import java.security.SecureRandom;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class SmsVerificationCodeVo {
    private static final Pattern CODE_PATTERN = Pattern.compile("^\\d{6}$");
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final String value;

    private SmsVerificationCodeVo(String value) {
        validate(value);
        this.value = value;
    }

    public static SmsVerificationCodeVo from(String value) {
        return new SmsVerificationCodeVo(value);
    }

    public static SmsVerificationCodeVo generate() {
        return new SmsVerificationCodeVo(String.format("%06d", SECURE_RANDOM.nextInt(1_000_000)));
    }

    private static void validate(String value) {
        if (value == null || !CODE_PATTERN.matcher(value.trim()).matches()) {
            throw new IllegalArgumentException("인증번호는 6자리 숫자여야 합니다.");
        }
    }
}
