package com.example.game_match.matchparticipation.dto;

import com.example.game_match.matchparticipation.domain.vo.MatchParticipationRole;

public record MatchParticipationCreateRequestDto(
        Integer gameUserId,
        Integer friendlyMatchId,
        MatchParticipationRole role
) {
    public MatchParticipationCreateDto toServiceDto(Integer pathMatchId) {
        Integer resolvedMatchId = pathMatchId == null ? friendlyMatchId : pathMatchId;
        return new MatchParticipationCreateDto(gameUserId, resolvedMatchId, role);
    }
}
