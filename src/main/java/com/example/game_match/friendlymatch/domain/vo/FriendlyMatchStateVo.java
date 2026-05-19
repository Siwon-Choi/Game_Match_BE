package com.example.game_match.friendlymatch.domain.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class FriendlyMatchStateVo {
    private static final byte OPEN = 0;
    private static final byte CLOSED = 1;

    private final Byte value;

    private FriendlyMatchStateVo(Byte value) {
        validate(value);
        this.value = value;
    }

    public static FriendlyMatchStateVo from(Byte value) {
        return new FriendlyMatchStateVo(value);
    }

    public static FriendlyMatchStateVo open() {
        return from(OPEN);
    }

    public static FriendlyMatchStateVo closed() {
        return from(CLOSED);
    }

    private static void validate(Byte value) {
        if (value == null) {
            throw new IllegalArgumentException("모집 상태는 필수입니다.");
        }

        if (value != OPEN && value != CLOSED) {
            throw new IllegalArgumentException("모집 상태는 0(모집 중) 또는 1(모집 마감)이어야 합니다.");
        }
    }
}
