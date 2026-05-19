package com.example.game_match.comment.repository;

import com.example.game_match.comment.domain.Comment;
import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Optional<Comment> findById(Integer id);

    List<Comment> findByPostIdAndParentCommentIsNull(Integer postId);

    List<Comment> findByParentCommentId(Integer parentCommentId);

    Comment save(Comment comment);

    void delete(Comment comment);

    void deleteRepliesByPostId(Integer postId);

    void deleteTopLevelCommentsByPostId(Integer postId);
}
