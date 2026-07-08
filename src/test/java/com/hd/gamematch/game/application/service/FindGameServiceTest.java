package com.hd.gamematch.game.application.service;

import com.hd.gamematch.game.application.port.in.FindGameQuery;
import com.hd.gamematch.game.application.port.out.LoadGamePort;
import com.hd.gamematch.game.domain.Game;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FindGameServiceTest {

    @Test
    void findGameReturnsGameWhenGameExists() {
        RecordingLoadGamePort loadGamePort = new RecordingLoadGamePort(
                Optional.of(Game.of(1L, "League of Legends", "MOBA", "https://example.com/lol"))
        );
        FindGameService service = new FindGameService(loadGamePort);

        Game result = service.findGame(FindGameQuery.of(1L));

        assertEquals(1L, result.id());
        assertEquals("League of Legends", result.name());
        assertEquals("MOBA", result.sort());
        assertEquals("https://example.com/lol", result.url());

        assertEquals(1L, loadGamePort.gameId);
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