package com.example.game_match.post.repository.jpa;

import com.example.game_match.game.domain.Game;
import com.example.game_match.post.domain.Post;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostJpaRepository extends JpaRepository<Post, Integer> {
    @NotNull
    @Override
    @EntityGraph(attributePaths = {"user", "game"})
    Optional<Post> findById(@NotNull Integer id);

    @EntityGraph(attributePaths = {"user", "game"})
    Page<Post> findByGame(Game game, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Post p where p.id = :postId")
    Optional<Post> findByIdForUpdate(@Param("postId") Integer postId);

    @Modifying
    @Query("update Post p set p.views = coalesce(p.views, 0) + 1 where p.id = :postId")
    void incrementViews(@Param("postId") Integer postId);
}
