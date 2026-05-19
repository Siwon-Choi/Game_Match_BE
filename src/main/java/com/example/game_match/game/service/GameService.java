package com.example.game_match.game.service;

import com.example.game_match.game.domain.Game;
import com.example.game_match.game.domain.vo.GameNameVo;
import com.example.game_match.game.domain.vo.GameSortVo;
import com.example.game_match.game.dto.GameResponseDto;
import com.example.game_match.game.repository.GameRepository;
import com.example.game_match.global.exception.BusinessException;
import com.example.game_match.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;

    // 게임 목록을 조회한다.
    // name이 있으면 게임 이름으로 조회하고, sort가 있으면 게임 분류로 조회한다.
    // name과 sort가 모두 없으면 전체 게임 목록을 조회한다.
    @Transactional(readOnly = true)
    public List<GameResponseDto> findGames(String sort, String name) {
        if (name != null && !name.isBlank()) {
            return gameRepository.findByName(GameNameVo.from(name)).stream()
                    .map(GameResponseDto::from)
                    .toList();
        }

        if (sort != null && !sort.isBlank()) {
            return gameRepository.findBySort(GameSortVo.from(sort)).stream()
                    .map(GameResponseDto::from)
                    .toList();
        }

        return gameRepository.findAll().stream()
                .map(GameResponseDto::from)
                .toList();
    }

    // gameId로 게임을 조회한다.
    // 존재하지 않는 gameId면 GAME_NOT_FOUND 예외를 발생시킨다.
    @Transactional(readOnly = true)
    public GameResponseDto findGameById(Integer gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND));

        return GameResponseDto.from(game);
    }
}
