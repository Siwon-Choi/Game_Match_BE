package com.example.game_match.comment.repository.jpa;

import com.example.game_match.comment.domain.Comment;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, Integer> {
    @NotNull
    @Override
    @EntityGraph(attributePaths = {"user", "post", "parentComment"})
    Optional<Comment> findById(@NotNull Integer id);

    @EntityGraph(attributePaths = {"user", "post", "parentComment"})
    List<Comment> findByPost_IdAndParentCommentIsNullOrderByDateAscTimeAscIdAsc(Integer postId);

    @EntityGraph(attributePaths = {"user", "post", "parentComment"})
    List<Comment> findByParentComment_IdOrderByDateAscTimeAscIdAsc(Integer parentCommentId);

    void deleteByPost_IdAndParentCommentIsNotNull(Integer postId);

    void deleteByPost_IdAndParentCommentIsNull(Integer postId);
}
