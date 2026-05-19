package com.example.game_match.matchrequest.domain;

import com.example.game_match.gameuser.domain.GameUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "friendly_match_request_participation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "friendly_match_request_id",
                        columnNames = {"Friendly_match_request_id", "Game_user_id"}
                )
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchRequestParticipation {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Friendly_match_request_id", nullable = false)
    private MatchRequest matchRequest;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Game_user_id", nullable = false)
    private GameUser gameUser;

    private MatchRequestParticipation(MatchRequest matchRequest, GameUser gameUser) {
        validateRequired(matchRequest, "matchRequest");
        validateRequired(gameUser, "gameUser");

        this.matchRequest = matchRequest;
        this.gameUser = gameUser;
    }

    public static MatchRequestParticipation create(MatchRequest matchRequest, GameUser gameUser) {
        return new MatchRequestParticipation(matchRequest, gameUser);
    }

    public Integer getMatchRequestId() {
        return matchRequest.getId();
    }

    public Integer getGameUserId() {
        return gameUser.getId();
    }

    private static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + "은 필수입니다.");
        }
    }
}
