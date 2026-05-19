package com.example.game_match.comment.controller;

import com.example.game_match.comment.dto.CommentCreateRequestDto;
import com.example.game_match.comment.dto.CommentResponseDto;
import com.example.game_match.comment.dto.CommentUpdateRequestDto;
import com.example.game_match.comment.service.CommentService;
import com.example.game_match.global.response.CommonResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // 게시글에 새 댓글 또는 대댓글을 생성한다.
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommonResponse<CommentResponseDto>> createComment(
            @PathVariable Integer postId,
            @RequestBody CommentCreateRequestDto requestDto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.success(commentService.createComment(requestDto.toServiceDto(postId))));
    }

    // 게시글 ID 기준으로 최상위 댓글 목록을 조회한다.
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<CommonResponse<List<CommentResponseDto>>> getCommentsByPostId(
            @PathVariable Integer postId
    ) {
        return ResponseEntity.ok(CommonResponse.success(commentService.getCommentsByPostId(postId)));
    }

    // 댓글 ID 기준으로 댓글 단건을 조회한다.
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommonResponse<CommentResponseDto>> getCommentById(@PathVariable Integer commentId) {
        return ResponseEntity.ok(CommonResponse.success(commentService.getCommentById(commentId)));
    }

    // 댓글 ID 기준으로 댓글 내용을 수정한다.
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommonResponse<CommentResponseDto>> updateComment(
            @PathVariable Integer commentId,
            @RequestBody CommentUpdateRequestDto requestDto
    ) {
        return ResponseEntity.ok(CommonResponse.success(
                commentService.updateComment(commentId, requestDto.toServiceDto())
        ));
    }

    // 댓글 ID 기준으로 댓글을 삭제한다.
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CommonResponse<Void>> deleteComment(@PathVariable Integer commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(CommonResponse.successWithMessage("Comment deleted successfully."));
    }

    // 부모 댓글 ID 기준으로 답글 목록을 조회한다.
    @GetMapping("/comments/{commentId}/replies")
    public ResponseEntity<CommonResponse<List<CommentResponseDto>>> getRepliesByParentCommentId(
            @PathVariable Integer commentId
    ) {
        return ResponseEntity.ok(CommonResponse.success(commentService.getRepliesByParentCommentId(commentId)));
    }
}
