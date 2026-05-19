package com.example.game_match.imagepost.repository;

import com.example.game_match.imagepost.domain.ImagePost;
import java.util.List;

public interface ImagePostRepository {
    List<ImagePost> findAllByPostId(Integer postId);

    ImagePost save(ImagePost imagePost);

    void deleteByPostId(Integer postId);
}
