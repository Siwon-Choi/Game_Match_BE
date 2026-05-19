package com.example.game_match.sms.repository;

import com.example.game_match.sms.domain.VerificationCode;
import com.example.game_match.sms.domain.vo.SmsVerificationCodeVo;
import com.example.game_match.sms.domain.vo.VerificationSessionIdVo;
import java.util.Optional;

public interface VerificationCodeRepository {
    VerificationCode save(VerificationCode verificationCode);

    Optional<VerificationCode> findByCode(SmsVerificationCodeVo code);

    void remove(VerificationCode verificationCode);

    void saveSessionCode(VerificationSessionIdVo sessionId, SmsVerificationCodeVo code);

    Optional<SmsVerificationCodeVo> findCodeBySessionId(VerificationSessionIdVo sessionId);

    void removeSession(VerificationSessionIdVo sessionId);
}
