package com.example.game_match.comment.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class CommentContentVo {
    private static final int MAX_LENGTH = 45;

    private final String value;

    private CommentContentVo(String value) {
        validate(value);
        this.value = value.trim();
    }

    public static CommentContentVo from(String value) {
        return new CommentContentVo(value);
    }

    private static void validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 필수입니다.");
        }

        if (value.trim().length() > MAX_LENGTH) {
            throw new IllegalArgumentException("댓글 내용은 45자 이하여야 합니다.");
        }
    }
}
