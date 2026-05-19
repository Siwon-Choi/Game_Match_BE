package com.example.game_match.auth.repository.jpa;

import com.example.game_match.auth.domain.RefreshToken;
import com.example.game_match.auth.repository.RefreshTokenRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryJpaAdapter implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public Optional<RefreshToken> findByUserId(Integer userId) {
        return refreshTokenJpaRepository.findByUser_Id(userId);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenJpaRepository.save(refreshToken);
    }

    @Override
    public void deleteByUserId(Integer userId) {
        refreshTokenJpaRepository.deleteByUser_Id(userId);
    }
}
