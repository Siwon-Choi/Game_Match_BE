package com.example.game_match.post.repository.jpa;

import com.example.game_match.game.domain.Game;
import com.example.game_match.post.domain.Post;
import com.example.game_match.post.repository.PostRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryJpaAdapter implements PostRepository {
    private final PostJpaRepository postJpaRepository;

    @Override
    public Optional<Post> findById(Integer id) {
        return postJpaRepository.findById(id);
    }

    @Override
    public Optional<Post> findByIdForUpdate(Integer postId) {
        return postJpaRepository.findByIdForUpdate(postId);
    }

    @Override
    public Page<Post> findByGame(Game game, Pageable pageable) {
        return postJpaRepository.findByGame(game, pageable);
    }

    @Override
    public Post save(Post post) {
        return postJpaRepository.save(post);
    }

    @Override
    public void deleteById(Integer postId) {
        postJpaRepository.deleteById(postId);
    }

    @Override
    public void incrementViews(Integer postId) {
        postJpaRepository.incrementViews(postId);
    }
}
