package com.example.game_match.game.dto;

import com.example.game_match.game.domain.Game;

public record GameResponseDto(
        Integer id,
        String name,
        String sort,
        String url
) {
    public static GameResponseDto from(Game game) {
        return new GameResponseDto(
                game.getId(),
                game.getName(),
                game.getSort(),
                game.getUrl()
        );
    }
}
