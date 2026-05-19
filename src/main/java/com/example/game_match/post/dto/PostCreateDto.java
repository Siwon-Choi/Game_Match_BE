package com.example.game_match.post.dto;

public record PostCreateDto(
        String title,
        String content,
        Integer userId,
        Integer gameId,
        Boolean anonymous
) {
}
