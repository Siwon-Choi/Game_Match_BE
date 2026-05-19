package com.example.game_match.friendlymatch.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class FriendlyMatchCommentVo {
    private static final int MAX_LENGTH = 255;

    private final String value;

    private FriendlyMatchCommentVo(String value) {
        validate(value);
        this.value = value == null ? null : value.trim();
    }

    public static FriendlyMatchCommentVo from(String value) {
        return new FriendlyMatchCommentVo(value);
    }

    private static void validate(String value) {
        if (value != null && value.trim().length() > MAX_LENGTH) {
            throw new IllegalArgumentException("경기 설명은 255자 이하여야 합니다.");
        }
    }
}
