package com.example.game_match.imagepost.dto;

import com.example.game_match.imagepost.domain.ImagePost;

public record ImagePostResponseDto(
        String id,
        String url,
        Integer postId
) {
    public static ImagePostResponseDto from(ImagePost imagePost) {
        return new ImagePostResponseDto(
                imagePost.getId(),
                imagePost.getUrl(),
                imagePost.getPostId()
        );
    }
}
