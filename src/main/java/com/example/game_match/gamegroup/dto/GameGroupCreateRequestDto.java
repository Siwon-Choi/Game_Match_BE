package com.example.game_match.gamegroup.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GameGroupCreateRequestDto {
    private Integer gameId;
    private String name;

    public GameGroupCreateDto toServiceDto() {
        return new GameGroupCreateDto(gameId, name);
    }
}
