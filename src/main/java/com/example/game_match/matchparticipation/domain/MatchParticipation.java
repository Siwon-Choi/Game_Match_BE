package com.example.game_match.matchparticipation.domain;

import com.example.game_match.friendlymatch.domain.FriendlyMatch;
import com.example.game_match.gameuser.domain.GameUser;
import com.example.game_match.matchparticipation.domain.vo.MatchParticipationRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "friendly_match_participation",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_participation", columnNames = {"Game_User_Id", "Friendly_Match_Id"})
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchParticipation {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Game_User_Id", nullable = false)
    private GameUser gameUser;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Friendly_Match_Id", nullable = false)
    private FriendlyMatch friendlyMatch;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "Role", nullable = false)
    private MatchParticipationRole role;

    private MatchParticipation(GameUser gameUser, FriendlyMatch friendlyMatch, MatchParticipationRole role) {
        validateRequired(gameUser, "gameUser");
        validateRequired(friendlyMatch, "friendlyMatch");
        validateRequired(role, "role");

        this.gameUser = gameUser;
        this.friendlyMatch = friendlyMatch;
        this.role = role;
    }

    public static MatchParticipation create(
            GameUser gameUser,
            FriendlyMatch friendlyMatch,
            MatchParticipationRole role
    ) {
        return new MatchParticipation(gameUser, friendlyMatch, role);
    }

    public Integer getGameUserId() {
        return gameUser.getId();
    }

    public Integer getFriendlyMatchId() {
        return friendlyMatch.getId();
    }

    private static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + "은 필수입니다.");
        }
    }
}
