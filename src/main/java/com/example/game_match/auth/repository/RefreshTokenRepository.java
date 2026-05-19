package com.example.game_match.auth.repository;

import com.example.game_match.auth.domain.RefreshToken;
import java.util.Optional;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findByUserId(Integer userId);

    RefreshToken save(RefreshToken refreshToken);

    void deleteByUserId(Integer userId);
}
