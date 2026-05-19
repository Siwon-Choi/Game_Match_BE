package com.example.game_match.friendlymatch.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class FriendlyMatchSortVo {
    private static final byte TEAM = 0;
    private static final byte PERSONAL = 1;

    private final Byte value;

    private FriendlyMatchSortVo(Byte value) {
        validate(value);
        this.value = value;
    }

    public static FriendlyMatchSortVo from(Byte value) {
        return new FriendlyMatchSortVo(value);
    }

    private static void validate(Byte value) {
        if (value == null) {
            throw new IllegalArgumentException("경기 방식은 필수입니다.");
        }

        if (value != TEAM && value != PERSONAL) {
            throw new IllegalArgumentException("경기 방식은 0(팀전) 또는 1(개인전)이어야 합니다.");
        }
    }
}
