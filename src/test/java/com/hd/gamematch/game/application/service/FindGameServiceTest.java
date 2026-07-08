package com.hd.gamematch.game.application.service;

import com.hd.gamematch.game.application.port.in.FindGameQuery;
import com.hd.gamematch.game.application.port.out.LoadGamePort;
import com.hd.gamematch.game.domain.Game;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FindGameServiceTest {

    @Test
    void 게임_id로_게임을_단건_조회한다() {
        // LoadGamePort가 DB 대신 Game을 하나 반환한다고 가정한다.
        RecordingLoadGamePort loadGamePort = new RecordingLoadGamePort(
                Optional.of(Game.of(1L, "League of Legends", "MOBA", "https://example.com/lol"))
        );
        FindGameService service = new FindGameService(loadGamePort);

        // when
        // 게임 id 1번으로 단건 조회 유스케이스를 실행한다.
        Game result = service.findGame(FindGameQuery.of(1L));

        // then
        // 조회 결과가 예상한 게임 정보와 일치해야 한다.
        assertEquals(1L, result.id());
        assertEquals("League of Legends", result.name());
        assertEquals("MOBA", result.sort());
        assertEquals("https://example.com/lol", result.url());

        // 그리고 서비스는 LoadGamePort에 같은 id로 조회를 위임해야 한다.
        assertEquals(1L, loadGamePort.gameId);
    }


    @Test
    void 게임_id로_조회했는데_게임이_없으면_예외가_발생한다() {
        // given
        // LoadGamePort가 DB 대신 "조회 결과 없음"을 반환한다고 가정한다.
        RecordingLoadGamePort loadGamePort = new RecordingLoadGamePort(Optional.empty());
        FindGameService service = new FindGameService(loadGamePort);

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.findGame(FindGameQuery.of(999L))
        );

        assertEquals("게임을 찾을 수 없습니다.", exception.getMessage());
    }


    private static class RecordingLoadGamePort implements LoadGamePort {

        private final Optional<Game> result;
        private Long gameId;

        private RecordingLoadGamePort(Optional<Game> result) {
            this.result = result;
        }

        @Override
        public Optional<Game> loadGameById(Long gameId) {
            this.gameId = gameId;
            return result;
        }
    }

}