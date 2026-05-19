package com.example.game_match.game.domain;

import com.example.game_match.game.domain.vo.GameNameVo;
import com.example.game_match.game.domain.vo.GameNameVoConverter;
import com.example.game_match.game.domain.vo.GameSortVo;
import com.example.game_match.game.domain.vo.GameSortVoConverter;
import com.example.game_match.game.domain.vo.GameUrlVo;
import com.example.game_match.game.domain.vo.GameUrlVoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Game")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Name", nullable = false, unique = true, length = 255)
    @Convert(converter = GameNameVoConverter.class)
    private GameNameVo name;

    @Column(name = "sort", nullable = false, length = 255)
    @Convert(converter = GameSortVoConverter.class)
    private GameSortVo sort;

    @Column(name = "url", length = 255)
    @Convert(converter = GameUrlVoConverter.class)
    private GameUrlVo url;

    private Game(GameNameVo name, GameSortVo sort, GameUrlVo url) {
        validateRequired(name, "name");
        validateRequired(sort, "sort");

        this.name = name;
        this.sort = sort;
        this.url = url == null ? GameUrlVo.from(null) : url;
    }

    public static Game create(GameNameVo name, GameSortVo sort, GameUrlVo url) {
        return new Game(name, sort, url);
    }

    public String getName() {
        return name.getValue();
    }

    public String getSort() {
        return sort.getValue();
    }

    public String getUrl() {
        return url == null ? null : url.getValue();
    }

    private static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + "은 필수입니다.");
        }
    }
}
