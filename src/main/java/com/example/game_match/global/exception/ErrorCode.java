package com.example.game_match.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_403", "접근 권한이 없습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_001", "아이디 또는 비밀번호가 올바르지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_002", "refresh token이 유효하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.BAD_REQUEST, "USER_002", "이미 사용 중인 로그인 ID입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "USER_003", "이미 사용 중인 이메일입니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "USER_004", "이미 사용 중인 전화번호입니다."),
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "GAME_001", "게임을 찾을 수 없습니다."),
    GAME_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "GAME_GROUP_001", "게임 그룹을 찾을 수 없습니다."),
    GAME_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "GAME_USER_001", "게임 프로필을 찾을 수 없습니다."),
    DUPLICATE_GAME_USER_NICKNAME(HttpStatus.BAD_REQUEST, "GAME_USER_002", "이미 사용 중인 게임 닉네임입니다."),
    DUPLICATE_GAME_USER_PROFILE(HttpStatus.CONFLICT, "GAME_USER_003", "이미 해당 게임의 프로필이 존재합니다."),
    FRIENDLY_MATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "FRIENDLY_MATCH_001", "친선 경기를 찾을 수 없습니다."),
    DUPLICATE_MATCH_PARTICIPATION(HttpStatus.CONFLICT, "MATCH_PARTICIPATION_001", "이미 roster에 등록된 유저입니다."),
    MATCH_SCHEDULE_CONFLICT(HttpStatus.CONFLICT, "MATCH_PARTICIPATION_002", "해당 시간대에 이미 친선 경기 일정이 있는 유저입니다."),
    MATCH_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "MATCH_REQUEST_001", "친선 경기 신청을 찾을 수 없습니다."),
    DUPLICATE_MATCH_REQUEST(HttpStatus.CONFLICT, "MATCH_REQUEST_002", "이미 해당 경기에 신청한 팀입니다."),
    MATCH_REQUEST_CLOSED(HttpStatus.CONFLICT, "MATCH_REQUEST_003", "모집이 마감된 경기에는 신청할 수 없습니다."),
    MATCH_REQUEST_SCHEDULE_CONFLICT(HttpStatus.CONFLICT, "MATCH_REQUEST_004", "해당 시간대에 이미 신청한 친선 경기가 있습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_001", "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_001", "댓글을 찾을 수 없습니다."),
    POST_VOTE_CONFLICT(HttpStatus.CONFLICT, "POST_VOTE_001", "기존 추천 또는 비추천을 먼저 취소해야 합니다."),
    SMS_NOT_CONFIGURED(HttpStatus.SERVICE_UNAVAILABLE, "SMS_001", "SMS 발송 설정이 필요합니다."),
    SMS_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "SMS_002", "인증번호가 틀렸습니다."),
    SMS_VERIFICATION_EXPIRED(HttpStatus.BAD_REQUEST, "SMS_003", "인증번호가 만료되었습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
