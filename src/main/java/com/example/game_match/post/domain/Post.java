package com.example.game_match.post.domain;

import com.example.game_match.game.domain.Game;
import com.example.game_match.post.domain.vo.PostContentVo;
import com.example.game_match.post.domain.vo.PostContentVoConverter;
import com.example.game_match.post.domain.vo.PostTitleVo;
import com.example.game_match.post.domain.vo.PostTitleVoConverter;
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
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "title", nullable = false, length = 15)
    @Convert(converter = PostTitleVoConverter.class)
    private PostTitleVo title;

    @Getter
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Getter
    @Column(name = "views")
    private Integer views;

    @Getter
    @Column(name = "recommendations")
    private Integer recommendations;

    @Getter
    @Column(name = "dislikes")
    private Integer dislikes;

    @Column(name = "content", length = 45)
    @Convert(converter = PostContentVoConverter.class)
    private PostContentVo content;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Getter
    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Getter
    @Column(name = "anonymous", nullable = false)
    private Boolean anonymous;

    private Post(
            PostTitleVo title,
            PostContentVo content,
            User user,
            Game game,
            Boolean anonymous
    ) {
        validateRequired(title, "title");
        validateRequired(content, "content");
        validateRequired(user, "user");
        validateRequired(game, "game");
        validateRequired(anonymous, "anonymous");

        this.title = title;
        this.content = content;
        this.user = user;
        this.game = game;
        this.anonymous = anonymous;
        this.date = LocalDate.now();
        this.time = LocalTime.now();
        this.views = 0;
        this.recommendations = 0;
        this.dislikes = 0;
    }

    public static Post create(
            PostTitleVo title,
            PostContentVo content,
            User user,
            Game game,
            Boolean anonymous
    ) {
        return new Post(title, content, user, game, anonymous);
    }

    public void update(PostTitleVo title, PostContentVo content, User user, Game game, Boolean anonymous) {
        validateRequired(title, "title");
        validateRequired(content, "content");
        validateRequired(user, "user");
        validateRequired(game, "game");
        validateRequired(anonymous, "anonymous");

        this.title = title;
        this.content = content;
        this.user = user;
        this.game = game;
        this.anonymous = anonymous;
    }

    public void incrementRecommendations() {
        recommendations = recommendations == null ? 1 : recommendations + 1;
    }

    public void decrementRecommendations() {
        recommendations = recommendations == null || recommendations <= 0 ? 0 : recommendations - 1;
    }

    public void incrementDislikes() {
        dislikes = dislikes == null ? 1 : dislikes + 1;
    }

    public void decrementDislikes() {
        dislikes = dislikes == null || dislikes <= 0 ? 0 : dislikes - 1;
    }

    public String getTitle() {
        return title.getValue();
    }

    public String getContent() {
        return content.getValue();
    }

    public Integer getUserId() {
        return user.getId();
    }

    public Integer getGameId() {
        return game.getId();
    }

    private static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + "은 필수입니다.");
        }
    }
}
