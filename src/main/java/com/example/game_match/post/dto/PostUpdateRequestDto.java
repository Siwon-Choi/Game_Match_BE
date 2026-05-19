package com.example.game_match.post.dto;

public record PostUpdateRequestDto(
        String title,
        String content,
        Integer userId,
        Integer gameId,
        Boolean anonymous
) {
    public PostUpdateDto toServiceDto() {
        return new PostUpdateDto(title, content, userId, gameId, anonymous);
    }
}
