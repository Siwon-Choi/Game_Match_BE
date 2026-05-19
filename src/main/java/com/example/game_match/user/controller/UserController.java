package com.example.game_match.user.controller;

import com.example.game_match.auth.service.RefreshTokenService;
import com.example.game_match.auth.service.JwtTokenProvider;
import com.example.game_match.global.exception.ErrorCode;
import com.example.game_match.global.response.CommonResponse;
import com.example.game_match.user.domain.User;
import com.example.game_match.user.dto.LoginResult;
import com.example.game_match.user.dto.TokenRefreshRequest;
import com.example.game_match.user.dto.TokenRefreshResponse;
import com.example.game_match.user.dto.UserLoginRequestDto;
import com.example.game_match.user.dto.UserRegisterRequestDto;
import com.example.game_match.user.dto.UserResponseDto;
import com.example.game_match.user.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// @Controller + @ResponseBody => 메서드 반환값을 HTTP BODY로 내려보내는 Spring MVC Controller Bean이다.
@RestController
// Lombok 어노테이션 => Lombok이 컴파일 시점에 생성자를 자동으로 만들어주는 어노테이션이다.
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;


    // 로그인 메서드
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResult>> login(@RequestBody UserLoginRequestDto requestDto) {
        // @RequestBody -> http 요청 body를 Java 객체로 변환하여 파라미터에 넣음


        // Service에서 검증 후 유저 반환
        Optional<User> user = userService.authenticate(requestDto.toServiceDto());

        // 로그인 실패
        if (user.isEmpty()) {
            return ResponseEntity
                    .status(ErrorCode.LOGIN_FAILED.getStatus())
                    .body(CommonResponse.error(ErrorCode.LOGIN_FAILED));
        }

        // 로그인 성공
        // CommonResponse는 ResponseEntity의 body에 담기며, CommonResponse는 success, code, message, data로 이루어진 객체이다. -> 추후 Jackson의 ObjectMapper을 통해 json으로 변환된다.
        // issueLoginResult는 user 정보를 가지고 로그인 성공 시 반환 객체(LoginResult)를 생성해준다.
        return ResponseEntity.ok(CommonResponse.success(issueLoginResult(user.get())));
    }


    // 유저 조회
    @GetMapping("/user")
    public ResponseEntity<CommonResponse<UserResponseDto>> getUser(@AuthenticationPrincipal Integer userId) {

        // null일 경우
        if (userId == null) {
            return ResponseEntity
                    .status(ErrorCode.UNAUTHORIZED.getStatus())
                    .body(CommonResponse.error(ErrorCode.UNAUTHORIZED));
        }

        // userId로 찾는다
        return userService.findUserById(userId)
                // user 엔티티를 userResponseDto로 변환
                .map(UserResponseDto::from)
                .map(response -> ResponseEntity.ok(CommonResponse.success(response)))
                .orElseGet(() -> ResponseEntity
                        .status(ErrorCode.USER_NOT_FOUND.getStatus())
                        .body(CommonResponse.error(ErrorCode.USER_NOT_FOUND)));
    }


    // 로그아
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(@RequestBody(required = false) TokenRefreshRequest request) {

        // refresh token이 있으면 서버 저장 토큰 삭제까지 시도
        if (request != null) {
            // refresh token을 검증하고,
            // 그 안에서 userId를 꺼내 Optional<Integer>로 반환하는 메서드
            jwtTokenProvider.resolveUserIdFromRefreshToken(request.getRefreshToken())
                    // 해당하는 userId로 refreshtoken 삭제
                    .ifPresent(userId -> refreshTokenService.deleteIfMatches(userId, request.getRefreshToken()));
        }

        return ResponseEntity.ok(CommonResponse.successWithMessage("Logout Successful"));
    }


    // refresh token을 검증해서 최종적으로 새 access token + 새 refresh token 발급
    @PostMapping("/token/refresh")
    public ResponseEntity<CommonResponse<TokenRefreshResponse>> refreshTokens(
            @RequestBody TokenRefreshRequest request
    ) {
        // refresh token의 서명/만료/type을 검증하고, subject에서 userId를 추출한다.
        Optional<Integer> userId = jwtTokenProvider.resolveUserIdFromRefreshToken(request.getRefreshToken());

        // refresh token이 없거나, 만료/위조/형식 오류/type 불일치/userId 추출 실패면 401 응답을 반환한다.
        if (userId.isEmpty()) {
            return ResponseEntity
                    .status(ErrorCode.INVALID_REFRESH_TOKEN.getStatus())
                    .body(CommonResponse.error(ErrorCode.INVALID_REFRESH_TOKEN));
        }

        // refresh token에서 꺼낸 userId로 실제 사용자가 존재하는지 확인한다.
        return userService.findUserById(userId.get())
                // 사용자가 존재하면 현재 refresh token을 DB 저장값과 비교한 뒤, 새 토큰 쌍을 발급하고 refresh token을 교체한다.
                .map(user -> issueRotatedTokens(user, request.getRefreshToken()))
                // 토큰은 유효해도 해당 userId의 사용자가 없으면 유효하지 않은 refresh token으로 처리한다.
                .orElseGet(() -> ResponseEntity
                        .status(ErrorCode.INVALID_REFRESH_TOKEN.getStatus())
                        .body(CommonResponse.error(ErrorCode.INVALID_REFRESH_TOKEN)));
    }

    // id 검사
    @GetMapping("/check/id")
    public ResponseEntity<CommonResponse<Boolean>> checkLoginId(@RequestParam String loginId) {
        return ResponseEntity.ok(CommonResponse.success(userService.isLoginIdTaken(loginId)));
    }

    // email 검사
    @GetMapping("/check/email")
    public ResponseEntity<CommonResponse<Boolean>> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(CommonResponse.success(userService.isEmailTaken(email)));
    }

    // 회원 가입
    @PostMapping("/register")
    public ResponseEntity<CommonResponse<LoginResult>> register(@RequestBody UserRegisterRequestDto requestDto) {
        User savedUser = userService.register(requestDto.toServiceDto());
        return ResponseEntity.ok(CommonResponse.success(issueLoginResult(savedUser)));
    }


    // 로그인 성공 시 결과 반환
    private LoginResult issueLoginResult(User user) {
        // accessToken과 refreshToken을 jwtTokenProvider를 통해서 발급 후 LoginResult에 담아서 넣는다.
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);
        // refreshToken 재발급
        refreshTokenService.saveOrReplace(user, refreshToken);

        return LoginResult.of(
                user.getName(),
                user.getId(),
                accessToken,
                refreshToken
        );
    }


    // 토큰 교체 메서드
    private ResponseEntity<CommonResponse<TokenRefreshResponse>> issueRotatedTokens(
            User user,
            String currentRefreshToken
    ) {
        // 새로운 토큰 발급
        String nextAccessToken = jwtTokenProvider.createAccessToken(user);
        String nextRefreshToken = jwtTokenProvider.createRefreshToken(user);

        // 토큰 교체
        if (!refreshTokenService.rotate(user, currentRefreshToken, nextRefreshToken)) {
            return ResponseEntity
                    .status(ErrorCode.INVALID_REFRESH_TOKEN.getStatus())
                    .body(CommonResponse.error(ErrorCode.INVALID_REFRESH_TOKEN));
        }

        return ResponseEntity.ok(CommonResponse.success(new TokenRefreshResponse(nextAccessToken, nextRefreshToken)));
    }
}
