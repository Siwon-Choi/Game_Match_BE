package com.example.game_match.recommendation.controller;

import com.example.game_match.global.exception.ErrorCode;
import com.example.game_match.global.response.CommonResponse;
import com.example.game_match.recommendation.dto.PostVoteRequestDto;
import com.example.game_match.recommendation.dto.PostVoteResponseDto;
import com.example.game_match.recommendation.service.PostRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostRecommendationController {
    private final PostRecommendationService postRecommendationService;

    // 로그인한 사용자가 특정 게시글에 추천/비추천을 눌렀는지 조회한다.
    @GetMapping("/posts/{postId}/votes/me")
    public ResponseEntity<CommonResponse<String>> getMyRecommendationStatus(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer userId
    ) {
        if (userId == null) {
            return ResponseEntity
                    .status(ErrorCode.UNAUTHORIZED.getStatus())
                    .body(CommonResponse.error(ErrorCode.UNAUTHORIZED));
        }

        return ResponseEntity.ok(CommonResponse.success(
                postRecommendationService.getRecommendationStatus(postId, userId)
        ));
    }

    // 로그인한 사용자의 추천/비추천을 토글하고, 게시글 카운터를 함께 갱신한다.
    @PostMapping("/posts/{postId}/votes")
    public ResponseEntity<CommonResponse<PostVoteResponseDto>> votePost(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer userId,
            @RequestBody PostVoteRequestDto requestDto
    ) {
        if (userId == null) {
            return ResponseEntity
                    .status(ErrorCode.UNAUTHORIZED.getStatus())
                    .body(CommonResponse.error(ErrorCode.UNAUTHORIZED));
        }

        return ResponseEntity.ok(CommonResponse.success(
                postRecommendationService.votePost(postId, userId, requestDto.goodOrBad())
        ));
    }
}
