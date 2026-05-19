package com.example.game_match.imagepost.repository.jpa;

import com.example.game_match.imagepost.domain.ImagePost;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagePostJpaRepository extends JpaRepository<ImagePost, String> {
    @EntityGraph(attributePaths = {"post"})
    List<ImagePost> findAllByPost_Id(Integer postId);

    void deleteByPost_Id(Integer postId);
}
