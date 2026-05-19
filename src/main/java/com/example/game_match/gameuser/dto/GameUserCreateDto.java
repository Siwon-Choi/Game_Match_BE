package com.example.game_match.gameuser.dto;

public record GameUserCreateDto(
        Integer gameId,
        String nickname,
        Integer groupId
) {
}
