package com.example.game_match.imagepost.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ImageUrlVo {
    private static final int MAX_LENGTH = 255;

    private final String value;

    private ImageUrlVo(String value) {
        validate(value);
        this.value = value.trim();
    }

    public static ImageUrlVo from(String value) {
        return new ImageUrlVo(value);
    }

    private static void validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("이미지 URL은 필수입니다.");
        }

        if (value.trim().length() > MAX_LENGTH) {
            throw new IllegalArgumentException("이미지 URL은 255자 이하여야 합니다.");
        }
    }
}
