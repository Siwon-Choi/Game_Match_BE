package com.example.game_match.friendlymatch.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record FriendlyMatchCreateDto(
        Integer hostId,
        Integer gameId,
        LocalDate date,
        LocalTime time,
        Byte sort,
        Byte state,
        Integer recruit,
        String comment
) {
}
