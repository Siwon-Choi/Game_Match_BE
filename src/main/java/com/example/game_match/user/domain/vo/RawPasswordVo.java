package com.example.game_match.user.domain.vo;

import lombok.Getter;

@Getter
public class RawPasswordVo {
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 255;

    private final String value;

    private RawPasswordVo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("loginPassword는 필수입니다.");
        }
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("loginPassword는 8자 이상 255자 이하여야 합니다.");
        }

        this.value = value;
    }

    public static RawPasswordVo from(String value) {
        return new RawPasswordVo(value);
    }
}
