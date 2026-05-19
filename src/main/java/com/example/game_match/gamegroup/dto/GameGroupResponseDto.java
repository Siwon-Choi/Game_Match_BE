package com.example.game_match.gamegroup.dto;

import com.example.game_match.gamegroup.domain.GameGroup;

public record GameGroupResponseDto(
        Integer id,
        Integer gameId,
        String gameName,
        String name
) {
    public static GameGroupResponseDto from(GameGroup gameGroup) {
        return new GameGroupResponseDto(
                gameGroup.getId(),
                gameGroup.getGame().getId(),
                gameGroup.getGame().getName(),
                gameGroup.getName()
        );
    }
}
