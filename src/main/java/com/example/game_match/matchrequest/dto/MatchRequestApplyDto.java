package com.example.game_match.matchrequest.dto;

import java.util.List;

public record MatchRequestApplyDto(
        Integer gameUserId,
        Integer matchId,
        List<Integer> teamMemberIds,
        String comment
) {
}
