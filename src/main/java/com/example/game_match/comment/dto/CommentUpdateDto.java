package com.example.game_match.comment.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CommentUpdateDto(
        Integer userId,
        String content,
        LocalDate date,
        LocalTime time,
        Integer postId,
        Integer parentCommentId,
        Boolean anonymous
) {
}
