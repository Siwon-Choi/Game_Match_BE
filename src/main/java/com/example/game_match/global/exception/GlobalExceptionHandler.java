package com.example.game_match.global.exception;

import com.example.game_match.global.response.CommonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 여러 Controller에서 발생한 예외를 한 곳에서 처리하게 해주는 어노테이션
// 에를 들어 Controller 메서드 안에서 예외가 터졌을 때, 각 Controller마다 try-catch를 쓰지 않고 전역에서 잡아준다.
// ex) Service에서
// throw new BusinessException(ErrorCode.GAME_NOT_FOUND);가 발생하면
// Spring MVC가 이 클래스를 찾는다.
// DispatcherServlet까지 예외 전달 후 ExceptionHandlerExceptionResolver가 처리 가능한 @ExceptionHandler 검색하는 원리이다.
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 비즈니스 로직에서 의도적으로 발생시킨 예외를 처리한다.
    // ErrorCode에 정의된 상태 코드와 메시지를 CommonResponse 형식으로 반환한다.
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<Void>> handleBusinessException(BusinessException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(CommonResponse.error(errorCode, exception.getMessage()));
    }

    // VO 생성 실패, 잘못된 파라미터 등 IllegalArgumentException을 400 Bad Request로 처리한다.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<Void>> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getStatus())
                .body(CommonResponse.error(ErrorCode.INVALID_REQUEST, exception.getMessage()));
    }

    // @Valid 검증 실패 시 첫 번째 필드 에러 메시지를 400 Bad Request로 반환한다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse(ErrorCode.INVALID_REQUEST.getMessage());

        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getStatus())
                .body(CommonResponse.error(ErrorCode.INVALID_REQUEST, message));
    }

    // 요청 body JSON 형식이 잘못됐거나 DTO로 변환할 수 없을 때 400 Bad Request로 처리한다.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception
    ) {
        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.getStatus())
                .body(CommonResponse.error(ErrorCode.INVALID_REQUEST));
    }

    // 위에서 처리하지 못한 예외를 마지막으로 잡아 500 Internal Server Error로 처리한다.
    // 예상하지 못한 내부 오류가 클라이언트에 그대로 노출되지 않도록 공통 메시지를 반환한다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception exception) {
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(CommonResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}

