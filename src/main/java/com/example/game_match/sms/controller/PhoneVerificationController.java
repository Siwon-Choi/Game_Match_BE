package com.example.game_match.sms.controller;

import com.example.game_match.global.response.CommonResponse;
import com.example.game_match.sms.dto.PhoneVerificationConfirmRequestDto;
import com.example.game_match.sms.dto.PhoneVerificationRequestDto;
import com.example.game_match.sms.dto.PhoneVerificationResponseDto;
import com.example.game_match.sms.service.PhoneVerificationService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PhoneVerificationController {
    private final PhoneVerificationService phoneVerificationService;

    // 전화번호로 인증번호 전송을 요청한다.
    @PostMapping("/phone-verifications")
    public ResponseEntity<CommonResponse<PhoneVerificationResponseDto>> requestVerificationCode(
            @RequestBody PhoneVerificationRequestDto requestDto
    ) {
        phoneVerificationService.requestVerificationCode(
                requestDto.sessionId(),
                requestDto.phoneNumber(),
                LocalDateTime.now()
        );

        return ResponseEntity.ok(CommonResponse.success(
                PhoneVerificationResponseDto.success("인증요청이 전송되었습니다.")
        ));
    }

    // 사용자가 입력한 인증번호가 세션에 저장된 인증번호와 일치하는지 검증한다.
    @PostMapping("/phone-verifications/confirm")
    public ResponseEntity<CommonResponse<PhoneVerificationResponseDto>> confirmVerificationCode(
            @RequestBody PhoneVerificationConfirmRequestDto requestDto
    ) {
        phoneVerificationService.verifySessionCode(
                requestDto.sessionId(),
                requestDto.code(),
                LocalDateTime.now()
        );

        return ResponseEntity.ok(CommonResponse.success(
                PhoneVerificationResponseDto.success("정상 인증 되었습니다.")
        ));
    }
}
