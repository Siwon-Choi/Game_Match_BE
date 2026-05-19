package com.example.game_match.gameuser.dto;

public record GameUserUpdateRequestDto(
        String nickname,
        Integer groupId
) {
    public GameUserUpdateDto toServiceDto() {
        return new GameUserUpdateDto(nickname, groupId);
    }
}
