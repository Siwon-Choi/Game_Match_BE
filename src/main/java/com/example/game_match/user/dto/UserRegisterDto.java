package com.example.game_match.user.dto;

import java.time.LocalDate;

public record UserRegisterDto(
        String name,
        String email,
        String phoneNumber,
        LocalDate birth,
        String loginId,
        String loginPassword
) {
}
