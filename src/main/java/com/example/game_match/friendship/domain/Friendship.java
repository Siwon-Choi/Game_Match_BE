package com.example.game_match.friendship.domain;

import com.example.game_match.gameuser.domain.GameUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Friendship",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_friendship", columnNames = {"Game_User_Id_1", "Game_User_Id_2"})
        },
        indexes = {
                @Index(name = "idx_game_user_id_1", columnList = "Game_User_Id_1"),
                @Index(name = "idx_game_user_id_2", columnList = "Game_User_Id_2")
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friendship {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Game_User_Id_1", nullable = false)
    private GameUser gameUser1;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Game_User_Id_2", nullable = false)
    private GameUser gameUser2;

    private Friendship(GameUser gameUser1, GameUser gameUser2) {
        validateRequired(gameUser1, "gameUser1");
        validateRequired(gameUser2, "gameUser2");

        this.gameUser1 = gameUser1;
        this.gameUser2 = gameUser2;
    }

    public static Friendship create(GameUser gameUser1, GameUser gameUser2) {
        return new Friendship(gameUser1, gameUser2);
    }

    public Integer getGameUser1Id() {
        return gameUser1.getId();
    }

    public Integer getGameUser2Id() {
        return gameUser2.getId();
    }

    public Integer getFriendId(Integer gameUserId) {
        if (getGameUser1Id().equals(gameUserId)) {
            return getGameUser2Id();
        }

        return getGameUser1Id();
    }

    private static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + "은 필수입니다.");
        }
    }
}
