package com.example.game_match.recommendation.repository.jpa;

import com.example.game_match.recommendation.domain.PostRecommendation;
import com.example.game_match.recommendation.domain.PostRecommendationId;
import com.example.game_match.recommendation.repository.PostRecommendationRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRecommendationRepositoryJpaAdapter implements PostRecommendationRepository {
    private final PostRecommendationJpaRepository postRecommendationJpaRepository;

    @Override
    public Optional<PostRecommendation> findById(PostRecommendationId id) {
        return postRecommendationJpaRepository.findById(id);
    }

    @Override
    public PostRecommendation save(PostRecommendation postRecommendation) {
        return postRecommendationJpaRepository.save(postRecommendation);
    }

    @Override
    public void deleteById(PostRecommendationId id) {
        postRecommendationJpaRepository.deleteById(id);
    }

    @Override
    public void deleteByPostId(Integer postId) {
        postRecommendationJpaRepository.deleteByPost_Id(postId);
    }
}
