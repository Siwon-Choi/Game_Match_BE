package com.example.game_match.gamegroup.domain;

import com.example.game_match.game.domain.Game;
import com.example.game_match.gamegroup.domain.vo.GameGroupNameVo;
import com.example.game_match.gamegroup.domain.vo.GameGroupNameVoConverter;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`Group`")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameGroup {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Game_Id", nullable = false)
    private Game game;

    @Column(name = "Name", nullable = false, length = 255)
    @Convert(converter = GameGroupNameVoConverter.class)
    private GameGroupNameVo name;

    private GameGroup(Game game, GameGroupNameVo name) {
        validateRequired(game, "game");
        validateRequired(name, "name");

        this.game = game;
        this.name = name;
    }

    public static GameGroup create(Game game, GameGroupNameVo name) {
        return new GameGroup(game, name);
    }

    public void updateName(GameGroupNameVo name) {
        validateRequired(name, "name");
        this.name = name;
    }

    public String getName() {
        return name.getValue();
    }

    private static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + "은 필수입니다.");
        }
    }
}
