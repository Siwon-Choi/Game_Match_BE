package com.example.game_match.friendlymatch.domain;

import com.example.game_match.friendlymatch.domain.vo.FriendlyMatchCommentVo;
import com.example.game_match.friendlymatch.domain.vo.FriendlyMatchCommentVoConverter;
import com.example.game_match.friendlymatch.domain.vo.FriendlyMatchRecruitVo;
import com.example.game_match.friendlymatch.domain.vo.FriendlyMatchRecruitVoConverter;
import com.example.game_match.friendlymatch.domain.vo.FriendlyMatchSortVo;
import com.example.game_match.friendlymatch.domain.vo.FriendlyMatchSortVoConverter;
import com.example.game_match.friendlymatch.domain.vo.FriendlyMatchStateVo;
import com.example.game_match.friendlymatch.domain.vo.FriendlyMatchStateVoConverter;
import com.example.game_match.game.domain.Game;
import com.example.game_match.gameuser.domain.GameUser;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "friendly_match")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendlyMatch {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Host_Id", nullable = false)
    private GameUser host;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Game_Id", nullable = false)
    private Game game;

    @Getter
    @Column(name = "Date", nullable = false)
    private LocalDate date;

    @Getter
    @Column(name = "Time", nullable = false)
    private LocalTime time;

    @Column(name = "Sort", nullable = false)
    @Convert(converter = FriendlyMatchSortVoConverter.class)
    private FriendlyMatchSortVo sort;

    @Column(name = "State", nullable = false)
    @Convert(converter = FriendlyMatchStateVoConverter.class)
    private FriendlyMatchStateVo state;

    @Column(name = "Recruit", nullable = false)
    @Convert(converter = FriendlyMatchRecruitVoConverter.class)
    private FriendlyMatchRecruitVo recruit;

    @Column(name = "Comment", length = 255)
    @Convert(converter = FriendlyMatchCommentVoConverter.class)
    private FriendlyMatchCommentVo comment;

    private FriendlyMatch(
            GameUser host,
            Game game,
            LocalDate date,
            LocalTime time,
            FriendlyMatchSortVo sort,
            FriendlyMatchStateVo state,
            FriendlyMatchRecruitVo recruit,
            FriendlyMatchCommentVo comment
    ) {
        validateRequired(host, "host");
        validateRequired(game, "game");
        validateRequired(date, "date");
        validateRequired(time, "time");
        validateRequired(sort, "sort");
        validateRequired(state, "state");
        validateRequired(recruit, "recruit");

        this.host = host;
        this.game = game;
        this.date = date;
        this.time = time;
        this.sort = sort;
        this.state = state;
        this.recruit = recruit;
        this.comment = comment == null ? FriendlyMatchCommentVo.from(null) : comment;
    }

    public static FriendlyMatch create(
            GameUser host,
            Game game,
            LocalDate date,
            LocalTime time,
            FriendlyMatchSortVo sort,
            FriendlyMatchStateVo state,
            FriendlyMatchRecruitVo recruit,
            FriendlyMatchCommentVo comment
    ) {
        return new FriendlyMatch(host, game, date, time, sort, state, recruit, comment);
    }

    public void updateState(FriendlyMatchStateVo state) {
        validateRequired(state, "state");
        this.state = state;
    }

    public LocalDateTime getScheduledAt() {
        return LocalDateTime.of(date, time);
    }

    public Byte getSort() {
        return sort.getValue();
    }

    public Byte getState() {
        return state.getValue();
    }

    public Integer getRecruit() {
        return recruit.getValue();
    }

    public String getComment() {
        return comment == null ? null : comment.getValue();
    }

    private static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + "은 필수입니다.");
        }
    }
}
