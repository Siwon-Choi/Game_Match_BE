package com.example.game_match.recommendation.repository;

import com.example.game_match.recommendation.domain.PostRecommendation;
import com.example.game_match.recommendation.domain.PostRecommendationId;
import java.util.Optional;

public interface PostRecommendationRepository {
    Optional<PostRecommendation> findById(PostRecommendationId id);

    PostRecommendation save(PostRecommendation postRecommendation);

    void deleteById(PostRecommendationId id);

    void deleteByPostId(Integer postId);
}
