package com.example.game_match.gameuser.repository.jpa;

import com.example.game_match.gameuser.domain.GameUser;
import com.example.game_match.gameuser.domain.vo.GameUserNicknameVo;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameUserJpaRepository extends JpaRepository<GameUser, Integer> {
    @NotNull
    @Override
    @EntityGraph(attributePaths = {"user", "game", "group"})
    Optional<GameUser> findById(@NotNull Integer id);

    @Query(
            value = """
                    SELECT *
                    FROM game_user
                    WHERE Nickname LIKE CONCAT('%', :nickname, '%')
                    ORDER BY Id ASC
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM game_user
                    WHERE Nickname LIKE CONCAT('%', :nickname, '%')
                    """,
            nativeQuery = true
    )
    Page<GameUser> searchByNicknameContaining(@Param("nickname") String nickname, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "game", "group"})
    Optional<GameUser> findByGame_IdAndUser_Id(Integer gameId, Integer userId);

    Optional<GameUser> findByGame_IdAndNickname(Integer gameId, GameUserNicknameVo nickname);

    @EntityGraph(attributePaths = {"user", "game", "group"})
    List<GameUser> findByGroup_Id(Integer groupId);

    @EntityGraph(attributePaths = {"user", "game", "group"})
    List<GameUser> findByUser_Id(Integer userId);
}
