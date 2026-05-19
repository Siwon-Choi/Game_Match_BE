package com.example.game_match.gamegroup.service;

import com.example.game_match.game.domain.Game;
import com.example.game_match.game.repository.GameRepository;
import com.example.game_match.gamegroup.domain.GameGroup;
import com.example.game_match.gamegroup.domain.vo.GameGroupNameVo;
import com.example.game_match.gamegroup.dto.GameGroupCreateDto;
import com.example.game_match.gamegroup.dto.GameGroupResponseDto;
import com.example.game_match.gamegroup.repository.GameGroupRepository;
import com.example.game_match.global.exception.BusinessException;
import com.example.game_match.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameGroupService {
    private final GameGroupRepository gameGroupRepository;
    private final GameRepository gameRepository;

    // 게임 그룹 목록을 조회한다.
    // gameId가 없으면 전체 게임 그룹을 조회하고,
    // gameId가 있으면 해당 게임에 속한 게임 그룹만 조회한다.
    @Transactional(readOnly = true)
    public List<GameGroupResponseDto> findGameGroups(Integer gameId) {
        List<GameGroup> gameGroups = gameId == null
                ? gameGroupRepository.findAll()
                : gameGroupRepository.findByGameId(gameId);

        return gameGroups.stream()
                .map(GameGroupResponseDto::from)
                .toList();
    }

    // groupId로 게임 그룹 단건을 조회한다.
    // 존재하지 않는 groupId면 GAME_GROUP_NOT_FOUND 예외를 발생시킨다.
    @Transactional(readOnly = true)
    public GameGroupResponseDto findGameGroupById(Integer groupId) {
        return gameGroupRepository.findById(groupId)
                .map(GameGroupResponseDto::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_GROUP_NOT_FOUND));
    }

    // 요청 DTO의 gameId로 게임을 조회한 뒤, 해당 게임에 속한 새 게임 그룹을 생성한다.
    // 존재하지 않는 gameId면 GAME_NOT_FOUND 예외를 발생시킨다.
    @Transactional
    public GameGroupResponseDto createGameGroup(GameGroupCreateDto dto) {
        Game game = gameRepository.findById(dto.gameId())
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND));

        GameGroup gameGroup = GameGroup.create(game, GameGroupNameVo.from(dto.name()));

        return GameGroupResponseDto.from(gameGroupRepository.save(gameGroup));
    }
}
