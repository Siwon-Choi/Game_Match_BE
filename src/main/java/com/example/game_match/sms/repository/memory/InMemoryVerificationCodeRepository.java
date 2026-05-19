package com.example.game_match.sms.repository.memory;

import com.example.game_match.sms.domain.VerificationCode;
import com.example.game_match.sms.domain.vo.SmsVerificationCodeVo;
import com.example.game_match.sms.domain.vo.VerificationSessionIdVo;
import com.example.game_match.sms.repository.VerificationCodeRepository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryVerificationCodeRepository implements VerificationCodeRepository {
    private final Map<String, VerificationCode> codeStore = new ConcurrentHashMap<>();
    private final Map<String, SmsVerificationCodeVo> sessionStore = new ConcurrentHashMap<>();

    @Override
    public VerificationCode save(VerificationCode verificationCode) {
        codeStore.put(verificationCode.getCodeValue(), verificationCode);
        return verificationCode;
    }

    @Override
    public Optional<VerificationCode> findByCode(SmsVerificationCodeVo code) {
        return Optional.ofNullable(codeStore.get(code.getValue()));
    }

    @Override
    public void remove(VerificationCode verificationCode) {
        codeStore.remove(verificationCode.getCodeValue());
    }

    @Override
    public void saveSessionCode(VerificationSessionIdVo sessionId, SmsVerificationCodeVo code) {
        sessionStore.put(sessionId.getValue(), code);
    }

    @Override
    public Optional<SmsVerificationCodeVo> findCodeBySessionId(VerificationSessionIdVo sessionId) {
        return Optional.ofNullable(sessionStore.get(sessionId.getValue()));
    }

    @Override
    public void removeSession(VerificationSessionIdVo sessionId) {
        sessionStore.remove(sessionId.getValue());
    }
}
