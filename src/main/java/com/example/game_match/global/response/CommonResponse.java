package com.example.game_match.global.response;

import com.example.game_match.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class CommonResponse<T> {
    private static final String SUCCESS_CODE = "SUCCESS";
    private static final String SUCCESS_MESSAGE = "요청에 성공했습니다.";

    private final boolean success;
    private final String code;
    private final String message;
    private final T data;

    private CommonResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(true, SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    public static <T> CommonResponse<T> success(String message, T data) {
        return new CommonResponse<>(true, SUCCESS_CODE, message, data);
    }

    public static CommonResponse<Void> success() {
        return new CommonResponse<>(true, SUCCESS_CODE, SUCCESS_MESSAGE, null);
    }

    public static CommonResponse<Void> successWithMessage(String message) {
        return new CommonResponse<>(true, SUCCESS_CODE, message, null);
    }

    public static <T> CommonResponse<T> error(ErrorCode errorCode) {
        return new CommonResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> CommonResponse<T> error(ErrorCode errorCode, String message) {
        return new CommonResponse<>(false, errorCode.getCode(), message, null);
    }
}
