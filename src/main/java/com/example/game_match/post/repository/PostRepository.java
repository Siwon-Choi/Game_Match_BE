package com.example.game_match.post.repository;

import com.example.game_match.game.domain.Game;
import com.example.game_match.post.domain.Post;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository {
    Optional<Post> findById(Integer id);

    Optional<Post> findByIdForUpdate(Integer postId);

    Page<Post> findByGame(Game game, Pageable pageable);

    Post save(Post post);

    void deleteById(Integer postId);

    void incrementViews(Integer postId);
}
