package com.example.game_match.auth.repository.jpa;

import com.example.game_match.auth.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByUser_Id(Integer userId);

    void deleteByUser_Id(Integer userId);
}
