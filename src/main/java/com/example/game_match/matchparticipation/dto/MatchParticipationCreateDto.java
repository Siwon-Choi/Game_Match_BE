package com.example.game_match.matchparticipation.dto;

import com.example.game_match.matchparticipation.domain.vo.MatchParticipationRole;

public record MatchParticipationCreateDto(
        Integer gameUserId,
        Integer friendlyMatchId,
        MatchParticipationRole role
) {
}
