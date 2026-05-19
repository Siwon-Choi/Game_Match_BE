package com.example.game_match.friendlymatch.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record FriendlyMatchHostedSummaryResponseDto(
        Integer matchId,
        String gameName,
        LocalDate date,
        LocalTime time,
        Byte sort,
        Byte state,
        Integer recruit,
        String comment,
        long activeRequestCount,
        long pendingRequestCount,
        long approvedRequestCount,
        LocalDateTime latestRequestUpdatedAt
) {
}
