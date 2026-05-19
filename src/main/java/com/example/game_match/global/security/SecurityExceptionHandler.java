package com.example.game_match.global.security;

import com.example.game_match.global.exception.ErrorCode;
import com.example.game_match.global.response.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
// Spring Security에서 발생한 401/403을 우리 공통 JSON 응답으로 바꿔주는 핸들러
// 401: 인증 실패 / 403: 인가 실패
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    // Security 필터 단계에서는 Controller 반환값 처리처럼 ResponseEntity와 HttpMessageConverter 흐름을 타지 않을 수 있기 떄문이다.
    // ObjectMapper는 Java 객체를 JSON 문자열로 바꿔주는 Jackson 객체
    private final ObjectMapper objectMapper;


    @Override
    // AuthenticationEntryPoint 인터페이스의 메서드
    // Spring Security가 인증 실패라고 판단하면 호출
    public void commence(
            HttpServletRequest request,
            // 여기에 401 응답을 직접 써야 한다.
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        writeErrorResponse(response, ErrorCode.UNAUTHORIZED);
    }

    @Override
    // AccessDeniedHandler 인터페이스의 메서드
    // Spring Security가 인가 실패라고 판단하면 호출
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        writeErrorResponse(response, ErrorCode.FORBIDDEN);
    }

    private void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        // ErrorCode에 정의된 HTTP 상태 코드를 응답에 설정한다. 예: 401, 403
        response.setStatus(errorCode.getStatus().value());
        // 한글 에러 메시지가 깨지지 않도록 응답 문자 인코딩을 UTF-8로 설정한다.
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // 응답 body가 JSON 형식임을 클라이언트에게 알린다.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // CommonResponse.error(errorCode) 객체를 JSON으로 변환해서 응답 body에 직접 작성한다.
        objectMapper.writeValue(response.getWriter(), CommonResponse.error(errorCode));
    }
}
