package com.example.game_match.friendlymatch.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class FriendlyMatchRecruitVo {
    private final Integer value;

    private FriendlyMatchRecruitVo(Integer value) {
        validate(value);
        this.value = value;
    }

    public static FriendlyMatchRecruitVo from(Integer value) {
        return new FriendlyMatchRecruitVo(value);
    }

    private static void validate(Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("모집 인원은 필수입니다.");
        }

        if (value < 1) {
            throw new IllegalArgumentException("모집 인원은 1명 이상이어야 합니다.");
        }
    }
}
