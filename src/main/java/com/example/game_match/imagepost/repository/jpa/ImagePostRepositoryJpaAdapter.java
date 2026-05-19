package com.example.game_match.imagepost.repository.jpa;

import com.example.game_match.imagepost.domain.ImagePost;
import com.example.game_match.imagepost.repository.ImagePostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ImagePostRepositoryJpaAdapter implements ImagePostRepository {
    private final ImagePostJpaRepository imagePostJpaRepository;

    @Override
    public List<ImagePost> findAllByPostId(Integer postId) {
        return imagePostJpaRepository.findAllByPost_Id(postId);
    }

    @Override
    public ImagePost save(ImagePost imagePost) {
        return imagePostJpaRepository.save(imagePost);
    }

    @Override
    public void deleteByPostId(Integer postId) {
        imagePostJpaRepository.deleteByPost_Id(postId);
    }
}
