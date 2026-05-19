package com.example.game_match.user.repository.jpa;

import com.example.game_match.user.domain.User;
import com.example.game_match.user.domain.vo.EmailVo;
import com.example.game_match.user.domain.vo.LoginIdVo;
import com.example.game_match.user.domain.vo.PhoneNumberVo;
import com.example.game_match.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryJpaAdapter implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(Integer id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByLoginId(LoginIdVo loginId) {
        return userJpaRepository.findByLoginId(loginId);
    }

    @Override
    public boolean existsByLoginId(LoginIdVo loginId) {
        return userJpaRepository.existsByLoginId(loginId);
    }

    @Override
    public boolean existsByEmail(EmailVo email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(PhoneNumberVo phoneNumber) {
        return userJpaRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }
}
