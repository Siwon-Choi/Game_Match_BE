package com.example.game_match.recommendation.domain;

import com.example.game_match.post.domain.Post;
import com.example.game_match.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Post_Recommendation_User")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostRecommendation {
    @Getter
    @EmbeddedId
    private PostRecommendationId id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_Id", nullable = false)
    private Post post;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_Id", nullable = false)
    private User user;

    @Getter
    @Column(name = "goodorbad", nullable = false)
    private Boolean goodOrBad;

    private PostRecommendation(Post post, User user, Boolean goodOrBad) {
        validateRequired(post, "post");
        validateRequired(user, "user");
        validateRequired(goodOrBad, "goodOrBad");

        this.id = PostRecommendationId.of(post.getId(), user.getId());
        this.post = post;
        this.user = user;
        this.goodOrBad = goodOrBad;
    }

    public static PostRecommendation create(Post post, User user, Boolean goodOrBad) {
        return new PostRecommendation(post, user, goodOrBad);
    }

    public boolean isSameVote(Boolean nextGoodOrBad) {
        return goodOrBad.equals(nextGoodOrBad);
    }

    private static void validateRequired(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + "은 필수입니다.");
        }
    }
}
