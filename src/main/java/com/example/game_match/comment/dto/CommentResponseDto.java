package com.example.game_match.comment.dto;

import com.example.game_match.comment.domain.Comment;
import java.time.LocalDate;
import java.time.LocalTime;

public record CommentResponseDto(
        Integer id,
        Integer userId,
        String content,
        LocalDate date,
        LocalTime time,
        Integer postId,
        Integer parentCommentId,
        Boolean anonymous
) {
    public static CommentResponseDto from(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getUserId(),
                comment.getContent(),
                comment.getDate(),
                comment.getTime(),
                comment.getPostId(),
                comment.getParentCommentId(),
                comment.getAnonymous()
        );
    }
}
