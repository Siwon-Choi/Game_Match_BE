package com.example.game_match.recommendation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostRecommendationId implements Serializable {
    @Column(name = "post_Id")
    private Integer postId;

    @Column(name = "user_Id")
    private Integer userId;

    private PostRecommendationId(Integer postId, Integer userId) {
        this.postId = postId;
        this.userId = userId;
    }

    public static PostRecommendationId of(Integer postId, Integer userId) {
        return new PostRecommendationId(postId, userId);
    }
}
