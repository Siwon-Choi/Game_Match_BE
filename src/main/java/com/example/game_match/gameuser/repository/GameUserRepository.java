package com.example.game_match.gameuser.repository;

import com.example.game_match.gameuser.domain.GameUser;
import com.example.game_match.gameuser.domain.vo.GameUserNicknameVo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GameUserRepository {
    Optional<GameUser> findById(Integer id);

    Page<GameUser> findAllByNicknameContaining(GameUserNicknameVo nickname, Pageable pageable);

    Optional<GameUser> findByGameIdAndUserId(Integer gameId, Integer userId);

    Optional<GameUser> findByGameIdAndNickname(Integer gameId, GameUserNicknameVo nickname);

    List<GameUser> findByGroupId(Integer groupId);

    List<GameUser> findByUserId(Integer userId);

    GameUser save(GameUser gameUser);
}
