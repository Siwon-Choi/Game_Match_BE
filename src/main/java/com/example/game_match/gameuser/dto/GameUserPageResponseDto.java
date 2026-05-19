package com.example.game_match.gameuser.dto;

import java.util.List;

public record GameUserPageResponseDto(
        long totalCount,
        List<GameUserResponseDto> gameUsers
) {
}
