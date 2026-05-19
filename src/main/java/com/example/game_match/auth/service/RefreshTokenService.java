package com.example.game_match.auth.service;

import com.example.game_match.auth.domain.RefreshToken;
import com.example.game_match.auth.domain.vo.TokenHashVo;
import com.example.game_match.auth.repository.RefreshTokenRepository;
import com.example.game_match.user.domain.User;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveOrReplace(User user, String refreshToken) {
        TokenHashVo nextTokenHash = TokenHashVo.from(hash(refreshToken));
        RefreshToken storedToken = refreshTokenRepository.findByUserId(user.getId())
                .orElseGet(() -> RefreshToken.create(user, nextTokenHash));

        storedToken.updateTokenHash(nextTokenHash);
        refreshTokenRepository.save(storedToken);
    }

    // 현재 refresh token이 DB에 저장된 토큰과 맞으면,
    // 새 refresh token으로 교체하고 true 반환
    // 안 맞으면 false 반환
    @Transactional
    public boolean rotate(User user, String currentRefreshToken, String nextRefreshToken) {

        // 현재 토큰 해시
        String currentTokenHash = hash(currentRefreshToken);
        // 새 토큰 해시
        TokenHashVo nextTokenHash = TokenHashVo.from(hash(nextRefreshToken));

        // user를 id로 찾아서
        return refreshTokenRepository.findByUserId(user.getId())
                // db의 token과 currentToken이 같은지를 확인
                .filter(storedToken -> storedToken.getTokenHash().equals(currentTokenHash))
                // 저장된 토큰 새 토큰으로 교체
                .map(storedToken -> {
                    // 영속 엔티
                    storedToken.updateTokenHash(nextTokenHash);
                    return true;
                })
                .orElse(false);
    }


    @Transactional
    public void deleteIfMatches(Integer userId, String refreshToken) {

        // 요청으로 받은 refresh token 원문을 DB 저장 방식과 동일하게 해시한다.
        String tokenHash = hash(refreshToken);

        // userId로 DB에 저장된 refresh token 정보를 조회한다.
        refreshTokenRepository.findByUserId(userId)
                // DB에 저장된 token hash와 요청 token의 hash가 일치하는 경우만 통과시킨다.
                .filter(storedToken -> storedToken.getTokenHash().equals(tokenHash))
                // 일치하면 해당 사용자의 refresh token 저장 값을 삭제해서 재발급을 막는다.
                .ifPresent(storedToken -> refreshTokenRepository.deleteByUserId(userId));
    }


    // 해시
    private String hash(String refreshToken) {
        // refresh token이 없거나 공백뿐이면 비교가 실패하도록 빈 문자열을 반환한다.
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return "";
        }

        try {
            // SHA-256 해시 알고리즘을 사용하는 MessageDigest 객체를 생성한다.
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // refresh token 문자열을 UTF-8 byte 배열로 변환한 뒤 SHA-256 해시를 계산한다.
            byte[] hashed = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            // 해시 결과 byte 배열을 URL-safe Base64 문자열로 변환하고, 끝의 padding(=)은 제거한다.
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256은 Java 기본 알고리즘이라 일반적으로 발생하지 않지만, 발생하면 서버 환경 문제로 보고 예외 처리한다.
            throw new IllegalStateException("refresh token 해시를 생성할 수 없습니다.", e);
        }
    }
}
