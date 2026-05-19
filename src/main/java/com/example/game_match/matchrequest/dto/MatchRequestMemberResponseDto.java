package com.example.game_match.matchrequest.dto;

import com.example.game_match.gameuser.domain.GameUser;

public record MatchRequestMemberResponseDto(
        Integer gameUserId,
        String nickname,
        Integer groupId,
        String groupName
) {
    public static MatchRequestMemberResponseDto from(GameUser gameUser) {
        return new MatchRequestMemberResponseDto(
                gameUser.getId(),
                gameUser.getNickname(),
                gameUser.getGroupId(),
                gameUser.getGroup() == null ? null : gameUser.getGroup().getName()
        );
    }
}
