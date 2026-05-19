package com.example.game_match.sms.dto;

public record PhoneVerificationConfirmRequestDto(
        String code,
        String sessionId
) {
}
