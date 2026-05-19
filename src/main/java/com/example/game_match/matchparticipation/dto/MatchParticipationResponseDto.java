package com.example.game_match.matchparticipation.dto;

import com.example.game_match.gameuser.dto.GameUserResponseDto;
import com.example.game_match.matchparticipation.domain.MatchParticipation;
import com.example.game_match.matchparticipation.domain.vo.MatchParticipationRole;

public record MatchParticipationResponseDto(
        Integer id,
        Integer friendlyMatchId,
        GameUserResponseDto gameUser,
        MatchParticipationRole role
) {
    public static MatchParticipationResponseDto from(MatchParticipation participation) {
        return new MatchParticipationResponseDto(
                participation.getId(),
                participation.getFriendlyMatchId(),
                GameUserResponseDto.from(participation.getGameUser()),
                participation.getRole()
        );
    }
}
