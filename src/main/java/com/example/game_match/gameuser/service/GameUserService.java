package com.example.game_match.gameuser.service;

import com.example.game_match.game.domain.Game;
import com.example.game_match.game.repository.GameRepository;
import com.example.game_match.gamegroup.domain.GameGroup;
import com.example.game_match.gamegroup.repository.GameGroupRepository;
import com.example.game_match.gameuser.domain.GameUser;
import com.example.game_match.gameuser.domain.vo.GameUserNicknameVo;
import com.example.game_match.gameuser.dto.GameUserCreateDto;
import com.example.game_match.gameuser.dto.GameUserPageResponseDto;
import com.example.game_match.gameuser.dto.GameUserResponseDto;
import com.example.game_match.gameuser.dto.GameUserUpdateDto;
import com.example.game_match.gameuser.repository.GameUserRepository;
import com.example.game_match.global.exception.BusinessException;
import com.example.game_match.global.exception.ErrorCode;
import com.example.game_match.user.domain.User;
import com.example.game_match.user.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class GameUserService {
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;

    private final GameUserRepository gameUserRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final GameGroupRepository gameGroupRepository;

    // gameUserId로 GameUser 엔티티를 조회한다.
    // 없으면 게임 프로필을 찾을 수 없다는 비즈니스 예외를 발생시킨다.
    @Transactional(readOnly = true)
    public GameUser findGameUserById(Integer gameUserId) {
        return gameUserRepository.findById(gameUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_USER_NOT_FOUND));
    }

    // 닉네임으로 게임 유저 프로필을 검색한다.
    // page/size 값을 보정한 뒤 페이징 조회하고, 응답 DTO 목록과 전체 개수를 함께 반환한다.
    @Transactional(readOnly = true)
    public GameUserPageResponseDto getAllNicknamesWithTotalCount(String nickname, int page, int size) {
        Pageable pageable = PageRequest.of(normalizePage(page) - 1, normalizeSize(size));

        Page<GameUser> gameUsersPage = gameUserRepository.findAllByNicknameContaining(
                GameUserNicknameVo.from(nickname),
                pageable
        );

        List<GameUserResponseDto> gameUsers = gameUsersPage.stream()
                .map(GameUserResponseDto::from)
                .toList();

        return new GameUserPageResponseDto(gameUsersPage.getTotalElements(), gameUsers);
    }

    // gameUserId로 게임 유저 프로필을 조회한 뒤 응답 DTO로 변환한다.
    @Transactional(readOnly = true)
    public GameUserResponseDto getGameUserById(Integer id) {
        return GameUserResponseDto.from(findGameUserById(id));
    }

    // 전달받은 게임 유저 목록 중 특정 gameId에 해당하는 게임 유저만 다시 조회한다.
    @Transactional(readOnly = true)
    public List<GameUser> getGameUsers(int gameId, List<GameUser> gameUsers) {
        return gameUsers.stream()
                .map(gameUser -> gameUserRepository.findByGameIdAndUserId(gameId, gameUser.getUserId()))
                .flatMap(Optional::stream)
                .toList();
    }

    // userId와 gameId 조합으로 특정 사용자의 특정 게임 프로필을 조회한다.
    @Transactional(readOnly = true)
    public GameUserResponseDto getGameUserByUserIdAndGameId(Integer userId, Integer gameId) {
        GameUser gameUser = gameUserRepository.findByGameIdAndUserId(gameId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_USER_NOT_FOUND));

        return GameUserResponseDto.from(gameUser);
    }

    // groupId로 해당 게임 그룹에 속한 게임 유저 프로필 목록을 조회한다.
    @Transactional(readOnly = true)
    public List<GameUserResponseDto> getGameUsersByGroupId(Integer groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException("Group_Id cannot be null.");
        }

        return gameUserRepository.findByGroupId(groupId).stream()
                .map(GameUserResponseDto::from)
                .toList();
    }

    // userId로 해당 사용자가 가진 게임 유저 프로필 목록을 조회한다.
    @Transactional(readOnly = true)
    public List<GameUser> getGameUsersByUserId(Integer userId) {
        return gameUserRepository.findByUserId(userId);
    }

    // 로그인한 사용자의 게임 유저 프로필을 새로 생성한다.
    // 같은 게임에 이미 프로필이 있으면 중복 프로필 예외를 발생시킨다.
    @Transactional
    public GameUserResponseDto createGameUser(Integer userId, GameUserCreateDto dto) {
        if (dto.gameId() == null) {
            throw new IllegalArgumentException("게임 정보가 필요합니다.");
        }

        GameUserNicknameVo nickname = GameUserNicknameVo.from(dto.nickname());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Game game = gameRepository.findById(dto.gameId())
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_NOT_FOUND));

        if (gameUserRepository.findByGameIdAndUserId(dto.gameId(), userId).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_GAME_USER_PROFILE);
        }

        validateNicknameAvailable(game.getId(), nickname, null);
        GameGroup group = resolveGroup(dto.groupId(), game.getId());

        GameUser gameUser = GameUser.create(user, game, group, nickname);
        return GameUserResponseDto.from(gameUserRepository.save(gameUser));
    }

    // 로그인한 사용자의 기존 게임 유저 프로필을 수정한다.
    // 본인 소유 프로필이 아니면 접근 권한 없음 예외를 발생시킨다.
    @Transactional
    public GameUserResponseDto updateGameUser(Integer userId, Integer gameUserId, GameUserUpdateDto dto) {
        GameUser gameUser = findGameUserById(gameUserId);

        if (!Objects.equals(gameUser.getUserId(), userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        GameUserNicknameVo nickname = GameUserNicknameVo.from(dto.nickname());
        validateNicknameAvailable(gameUser.getGameId(), nickname, gameUser.getId());

        GameGroup group = resolveGroup(dto.groupId(), gameUser.getGameId());
        gameUser.updateNickname(nickname);
        gameUser.updateGroup(group);

        return GameUserResponseDto.from(gameUserRepository.save(gameUser));
    }

    // 같은 게임 안에서 동일 닉네임이 이미 사용 중인지 검사한다.
    // 수정 요청에서는 현재 수정 중인 자기 자신의 id는 중복 대상에서 제외한다.
    private void validateNicknameAvailable(Integer gameId, GameUserNicknameVo nickname, Integer currentGameUserId) {
        GameUser duplicate = gameUserRepository.findByGameIdAndNickname(gameId, nickname).orElse(null);

        if (duplicate != null && !Objects.equals(duplicate.getId(), currentGameUserId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_GAME_USER_NICKNAME);
        }
    }

    // groupId가 있으면 GameGroup을 조회하고, 현재 게임과 같은 게임의 그룹인지 검증한다.
    // groupId가 null이면 클럽에 속하지 않은 게임 유저 프로필로 처리한다.
    private GameGroup resolveGroup(Integer groupId, Integer gameId) {
        if (groupId == null) {
            return null;
        }

        GameGroup group = gameGroupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GAME_GROUP_NOT_FOUND));

        if (!Objects.equals(group.getGame().getId(), gameId)) {
            throw new IllegalArgumentException("선택한 클럽이 현재 게임과 일치하지 않습니다.");
        }

        return group;
    }

    // 프론트에서 0 이하 page가 들어와도 최소 1페이지로 보정한다.
    private int normalizePage(int page) {
        return Math.max(page, DEFAULT_PAGE);
    }

    // 프론트에서 1보다 작은 size가 들어와도 기본 size로 보정한다.
    private int normalizeSize(int size) {
        return size < 1 ? DEFAULT_SIZE : size;
    }
}
