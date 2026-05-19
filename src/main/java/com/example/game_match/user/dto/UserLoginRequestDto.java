package com.example.game_match.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginRequestDto {
    private String loginId;
    private String loginPassword;

    public UserLoginDto toServiceDto() {
        return new UserLoginDto(loginId, loginPassword);
    }
}
