package com.hd.gamematch.game.application.service;

import com.hd.gamematch.game.application.port.in.FindGamesQuery;
import com.hd.gamematch.game.application.port.out.LoadGamesPort;
import com.hd.gamematch.game.domain.Game;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class FindGamesServiceTest {

    @Test
    void loadByNameAndSortWhenBothConditionsExist() {
        RecordingLoadGamesPort loadGamesPort = new RecordingLoadGamesPort();
        FindGamesService service = new FindGamesService(loadGamesPort);

        List<Game> result = service.findGames(FindGamesQuery.of("League", "MOBA"));

        assertSame(loadGamesPort.result, result);
        assertEquals("nameAndSort", loadGamesPort.calledMethod);
        assertEquals("League", loadGamesPort.name);
        assertEquals("MOBA", loadGamesPort.sort);
    }

    @Test
    void loadByNameWhenOnlyNameExists() {
        RecordingLoadGamesPort loadGamesPort = new RecordingLoadGamesPort();
        FindGamesService service = new FindGamesService(loadGamesPort);

        List<Game> result = service.findGames(FindGamesQuery.of("League", null));

        assertSame(loadGamesPort.result, result);
        assertEquals("name", loadGamesPort.calledMethod);
        assertEquals("League", loadGamesPort.name);
    }

    @Test
    void loadBySortWhenOnlySortExists() {
        RecordingLoadGamesPort loadGamesPort = new RecordingLoadGamesPort();
        FindGamesService service = new FindGamesService(loadGamesPort);

        List<Game> result = service.findGames(FindGamesQuery.of(null, "MOBA"));

        assertSame(loadGamesPort.result, result);
        assertEquals("sort", loadGamesPort.calledMethod);
        assertEquals("MOBA", loadGamesPort.sort);
    }

    @Test
    void loadAllWhenNoConditionExists() {
        RecordingLoadGamesPort loadGamesPort = new RecordingLoadGamesPort();
        FindGamesService service = new FindGamesService(loadGamesPort);

        List<Game> result = service.findGames(FindGamesQuery.of(null, " "));

        assertSame(loadGamesPort.result, result);
        assertEquals("all", loadGamesPort.calledMethod);
    }

    private static class RecordingLoadGamesPort implements LoadGamesPort {

        private final List<Game> result = List.of(Game.of(1L, "League of Legends", "MOBA", "https://example.com/lol"));

        private String calledMethod;
        private String name;
        private String sort;

        @Override
        public List<Game> loadAllGames() {
            calledMethod = "all";
            return result;
        }

        @Override
        public List<Game> loadGamesByNameAndSort(String name, String sort) {
            calledMethod = "nameAndSort";
            this.name = name;
            this.sort = sort;
            return result;
        }

        @Override
        public List<Game> loadGamesByName(String name) {
            calledMethod = "name";
            this.name = name;
            return result;
        }

        @Override
        public List<Game> loadGamesBySort(String sort) {
            calledMethod = "sort";
            this.sort = sort;
            return result;
        }
    }
}
