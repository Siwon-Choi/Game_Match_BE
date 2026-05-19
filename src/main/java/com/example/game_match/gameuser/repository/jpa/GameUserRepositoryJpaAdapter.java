package com.example.game_match.gameuser.repository.jpa;

import com.example.game_match.gameuser.domain.GameUser;
import com.example.game_match.gameuser.domain.vo.GameUserNicknameVo;
import com.example.game_match.gameuser.repository.GameUserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameUserRepositoryJpaAdapter implements GameUserRepository {
    private final GameUserJpaRepository gameUserJpaRepository;

    @Override
    public Optional<GameUser> findById(Integer id) {
        return gameUserJpaRepository.findById(id);
    }

    @Override
    public Page<GameUser> findAllByNicknameContaining(GameUserNicknameVo nickname, Pageable pageable) {
        return gameUserJpaRepository.searchByNicknameContaining(nickname.getValue(), pageable);
    }

    @Override
    public Optional<GameUser> findByGameIdAndUserId(Integer gameId, Integer userId) {
        return gameUserJpaRepository.findByGame_IdAndUser_Id(gameId, userId);
    }

    @Override
    public Optional<GameUser> findByGameIdAndNickname(Integer gameId, GameUserNicknameVo nickname) {
        return gameUserJpaRepository.findByGame_IdAndNickname(gameId, nickname);
    }

    @Override
    public List<GameUser> findByGroupId(Integer groupId) {
        return gameUserJpaRepository.findByGroup_Id(groupId);
    }

    @Override
    public List<GameUser> findByUserId(Integer userId) {
        return gameUserJpaRepository.findByUser_Id(userId);
    }

    @Override
    public GameUser save(GameUser gameUser) {
        return gameUserJpaRepository.save(gameUser);
    }
}
