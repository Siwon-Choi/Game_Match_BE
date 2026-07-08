package com.hd.gamematch.game.adapter.in.web;

import com.hd.gamematch.game.application.port.in.FindGamesQuery;
import com.hd.gamematch.game.application.port.in.FindGamesUseCase;
import com.hd.gamematch.game.domain.Game;
import com.hd.gamematch.global.response.CommonResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameControllerTest {

    @Test
    void findGamesReturnsCommonResponseWithGameResponses() {
        RecordingFindGamesUseCase findGamesUseCase = new RecordingFindGamesUseCase();
        GameController controller = new GameController(findGamesUseCase);

        CommonResponse<List<GameResponse>> response = controller.findGames(" League ", " MOBA ");

        assertTrue(response.success());
        assertEquals("SUCCESS", response.code());
        assertEquals("요청에 성공했습니다.", response.message());
        assertEquals(1, response.data().size());
        assertEquals(1L, response.data().get(0).id());
        assertEquals("League of Legends", response.data().get(0).name());
        assertEquals("MOBA", response.data().get(0).sort());
        assertEquals("https://example.com/lol", response.data().get(0).url());

        assertEquals("League", findGamesUseCase.query.name());
        assertEquals("MOBA", findGamesUseCase.query.sort());
    }

    private static class RecordingFindGamesUseCase implements FindGamesUseCase {

        private FindGamesQuery query;

        @Override
        public List<Game> findGames(FindGamesQuery query) {
            this.query = query;
            return List.of(Game.of(1L, "League of Legends", "MOBA", "https://example.com/lol"));
        }
    }
}
