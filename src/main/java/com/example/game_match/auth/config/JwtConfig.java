package com.example.game_match.auth.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

// 설정 클래스 -> 안에 @Bean 등록 메서드가 있을 수 있음
@Configuration
// JWT 관련 Bean과 검증 규칙을 등록하는 설정 클래스
public class JwtConfig {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";


    // 구성: jwtSecretKey, jwtEncoder, accessTokenDecoder, refreshTokenDecoder, createDecoder, accessTokenValidator

    @Bean
    // 설정값으로 들어온 JWT secret 문자열을 JWT 서명에 쓸 수 있는 SecretKey Bean으로 바꾼다.
    public SecretKey jwtSecretKey(@Value("${jwt.secret}") String secret) {

        // jwt 서명에 쓸 secret key를 문자열이 아닌 byte 배열로 만든다.
        byte[] keyBytes;

        try {
            // trim : 앞뒤 공백 제거
            // Base64 문자열을 원래 byte로 바꾼다.
            keyBytes = Base64.getDecoder().decode(secret.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT_SECRET은 Base64로 인코딩된 문자열이어야 합니다.", e);
        }

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET은 HS256 사용을 위해 Base64 디코딩 시 32바이트 이상이어야 합니다.");
        }

        // SecretKey 반환
        return new SecretKeySpec(keyBytes, HMAC_ALGORITHM);
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        // NimbusJwtEncoder: Spring Security OAuth2 JWT에서 제공하는 JwtEncoder 구현체
        // ImmutableSecret<>(jwtSecretKey): NimbusJwtEncoder는 SecretKey를 직접 받는 것 이 아닌, 키를 제공해주는 객체를 받아야하는데,
        // 그러한 객체를 JWK Source라 하고(쓰는 이유는 jwt 시스템에서는 키가 여러개 일 수 있는데, 관리하기에는 객체가 좋기 때문이다.)
        // 여기서는 키가 하나 뿐이니, 간단한 어댑터인 ImmutableSecret을 쓴다. (해당 키를 고정된 키 소스로 사용한다.)
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }


    // access token 검증용 JwtDecoder Bean을 등록
    @Bean
    public JwtDecoder accessTokenDecoder(SecretKey jwtSecretKey) {
        // 해당 jwtSecretKey로 jwtDecoder 생성
        NimbusJwtDecoder jwtDecoder = createDecoder(jwtSecretKey);

        // 검증은 인자들에게 담당
        // DelegatingOAuth2TokenValidator: validator를 묶어서 실행해주는 검증기
        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                // Spring Security가 제공하는 기본 JWT 검증기 -> 기본 claim을 검사 (ex: 만료 시간 -> 토큰 만료 검사를 한다.)
                JwtValidators.createDefault(),
                // payload의 type을 검사 -> refresh token으로 일반 api를 호출하는 것을 막음 + sub에 사용자 아이디가 있는지 검사
                accessTokenValidator()
        ));

        return jwtDecoder;
    }

    // refreshToken 검증용 JwtDecoder Bean을 등록
    @Bean
    public JwtDecoder refreshTokenDecoder(SecretKey jwtSecretKey) {
        NimbusJwtDecoder jwtDecoder = createDecoder(jwtSecretKey);

        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(),
                refreshTokenValidator()
        ));

        return jwtDecoder;
    }

    // JWT 문자열을 검증하고 해석할 수 있는 NimbusJwtDecoder를 만드는 공통 메서드
    private NimbusJwtDecoder createDecoder(SecretKey jwtSecretKey) {
        return NimbusJwtDecoder
                // 해당 비밀키로 서명을 검증하는 decoder
                .withSecretKey(jwtSecretKey)
                // jwt 서명 알고리즘은 HS256으로 검증
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        // 디코더의 역할
        // 1. JWT 문자열 파싱
        // 2. 서명 검증
        // 3. 만료 시간 등 기본 claim 검증
        // 4. 성공하면 Jwt 객체 반환
        // 5. 실패하면 예외 발생
    }


    // access token 검증기
    // OAuth2TokenValidator<Jwt> -> 이건 스프링 시큐리티가 제공하는 검증기 인터페이스
    private OAuth2TokenValidator<Jwt> accessTokenValidator() {
        return jwt -> {
            if (!ACCESS_TOKEN_TYPE.equals(jwt.getClaimAsString("type"))) {
                // type은 access token이어야한다.
                return OAuth2TokenValidatorResult.failure(new OAuth2Error(
                        "invalid_token",
                        "access token만 API 인증에 사용할 수 있습니다.",
                        null
                ));
            }

            // 통과하더라도 jwt의 sub는 Integer이어야한다.
            if (!hasIntegerSubject(jwt)) {
                return OAuth2TokenValidatorResult.failure(new OAuth2Error(
                        "invalid_token",
                        "JWT subject에는 사용자 ID가 필요합니다.",
                        null
                ));
            }

            // 모두 성공하면 success를 반환하는 검증기
            return OAuth2TokenValidatorResult.success();
        };
    }



    // refresh token 검증기
    private OAuth2TokenValidator<Jwt> refreshTokenValidator() {
        // type 검사 -> refresh token
        return jwt -> REFRESH_TOKEN_TYPE.equals(jwt.getClaimAsString("type"))
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error(
                        "invalid_token",
                        "refresh token만 재발급에 사용할 수 있습니다.",
                        null
                ));
    }

    private boolean hasIntegerSubject(Jwt jwt) {
        try {
            Integer.valueOf(jwt.getSubject());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
