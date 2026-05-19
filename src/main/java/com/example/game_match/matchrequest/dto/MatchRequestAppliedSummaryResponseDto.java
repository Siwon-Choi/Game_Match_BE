package com.example.game_match.matchrequest.dto;

import com.example.game_match.matchrequest.domain.vo.MatchRequestStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record MatchRequestAppliedSummaryResponseDto(
        Integer requestId,
        Integer matchId,
        String gameName,
        String hostNickname,
        LocalDate date,
        LocalTime time,
        Byte sort,
        Byte state,
        Integer recruit,
        String comment,
        MatchRequestStatus status,
        LocalDateTime updatedAt
) {
}
