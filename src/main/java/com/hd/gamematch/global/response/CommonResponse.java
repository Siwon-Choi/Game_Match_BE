package com.hd.gamematch.global.response;

public record CommonResponse<T>(
        boolean success,
        String code,
        String message,
        T data
){
    public static <T> CommonResponse<T> success(T data){
        return new CommonResponse<>(
                true,
                "SUCCESS",
                "요청에 성공했습니다.",
                data
        );
    }
}
