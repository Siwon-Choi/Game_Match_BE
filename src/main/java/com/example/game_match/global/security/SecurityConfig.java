package com.example.game_match.global.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            // Spring Security 설정 Bean을 만들기 위한 메서드 선언부

            // SecurityFilterChain을 만들기 위한 설정 도구
            HttpSecurity httpSecurity,
            // JwtDecoder 타입 중에서 이름이 accessTokenDecoder인 Bean을 넣는다
            @Qualifier("accessTokenDecoder") JwtDecoder accessTokenDecoder,
            // 인증 실패나 인가 실패가 났을 때 공통 JSON 응답을 내려주기 위함
            SecurityExceptionHandler securityExceptionHandler
    ) throws Exception {
        return httpSecurity
                // CORS 설정을 활성화한다.
                .cors(Customizer.withDefaults())
                // JWT 기반 stateless API에서는 서버 세션/쿠키 기반 CSRF 토큰을 사용하지 않으므로 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // 폼 로그인 화면과 세션 기반 로그인 처리를 사용하지 않는다.
                .formLogin(AbstractHttpConfigurer::disable)
                // 브라우저 기본 인증 팝업을 띄우는 HTTP Basic 인증을 사용하지 않는다.
                .httpBasic(AbstractHttpConfigurer::disable)
                // Spring Security 기본 /logout 필터를 끄고, REST Controller의 /logout API가 직접 처리하게 한다.
                .logout(AbstractHttpConfigurer::disable)
                // 서버 세션을 생성하지 않고, 매 요청의 Authorization Bearer access token으로 인증한다.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // // 요청별 인증/인가 규칙을 정의한다.
                .authorizeHttpRequests(authorize -> authorize
                        // CORS preflight 요청은 인증 없이 통과
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 로그인, 회원가입, 토큰 재발급, 중복 확인 등 인증 전에도 필요한 API는 공개한다.
                        .requestMatchers(
                                "/login",
                                "/register",
                                "/token/refresh",
                                "/logout",
                                "/check/id",
                                "/check/email",
                                "/phone-verifications",
                                "/phone-verifications/confirm",
                                "/images/**"
                        ).permitAll()
                        // // 조회성 GET API는 비로그인 사용자도 접근할 수 있게 공개한다.
                        .requestMatchers(HttpMethod.GET,
                                "/games",
                                "/games/**",
                                "/posts/**",
                                "/comments/**",
                                "/friendly-matches/**",
                                "/game-groups",
                                "/game-groups/**",
                                "/game-users/**",
                                "/match-requests/**"
                        ).permitAll()
                        // 게시글 조회수 증가는 로그인 없이도 허용한다.
                        .requestMatchers(HttpMethod.POST, "/posts/*/views").permitAll()
                        // 위에서 허용하지 않은 모든 요청은 인증된 사용자만 접근할 수 있다.
                        .anyRequest().authenticated()
                )
                // Spring Security 필터 체인 안에서 인증/인가 문제가 생겼을 때 어떻게 응답할지 설정
                .exceptionHandling(exception -> exception
                        // 인증 실패를 처리하는 객체를 지정
                        .authenticationEntryPoint(securityExceptionHandler)
                        // 인가 실패를 처리하는 객체를 지정
                        .accessDeniedHandler(securityExceptionHandler))
                //Authorization: Bearer {JWT} 형식의 access token을 받아서 인증하는 서버로 설정
                // 즉, Spring Security가 Bearer token을 꺼내서 JWT 인증을 시도
                .oauth2ResourceServer(oauth2 -> oauth2
                        // 인증 실패를 처리하는 객체를 지정
                        .authenticationEntryPoint(securityExceptionHandler)
                        // 인가 실패를 처리하는 객체를 지정
                        .accessDeniedHandler(securityExceptionHandler)
                        .jwt(jwt -> jwt
                                // jwt를 검증할 decoder로 accessTokenDecoder를 사용해
                                .decoder(accessTokenDecoder)
                                // jwt를 Spring Security가 사용할 Authentication 객체로 바꾸는 설정
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    // jwt를 Spring Security가 사용할 Authentication 객체로 바꾸는 설정
    // jwt를 받아서 AbstractAuthenticationToken으로 바꾸는 Converter를 만든다
    // Authentication = Spring Security에서 인증된 사용자 정보를 표현하는 인터페이스
    // 실제 구현체는 UsernamePasswordAuthenticationToken = principal, credentials, authorities를 담는 인증 객체
    private Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return jwt -> {
            Integer userId = Integer.valueOf(jwt.getSubject());

            return new UsernamePasswordAuthenticationToken(
                    userId,
                    jwt,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
        };
    }
}
