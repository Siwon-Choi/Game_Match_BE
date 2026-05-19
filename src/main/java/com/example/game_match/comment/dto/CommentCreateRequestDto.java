package com.example.game_match.comment.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CommentCreateRequestDto(
        Integer userId,
        String content,
        LocalDate date,
        LocalTime time,
        Integer parentCommentId,
        Boolean anonymous
) {
    public CommentCreateDto toServiceDto(Integer postId) {
        return new CommentCreateDto(userId, content, date, time, postId, parentCommentId, anonymous);
    }
}
