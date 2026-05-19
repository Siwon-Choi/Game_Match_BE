package com.example.game_match.friendlymatch.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.time.LocalDate;
import java.time.LocalTime;

public record FriendlyMatchCreateRequestDto(
        @JsonAlias("host") Integer hostId,
        @JsonAlias("game") Integer gameId,
        LocalDate date,
        LocalTime time,
        Byte sort,
        Byte state,
        Integer recruit,
        String comment
) {
    public FriendlyMatchCreateDto toServiceDto() {
        return new FriendlyMatchCreateDto(hostId, gameId, date, time, sort, state, recruit, comment);
    }
}
