package com.example.game_match.sms.service;

import com.example.game_match.global.exception.BusinessException;
import com.example.game_match.global.exception.ErrorCode;
import com.example.game_match.sms.domain.VerificationCode;
import com.example.game_match.sms.domain.vo.SmsVerificationCodeVo;
import com.example.game_match.sms.domain.vo.VerificationSessionIdVo;
import com.example.game_match.sms.repository.VerificationCodeRepository;
import com.example.game_match.user.domain.vo.PhoneNumberVo;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PhoneVerificationService {
    private final VerificationCodeRepository verificationCodeRepository;

    @Value("${spring.sms.api-key:}")
    private String apiKey;

    @Value("${spring.sms.api-secret:}")
    private String apiSecret;

    @Value("${spring.sms.provider:https://api.coolsms.co.kr}")
    private String smsProvider;

    @Value("${spring.sms.sender:}")
    private String smsSender;

    private DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        if (!isSmsConfigured()) {
            return;
        }

        messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, smsProvider);
    }

    // 전화번호와 세션 ID를 검증한 뒤 인증번호를 발급하고 SMS로 전송한다.
    public void requestVerificationCode(String sessionId, String phoneNumber, LocalDateTime requestedAt) {
        VerificationSessionIdVo sessionIdVo = VerificationSessionIdVo.from(sessionId);
        PhoneNumberVo phoneNumberVo = PhoneNumberVo.from(phoneNumber);

        VerificationCode verificationCode = VerificationCode.create(requestedAt);
        sendVerificationMessage(phoneNumberVo, verificationCode);

        verificationCodeRepository.save(verificationCode);
        verificationCodeRepository.saveSessionCode(sessionIdVo, verificationCode.getCode());
    }

    // 사용자가 입력한 인증번호가 세션에 발급된 번호와 일치하고 만료되지 않았는지 검증한다.
    public void verifySessionCode(String sessionId, String code, LocalDateTime verifiedAt) {
        VerificationSessionIdVo sessionIdVo = VerificationSessionIdVo.from(sessionId);
        SmsVerificationCodeVo codeVo = SmsVerificationCodeVo.from(code);
        SmsVerificationCodeVo expectedCode = verificationCodeRepository.findCodeBySessionId(sessionIdVo)
                .orElseThrow(() -> new BusinessException(ErrorCode.SMS_VERIFICATION_FAILED));

        if (!expectedCode.equals(codeVo)) {
            throw new BusinessException(ErrorCode.SMS_VERIFICATION_FAILED);
        }

        VerificationCode verificationCode = verificationCodeRepository.findByCode(codeVo)
                .orElseThrow(() -> new BusinessException(ErrorCode.SMS_VERIFICATION_FAILED));

        if (verificationCode.isExpired(verifiedAt)) {
            verificationCodeRepository.remove(verificationCode);
            verificationCodeRepository.removeSession(sessionIdVo);
            throw new BusinessException(ErrorCode.SMS_VERIFICATION_EXPIRED);
        }

        verificationCodeRepository.remove(verificationCode);
        verificationCodeRepository.removeSession(sessionIdVo);
    }

    private void sendVerificationMessage(PhoneNumberVo phoneNumber, VerificationCode verificationCode) {
        if (messageService == null) {
            throw new BusinessException(ErrorCode.SMS_NOT_CONFIGURED);
        }

        Message message = new Message();
        message.setFrom(smsSender);
        message.setTo(phoneNumber.getValue());
        message.setText(verificationCode.generateCodeMessage());
        messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    private boolean isSmsConfigured() {
        return StringUtils.hasText(apiKey)
                && StringUtils.hasText(apiSecret)
                && StringUtils.hasText(smsProvider)
                && StringUtils.hasText(smsSender);
    }
}
