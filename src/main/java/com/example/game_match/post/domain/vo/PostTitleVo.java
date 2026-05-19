package com.example.game_match.post.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class PostTitleVo {
    private static final int MAX_LENGTH = 15;

    private final String value;

    private PostTitleVo(String value) {
        validate(value);
        this.value = value.trim();
    }

    public static PostTitleVo from(String value) {
        return new PostTitleVo(value);
    }

    private static void validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("게시글 제목은 필수입니다.");
        }

        if (value.trim().length() > MAX_LENGTH) {
            throw new IllegalArgumentException("게시글 제목은 15자 이하여야 합니다.");
        }
    }
}
