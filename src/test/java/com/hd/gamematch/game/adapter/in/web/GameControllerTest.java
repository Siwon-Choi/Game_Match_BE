package com.hd.gamematch.game.adapter.in.web;

import com.hd.gamematch.game.application.port.in.FindGameQuery;
import com.hd.gamematch.game.application.port.in.FindGameUseCase;
import com.hd.gamematch.game.application.port.in.FindGamesQuery;
import com.hd.gamematch.game.application.port.in.FindGamesUseCase;
import com.hd.gamematch.game.domain.Game;
import com.hd.gamematch.global.response.CommonResponse;
import jdk.jfr.Recording;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameControllerTest {

    @Test

    //findGames 호출 시
    //GameResponse 목록을 포함한
    //CommonResponse가 반환된다.
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


    @Test
    void 게임_id로_게임을_단건_조회하면_CommonResponse로_반환된다(){

        //given
        RecordingFindGamesUseCase findGamesUseCase = new RecordingFindGamesUseCase();

        RecordingFindGameUseCase findGameUseCase = new RecordingFindGameUseCase(
                Game.of(1L, "League of Legends", "MOBA", "https://example.com/lol")
        );

        GameController controller = new GameController(
                findGamesUseCase,
                findGameUseCase
        );

        // when
        CommonResponse<GameResponse> response = controller.findGame(1L);


        // then
        assertTrue(response.success());
        assertEquals("SUCCESS", response.code());
        assertEquals("요청에 성공했습니다.", response.message());

        assertEquals(1L, response.data().id());
        assertEquals("League of Legends", response.data().name());
        assertEquals("MOBA", response.data().sort());
        assertEquals("https://example.com/lol", response.data().url());

        assertEquals(1L, findGameUseCase.gameId);

    }

    private static class RecordingFindGamesUseCase implements FindGamesUseCase {

        private FindGamesQuery query;

        @Override
        public List<Game> findGames(FindGamesQuery query) {
            this.query = query;
            return List.of(Game.of(1L, "League of Legends", "MOBA", "https://example.com/lol"));
        }
    }

    private static class RecordingFindGameUseCase implements FindGameUseCase {

        private final Game result;
        private Long gameId;

        private RecordingFindGameUseCase(Game result){
            this.result = result;
        }

        @Override
        public Game findGame(FindGameQuery query){
            this.gameId = query.gameId();
            return result;
        }
    }
}
