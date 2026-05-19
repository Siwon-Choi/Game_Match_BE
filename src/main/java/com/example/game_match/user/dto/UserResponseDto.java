package com.example.game_match.user.dto;

import com.example.game_match.user.domain.User;
import java.time.LocalDate;

public record UserResponseDto(
        Integer id,
        String name,
        String email,
        String profile,
        String phoneNumber,
        LocalDate birth,
        String loginId
) {
    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfile(),
                user.getPhoneNumber(),
                user.getBirth(),
                user.getLoginId()
        );
    }
}
