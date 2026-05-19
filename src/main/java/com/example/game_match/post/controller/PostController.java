package com.example.game_match.post.controller;

import com.example.game_match.global.response.CommonResponse;
import com.example.game_match.post.dto.PostCreateRequestDto;
import com.example.game_match.post.dto.PostResponseDto;
import com.example.game_match.post.dto.PostUpdateRequestDto;
import com.example.game_match.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    // 게시글을 생성한다.
    @PostMapping
    public ResponseEntity<CommonResponse<PostResponseDto>> createPost(
            @RequestBody PostCreateRequestDto requestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.success(postService.createPost(requestDto.toServiceDto())));
    }

    // 게시글 ID 기준으로 게시글을 수정한다.
    @PutMapping("/{postId}")
    public ResponseEntity<CommonResponse<PostResponseDto>> updatePost(
            @PathVariable Integer postId,
            @RequestBody PostUpdateRequestDto requestDto
    ) {
        return ResponseEntity.ok(CommonResponse.success(
                postService.updatePost(postId, requestDto.toServiceDto())
        ));
    }

    // 게시글 ID 기준으로 게시글을 삭제한다.
    @DeleteMapping("/{postId}")
    public ResponseEntity<CommonResponse<Void>> deletePost(@PathVariable Integer postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(CommonResponse.successWithMessage("Post deleted successfully."));
    }

    // 게임 이름 기준으로 게시글 목록을 페이지 단위로 조회한다.
    @GetMapping
    public ResponseEntity<CommonResponse<Page<PostResponseDto>>> getPosts(
            @RequestParam String gameName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(CommonResponse.success(postService.getPostsByGameName(gameName, page, size)));
    }

    // 게시글 ID 기준으로 게시글 상세 정보를 조회한다.
    @GetMapping("/{postId}")
    public ResponseEntity<CommonResponse<PostResponseDto>> getPostById(@PathVariable Integer postId) {
        return ResponseEntity.ok(CommonResponse.success(postService.getPostById(postId)));
    }

    // 게시글 조회수를 1 증가시킨다.
    @PostMapping("/{postId}/views")
    public ResponseEntity<CommonResponse<Void>> incrementViews(@PathVariable Integer postId) {
        postService.incrementViews(postId);
        return ResponseEntity.ok(CommonResponse.successWithMessage("Post view increased successfully."));
    }

}
