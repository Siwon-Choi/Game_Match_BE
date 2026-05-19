package com.example.game_match.recommendation.dto;

public record PostVoteResponseDto(
        String status,
        Integer recommendations,
        Integer dislikes
) {
}
