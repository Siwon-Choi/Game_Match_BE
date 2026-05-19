package com.example.game_match.matchrequest.dto;

import java.util.List;

public record MatchRequestApplyRequestDto(
        Integer gameUserId,
        List<Integer> teamMemberIds,
        String comment
) {
    public MatchRequestApplyDto toServiceDto(Integer matchId) {
        return new MatchRequestApplyDto(gameUserId, matchId, teamMemberIds, comment);
    }
}
