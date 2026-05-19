package com.example.game_match.sms.domain;

import com.example.game_match.sms.domain.vo.SmsVerificationCodeVo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerificationCode {
    private static final int EXPIRATION_TIME_IN_MINUTES = 10;

    private SmsVerificationCodeVo code;
    private LocalDateTime createdAt;
    private Integer expirationTimeInMinutes;

    private VerificationCode(SmsVerificationCodeVo code, LocalDateTime createdAt) {
        validateRequired(code, "code");
        validateRequired(createdAt, "createdAt");

        this.code = code;
        this.createdAt = createdAt;
        this.expirationTimeInMinutes = EXPIRATION_TIME_IN_MINUTES;
    }

    public static VerificationCode create(LocalDateTime createdAt) {
        return new VerificationCode(SmsVerificationCodeVo.generate(), createdAt);
    }

    public boolean isExpired(LocalDateTime verifiedAt) {
        validateRequired(verifiedAt, "verifiedAt");
        return verifiedAt.isAfter(createdAt.plusMinutes(expirationTimeInMinutes));
    }

    public String getCodeValue() {
        return code.getValue();
    }

    public String generateCodeMessage() {
        String formattedExpiredAt = createdAt
                .plusMinutes(expirationTimeInMinutes)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return String.format(
                """
                        [Verification Code]
                        %s
                        Expired At : %s
                        """,
                code.getValue(),
                formattedExpiredAt
        );
    }

    private static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + "은 필수입니다.");
        }
    }
}
