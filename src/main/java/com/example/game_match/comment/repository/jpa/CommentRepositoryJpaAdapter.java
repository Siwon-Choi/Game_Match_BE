package com.example.game_match.comment.repository.jpa;

import com.example.game_match.comment.domain.Comment;
import com.example.game_match.comment.repository.CommentRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryJpaAdapter implements CommentRepository {
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public Optional<Comment> findById(Integer id) {
        return commentJpaRepository.findById(id);
    }

    @Override
    public List<Comment> findByPostIdAndParentCommentIsNull(Integer postId) {
        return commentJpaRepository.findByPost_IdAndParentCommentIsNullOrderByDateAscTimeAscIdAsc(postId);
    }

    @Override
    public List<Comment> findByParentCommentId(Integer parentCommentId) {
        return commentJpaRepository.findByParentComment_IdOrderByDateAscTimeAscIdAsc(parentCommentId);
    }

    @Override
    public Comment save(Comment comment) {
        return commentJpaRepository.save(comment);
    }

    @Override
    public void delete(Comment comment) {
        commentJpaRepository.delete(comment);
    }

    @Override
    public void deleteRepliesByPostId(Integer postId) {
        commentJpaRepository.deleteByPost_IdAndParentCommentIsNotNull(postId);
    }

    @Override
    public void deleteTopLevelCommentsByPostId(Integer postId) {
        commentJpaRepository.deleteByPost_IdAndParentCommentIsNull(postId);
    }
}
