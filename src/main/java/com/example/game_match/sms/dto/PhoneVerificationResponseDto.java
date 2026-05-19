package com.example.game_match.sms.dto;

public record PhoneVerificationResponseDto(
        boolean success,
        String message
) {
    public static PhoneVerificationResponseDto success(String message) {
        return new PhoneVerificationResponseDto(true, message);
    }
}
