package com.example.game_match.gameuser.dto;

public record GameUserCreateRequestDto(
        Integer gameId,
        String nickname,
        Integer groupId
) {
    public GameUserCreateDto toServiceDto() {
        return new GameUserCreateDto(gameId, nickname, groupId);
    }
}
