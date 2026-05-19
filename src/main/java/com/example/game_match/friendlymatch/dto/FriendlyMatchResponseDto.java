package com.example.game_match.friendlymatch.dto;

import com.example.game_match.friendlymatch.domain.FriendlyMatch;
import com.example.game_match.game.dto.GameResponseDto;
import com.example.game_match.gameuser.dto.GameUserResponseDto;
import java.time.LocalDate;
import java.time.LocalTime;

public record FriendlyMatchResponseDto(
        Integer id,
        GameUserResponseDto host,
        GameResponseDto game,
        LocalDate date,
        LocalTime time,
        Byte sort,
        Byte state,
        Integer recruit,
        String comment
) {
    public static FriendlyMatchResponseDto from(FriendlyMatch friendlyMatch) {
        return new FriendlyMatchResponseDto(
                friendlyMatch.getId(),
                GameUserResponseDto.from(friendlyMatch.getHost()),
                GameResponseDto.from(friendlyMatch.getGame()),
                friendlyMatch.getDate(),
                friendlyMatch.getTime(),
                friendlyMatch.getSort(),
                friendlyMatch.getState(),
                friendlyMatch.getRecruit(),
                friendlyMatch.getComment()
        );
    }
}
