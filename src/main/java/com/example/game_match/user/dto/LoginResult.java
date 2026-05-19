package com.example.game_match.user.dto;

public record LoginResult(String name, Integer userId, String accessToken, String refreshToken) {

    public static LoginResult of(String name, Integer userId, String accessToken, String refreshToken) {
        return new LoginResult(name, userId, accessToken, refreshToken);
    }
}
