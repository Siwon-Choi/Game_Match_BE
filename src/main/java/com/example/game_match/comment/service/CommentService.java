package com.example.game_match.comment.service;

import com.example.game_match.comment.domain.Comment;
import com.example.game_match.comment.domain.vo.CommentContentVo;
import com.example.game_match.comment.dto.CommentCreateDto;
import com.example.game_match.comment.dto.CommentResponseDto;
import com.example.game_match.comment.dto.CommentUpdateDto;
import com.example.game_match.comment.repository.CommentRepository;
import com.example.game_match.global.exception.BusinessException;
import com.example.game_match.global.exception.ErrorCode;
import com.example.game_match.post.domain.Post;
import com.example.game_match.post.repository.PostRepository;
import com.example.game_match.user.domain.User;
import com.example.game_match.user.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 게시글에 새 댓글 또는 대댓글을 생성한다.
    @Transactional
    public CommentResponseDto createComment(CommentCreateDto dto) {
        User user = findUser(dto.userId());
        Post post = findPost(dto.postId());
        Comment parentComment = findParentComment(dto.parentCommentId());

        validateParentBelongsToPost(parentComment, post);

        Comment comment = Comment.create(
                user,
                CommentContentVo.from(dto.content()),
                dto.date(),
                dto.time(),
                post,
                parentComment,
                Boolean.TRUE.equals(dto.anonymous())
        );

        return CommentResponseDto.from(commentRepository.save(comment));
    }

    // 댓글 ID 기준으로 댓글 내용을 수정한다.
    @Transactional
    public CommentResponseDto updateComment(Integer commentId, CommentUpdateDto dto) {
        Comment comment = findComment(commentId);
        User user = findUser(dto.userId());
        Post post = findPost(dto.postId());
        Comment parentComment = findParentComment(dto.parentCommentId());

        validateParentBelongsToPost(parentComment, post);

        comment.update(
                user,
                CommentContentVo.from(dto.content()),
                dto.date(),
                dto.time(),
                post,
                parentComment,
                Boolean.TRUE.equals(dto.anonymous())
        );

        return CommentResponseDto.from(comment);
    }

    // 댓글 ID 기준으로 댓글을 삭제한다.
    @Transactional
    public void deleteComment(Integer commentId) {
        commentRepository.delete(findComment(commentId));
    }

    // 댓글 ID 기준으로 댓글 단건을 조회한다.
    @Transactional(readOnly = true)
    public CommentResponseDto getCommentById(Integer commentId) {
        return CommentResponseDto.from(findComment(commentId));
    }

    // 게시글 ID 기준으로 최상위 댓글만 조회한다.
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByPostId(Integer postId) {
        findPost(postId);

        return commentRepository.findByPostIdAndParentCommentIsNull(postId).stream()
                .map(CommentResponseDto::from)
                .toList();
    }

    // 부모 댓글 ID 기준으로 답글 목록을 조회한다.
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getRepliesByParentCommentId(Integer parentCommentId) {
        findComment(parentCommentId);

        return commentRepository.findByParentCommentId(parentCommentId).stream()
                .map(CommentResponseDto::from)
                .toList();
    }

    private User findUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Post findPost(Integer postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

    private Comment findParentComment(Integer parentCommentId) {
        if (parentCommentId == null) {
            return null;
        }

        return findComment(parentCommentId);
    }

    private Comment findComment(Integer commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private void validateParentBelongsToPost(Comment parentComment, Post post) {
        if (parentComment != null && !Objects.equals(parentComment.getPostId(), post.getId())) {
            throw new IllegalArgumentException("부모 댓글과 게시글이 일치하지 않습니다.");
        }
    }
}
