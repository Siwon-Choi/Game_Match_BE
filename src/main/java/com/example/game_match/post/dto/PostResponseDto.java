package com.example.game_match.post.dto;

import com.example.game_match.post.domain.Post;
import java.time.LocalDate;
import java.time.LocalTime;

public record PostResponseDto(
        Integer id,
        String title,
        LocalDate date,
        Integer views,
        Integer recommendations,
        Integer dislikes,
        String content,
        Integer userId,
        Integer gameId,
        LocalTime time,
        Boolean anonymous
) {
    public static PostResponseDto from(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getDate(),
                post.getViews(),
                post.getRecommendations(),
                post.getDislikes(),
                post.getContent(),
                post.getUserId(),
                post.getGameId(),
                post.getTime(),
                post.getAnonymous()
        );
    }
}
