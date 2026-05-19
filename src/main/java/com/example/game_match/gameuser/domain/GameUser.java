package com.example.game_match.gameuser.domain;

import com.example.game_match.game.domain.Game;
import com.example.game_match.gamegroup.domain.GameGroup;
import com.example.game_match.gameuser.domain.vo.GameUserNicknameVo;
import com.example.game_match.gameuser.domain.vo.GameUserNicknameVoConverter;
import com.example.game_match.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
@Table(name = "game_user",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_user_game", columnNames = {"User_Id", "Game_Id"}),
                @UniqueConstraint(name = "Nickname", columnNames = {"Nickname", "Game_Id"})
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameUser {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_Id", nullable = false)
    private User user;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Game_Id", nullable = false)
    private Game game;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Group_Id")
    private GameGroup group;

    @Column(name = "Nickname", nullable = false, length = 255)
    @Convert(converter = GameUserNicknameVoConverter.class)
    private GameUserNicknameVo nickname;

    private GameUser(User user, Game game, GameGroup group, GameUserNicknameVo nickname) {
        validateRequired(user, "user");
        validateRequired(game, "game");
        validateRequired(nickname, "nickname");

        this.user = user;
        this.game = game;
        this.group = group;
        this.nickname = nickname;
    }

    public static GameUser create(User user, Game game, GameGroup group, GameUserNicknameVo nickname) {
        return new GameUser(user, game, group, nickname);
    }

    public void updateNickname(GameUserNicknameVo nickname) {
        validateRequired(nickname, "nickname");
        this.nickname = nickname;
    }

    public void updateGroup(GameGroup group) {
        this.group = group;
    }

    public String getNickname() {
        return nickname.getValue();
    }

    public Integer getUserId() {
        return user.getId();
    }

    public Integer getGameId() {
        return game.getId();
    }

    public Integer getGroupId() {
        return group == null ? null : group.getId();
    }

    private static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + "은 필수입니다.");
        }
    }
}
