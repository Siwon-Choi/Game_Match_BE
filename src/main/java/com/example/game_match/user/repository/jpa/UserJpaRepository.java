package com.example.game_match.user.repository.jpa;

import com.example.game_match.user.domain.User;
import com.example.game_match.user.domain.vo.EmailVo;
import com.example.game_match.user.domain.vo.LoginIdVo;
import com.example.game_match.user.domain.vo.PhoneNumberVo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLoginId(LoginIdVo loginId);

    boolean existsByLoginId(LoginIdVo loginId);

    boolean existsByEmail(EmailVo email);

    boolean existsByPhoneNumber(PhoneNumberVo phoneNumber);
}
