package com.example.game_match.post.dto;

public record PostCreateRequestDto(
        String title,
        String content,
        Integer userId,
        Integer gameId,
        Boolean anonymous
) {
    public PostCreateDto toServiceDto() {
        return new PostCreateDto(title, content, userId, gameId, anonymous);
    }
}
