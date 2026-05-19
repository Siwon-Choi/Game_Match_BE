package com.example.game_match.recommendation.repository.jpa;

import com.example.game_match.recommendation.domain.PostRecommendation;
import com.example.game_match.recommendation.domain.PostRecommendationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRecommendationJpaRepository extends JpaRepository<PostRecommendation, PostRecommendationId> {
    void deleteByPost_Id(Integer postId);
}
