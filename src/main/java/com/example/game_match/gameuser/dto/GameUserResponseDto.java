package com.example.game_match.gameuser.dto;

import com.example.game_match.game.dto.GameResponseDto;
import com.example.game_match.gameuser.domain.GameUser;
import com.example.game_match.user.dto.UserResponseDto;

public record GameUserResponseDto(
        Integer id,
        String nickname,
        GameResponseDto game,
        UserResponseDto user,
        Integer groupId,
        String groupName
) {
    public static GameUserResponseDto from(GameUser gameUser) {
        return new GameUserResponseDto(
                gameUser.getId(),
                gameUser.getNickname(),
                GameResponseDto.from(gameUser.getGame()),
                UserResponseDto.from(gameUser.getUser()),
                gameUser.getGroupId(),
                gameUser.getGroup() == null ? null : gameUser.getGroup().getName()
        );
    }
}
