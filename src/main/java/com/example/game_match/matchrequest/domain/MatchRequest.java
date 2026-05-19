package com.example.game_match.matchrequest.domain;

import com.example.game_match.friendlymatch.domain.FriendlyMatch;
import com.example.game_match.gameuser.domain.GameUser;
import com.example.game_match.matchrequest.domain.vo.MatchRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "friendly_match_request",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_request", columnNames = {"Game_user_Id", "Friendly_Match_Id"})
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchRequest {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Game_user_Id", nullable = false)
    private GameUser gameUser;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Friendly_Match_Id", nullable = false)
    private FriendlyMatch friendlyMatch;

    @Getter
    @Column(name = "Comment", length = 255)
    private String comment;

    @Getter
    @Column(name = "status", nullable = false)
    private MatchRequestStatus status;

    @Getter
    @Column(name = "Updated_At", nullable = false)
    private LocalDateTime updatedAt;

    private MatchRequest(
            GameUser gameUser,
            FriendlyMatch friendlyMatch,
            String comment,
            MatchRequestStatus status
    ) {
        validateRequired(gameUser, "gameUser");
        validateRequired(friendlyMatch, "friendlyMatch");
        validateRequired(status, "status");

        this.gameUser = gameUser;
        this.friendlyMatch = friendlyMatch;
        this.comment = comment;
        this.status = status;
    }

    public static MatchRequest create(GameUser gameUser, FriendlyMatch friendlyMatch, String comment) {
        return new MatchRequest(gameUser, friendlyMatch, comment, MatchRequestStatus.await);
    }

    public void updateComment(String comment) {
        this.comment = comment;
    }

    public void updateStatus(MatchRequestStatus status) {
        validateRequired(status, "status");
        this.status = status;
    }

    public boolean isActive() {
        return status == MatchRequestStatus.await || status == MatchRequestStatus.approve;
    }

    public boolean isSelected() {
        return status == MatchRequestStatus.approve;
    }

    public Integer getGameUserId() {
        return gameUser.getId();
    }

    public Integer getFriendlyMatchId() {
        return friendlyMatch.getId();
    }

    @PrePersist
    void onCreate() {
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + "은 필수입니다.");
        }
    }
}
