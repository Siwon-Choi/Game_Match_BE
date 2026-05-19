package com.example.game_match.matchrequest.dto;

import com.example.game_match.matchrequest.domain.vo.MatchRequestStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record MatchRequestDetailResponseDto(
        Integer requestId,
        Integer matchId,
        String gameName,
        String hostNickname,
        Integer requesterGameUserId,
        String requesterNickname,
        Integer requesterGroupId,
        String requesterGroupName,
        String comment,
        MatchRequestStatus status,
        LocalDate date,
        LocalTime time,
        Byte sort,
        Byte state,
        Integer recruit,
        List<MatchRequestMemberResponseDto> teamMembers,
        LocalDateTime updatedAt
) {
}
