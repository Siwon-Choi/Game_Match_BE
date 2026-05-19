package com.example.game_match.auth.service;

import com.example.game_match.user.domain.User;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Component;

// 해당 클래스를 Spring Bean으로 등록
@Component
public class JwtTokenProvider {
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    // JwtEncoder, JwtDecoder => Spring Security OAuth2 JWT 라이브러리가 제공하는 인터페이스인데,
    // 실제 구현체(NimbusJwtEncoder, NimbusJwtDecoder)는 JwtConfig에서 Bean으로 등록해 주입받는다.
    // accessTokenDecoder의 경우에는 Spring Security Filter Chain이 처리하기 때문에 없다.
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder refreshTokenDecoder;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtTokenProvider(
            JwtEncoder jwtEncoder,
            @Qualifier("refreshTokenDecoder") JwtDecoder refreshTokenDecoder,
            @Value("${jwt.access-token-expiration-ms:3600000}") long accessTokenExpirationMs,
            @Value("${jwt.refresh-token-expiration-ms:31536000000}") long refreshTokenExpirationMs) {
        this.jwtEncoder = jwtEncoder;
        this.refreshTokenDecoder = refreshTokenDecoder;
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    public String createAccessToken(User user) {
        return createToken(user, ACCESS_TOKEN_TYPE, accessTokenExpirationMs);
    }

    public String createRefreshToken(User user) {
        return createToken(user, REFRESH_TOKEN_TYPE, refreshTokenExpirationMs);
    }

    // refresh token을 검증하고,
    // 그 안에서 userId를 꺼내 Optional<Integer>로 반환하는 메서드
    public Optional<Integer> resolveUserIdFromRefreshToken(String refreshToken) {

        // null / 빈 문자열 검사
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            // refreshToken을 검증한다.
            Jwt jwt = refreshTokenDecoder.decode(refreshToken.trim());
            // 해당하는 UserId를 반환한다.
            return parseUserId(jwt);
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private String createToken(User user, String tokenType, long expirationMs) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(expirationMs);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(String.valueOf(user.getId()))
                .claim("name", user.getName())
                .claim("type", tokenType)
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

    // UserId 반환

    private Optional<Integer> parseUserId(Jwt jwt) {
        try {
            return Optional.of(Integer.valueOf(jwt.getSubject()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
