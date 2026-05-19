package com.example.game_match.recommendation.service;

import com.example.game_match.global.exception.BusinessException;
import com.example.game_match.global.exception.ErrorCode;
import com.example.game_match.post.domain.Post;
import com.example.game_match.post.repository.PostRepository;
import com.example.game_match.recommendation.domain.PostRecommendation;
import com.example.game_match.recommendation.domain.PostRecommendationId;
import com.example.game_match.recommendation.dto.PostVoteResponseDto;
import com.example.game_match.recommendation.repository.PostRecommendationRepository;
import com.example.game_match.user.domain.User;
import com.example.game_match.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostRecommendationService {
    private static final String RECOMMENDED_STATUS = "Recommended";
    private static final String NOT_RECOMMENDED_STATUS = "Not Recommended";
    private static final String NO_RECOMMENDATION_STATUS = "No recommendation exists";

    private final PostRecommendationRepository postRecommendationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 특정 게시글에 대한 로그인 사용자의 추천/비추천 상태를 문자열로 조회한다.
    @Transactional(readOnly = true)
    public String getRecommendationStatus(Integer postId, Integer userId) {
        findPost(postId);
        findUser(userId);

        return postRecommendationRepository.findById(PostRecommendationId.of(postId, userId))
                .map(recommendation -> statusText(recommendation.getGoodOrBad()))
                .orElse(NO_RECOMMENDATION_STATUS);
    }

    // 추천/비추천 버튼 클릭 한 번을 추천 기록과 게시글 카운터 변경까지 하나의 트랜잭션으로 처리한다.
    @Transactional
    public PostVoteResponseDto votePost(Integer postId, Integer userId, Boolean goodOrBad) {
        if (goodOrBad == null) {
            throw new IllegalArgumentException("추천 또는 비추천 값이 필요합니다.");
        }

        Post post = postRepository.findByIdForUpdate(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        User user = findUser(userId);
        PostRecommendationId id = PostRecommendationId.of(postId, userId);

        return postRecommendationRepository.findById(id)
                .map(recommendation -> toggleExistingVote(recommendation, post, id, goodOrBad))
                .orElseGet(() -> createVote(post, user, goodOrBad));
    }

    private PostVoteResponseDto createVote(Post post, User user, Boolean goodOrBad) {
        postRecommendationRepository.save(PostRecommendation.create(post, user, goodOrBad));
        applyVoteCount(post, goodOrBad, 1);

        return toVoteResponse(goodOrBad, post);
    }

    private PostVoteResponseDto toggleExistingVote(
            PostRecommendation recommendation,
            Post post,
            PostRecommendationId id,
            Boolean goodOrBad
    ) {
        if (!recommendation.isSameVote(goodOrBad)) {
            throw new BusinessException(
                    ErrorCode.POST_VOTE_CONFLICT,
                    "반대 상태로 바로 변경할 수 없습니다. 기존 추천/비추천을 먼저 취소해주세요."
            );
        }

        postRecommendationRepository.deleteById(id);
        applyVoteCount(post, goodOrBad, -1);

        return toVoteResponse(null, post);
    }

    private void applyVoteCount(Post post, Boolean goodOrBad, int change) {
        if (Boolean.TRUE.equals(goodOrBad)) {
            if (change > 0) {
                post.incrementRecommendations();
                return;
            }

            post.decrementRecommendations();
            return;
        }

        if (change > 0) {
            post.incrementDislikes();
            return;
        }

        post.decrementDislikes();
    }

    private PostVoteResponseDto toVoteResponse(Boolean status, Post post) {
        return new PostVoteResponseDto(
                statusText(status),
                currentCount(post.getRecommendations()),
                currentCount(post.getDislikes())
        );
    }

    private String statusText(Boolean status) {
        if (status == null) {
            return NO_RECOMMENDATION_STATUS;
        }

        return status ? RECOMMENDED_STATUS : NOT_RECOMMENDED_STATUS;
    }

    private int currentCount(Integer count) {
        return count == null ? 0 : count;
    }

    private Post findPost(Integer postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

    private User findUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
