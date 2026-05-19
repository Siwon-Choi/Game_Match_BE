package com.example.game_match.user.repository;

import com.example.game_match.user.domain.User;
import com.example.game_match.user.domain.vo.EmailVo;
import com.example.game_match.user.domain.vo.LoginIdVo;
import com.example.game_match.user.domain.vo.PhoneNumberVo;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Integer id);

    Optional<User> findByLoginId(LoginIdVo loginId);

    boolean existsByLoginId(LoginIdVo loginId);

    boolean existsByEmail(EmailVo email);

    boolean existsByPhoneNumber(PhoneNumberVo phoneNumber);

    User save(User user);
}
