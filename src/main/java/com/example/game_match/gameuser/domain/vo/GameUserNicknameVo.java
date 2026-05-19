package com.example.game_match.gameuser.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class GameUserNicknameVo {
    private static final int MAX_LENGTH = 255;

    private final String value;

    private GameUserNicknameVo(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("게임 닉네임을 입력해주세요.");
        }

        String trimmedValue = value.trim();
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("게임 닉네임은 255자 이하여야 합니다.");
        }

        this.value = trimmedValue;
    }

    public static GameUserNicknameVo from(String value) {
        return new GameUserNicknameVo(value);
    }
}
