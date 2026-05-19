package com.example.game_match.sms.dto;

public record PhoneVerificationRequestDto(
        String phoneNumber,
        String sessionId
) {
}
