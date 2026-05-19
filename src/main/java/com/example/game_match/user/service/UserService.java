package com.example.game_match.user.service;

import com.example.game_match.global.exception.BusinessException;
import com.example.game_match.global.exception.ErrorCode;
import com.example.game_match.user.domain.User;
import com.example.game_match.user.domain.vo.EmailVo;
import com.example.game_match.user.domain.vo.EncodedPasswordVo;
import com.example.game_match.user.domain.vo.LoginIdVo;
import com.example.game_match.user.domain.vo.PhoneNumberVo;
import com.example.game_match.user.domain.vo.RawPasswordVo;
import com.example.game_match.user.domain.vo.UserNameVo;
import com.example.game_match.user.dto.UserLoginDto;
import com.example.game_match.user.dto.UserRegisterDto;
import com.example.game_match.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Optional<User> authenticate(UserLoginDto dto) {
        // 로그인 요청 DTO의 loginId를 도메인 VO로 변환한다.
        LoginIdVo loginId = LoginIdVo.from(dto.loginId());
        // 로그인 요청 DTO의 원문 비밀번호를 도메인 VO로 변환한다.
        RawPasswordVo rawPassword = RawPasswordVo.from(dto.loginPassword());

        // loginId로 사용자를 조회한다.
        return userRepository.findByLoginId(loginId)
                // 사용자가 존재하면, 입력한 원문 비밀번호와 DB에 저장된 암호화 비밀번호가 일치하는지 검증한다.
                .filter(user -> passwordEncoder.matches(rawPassword.getValue(), user.getLoginPassword()));
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserById(Integer userId) {
        if (userId == null) {
            return Optional.empty();
        }

        return userRepository.findById(userId);
    }

    @Transactional(readOnly = true)
    public boolean isLoginIdTaken(String loginId) {
        return userRepository.existsByLoginId(LoginIdVo.from(loginId));
    }

    @Transactional(readOnly = true)
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(EmailVo.from(email));
    }

    @Transactional(readOnly = true)
    public boolean isPhoneNumberTaken(String phoneNumber) {
        return userRepository.existsByPhoneNumber(PhoneNumberVo.from(phoneNumber));
    }

    // 회원가입
    @Transactional
    public User register(UserRegisterDto dto) {
        UserNameVo name = UserNameVo.from(dto.name());
        EmailVo email = EmailVo.from(dto.email());
        PhoneNumberVo phoneNumber = PhoneNumberVo.from(dto.phoneNumber());
        LoginIdVo loginId = LoginIdVo.from(dto.loginId());
        RawPasswordVo rawPassword = RawPasswordVo.from(dto.loginPassword());

        validateDuplicate(loginId, email, phoneNumber);

        String encodedPassword = passwordEncoder.encode(rawPassword.getValue());

        User user = User.create(
                name,
                email,
                phoneNumber,
                dto.birth(),
                loginId,
                EncodedPasswordVo.from(encodedPassword)
        );

        return userRepository.save(user);
    }

    private void validateDuplicate(LoginIdVo loginId, EmailVo email, PhoneNumberVo phoneNumber) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new BusinessException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }
    }
}
