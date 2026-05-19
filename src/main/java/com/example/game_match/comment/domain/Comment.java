package com.example.game_match.comment.domain;

import com.example.game_match.comment.domain.vo.CommentContentVo;
import com.example.game_match.comment.domain.vo.CommentContentVoConverter;
import com.example.game_match.post.domain.Post;
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
@Table(name = "Comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_Id", nullable = false)
    private User user;

    @Column(name = "content", nullable = false, length = 45)
    @Convert(converter = CommentContentVoConverter.class)
    private CommentContentVo content;

    @Getter
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Getter
    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_Id", nullable = false)
    private Post post;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_Id")
    private Comment parentComment;

    @Getter
    @Column(name = "anonymous", nullable = false)
    private Boolean anonymous;

    private Comment(
            User user,
            CommentContentVo content,
            LocalDate date,
            LocalTime time,
            Post post,
            Comment parentComment,
            Boolean anonymous
    ) {
        validateRequired(user, "user");
        validateRequired(content, "content");
        validateRequired(date, "date");
        validateRequired(time, "time");
        validateRequired(post, "post");
        validateRequired(anonymous, "anonymous");

        this.user = user;
        this.content = content;
        this.date = date;
        this.time = time;
        this.post = post;
        this.parentComment = parentComment;
        this.anonymous = anonymous;
    }

    public static Comment create(
            User user,
            CommentContentVo content,
            LocalDate date,
            LocalTime time,
            Post post,
            Comment parentComment,
            Boolean anonymous
    ) {
        return new Comment(user, content, date, time, post, parentComment, anonymous);
    }

    public void update(
            User user,
            CommentContentVo content,
            LocalDate date,
            LocalTime time,
            Post post,
            Comment parentComment,
            Boolean anonymous
    ) {
        validateRequired(user, "user");
        validateRequired(content, "content");
        validateRequired(date, "date");
        validateRequired(time, "time");
        validateRequired(post, "post");
        validateRequired(anonymous, "anonymous");

        this.user = user;
        this.content = content;
        this.date = date;
        this.time = time;
        this.post = post;
        this.parentComment = parentComment;
        this.anonymous = anonymous;
    }

    public String getContent() {
        return content.getValue();
    }

    public Integer getUserId() {
        return user.getId();
    }

    public Integer getPostId() {
        return post.getId();
    }

    public Integer getParentCommentId() {
        return parentComment == null ? null : parentComment.getId();
    }

    private static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + "은 필수입니다.");
        }
    }
}
