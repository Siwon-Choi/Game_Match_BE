package com.hd.gamematch.game.application.port.in;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FindGamesQueryTest {

    @Test
    void nameAndSortAreTrimmed() {
        FindGamesQuery query = FindGamesQuery.of(" League of Legends ", " MOBA ");

        assertTrue(query.hasName());
        assertTrue(query.hasSort());
        assertEquals("League of Legends", query.name());
        assertEquals("MOBA", query.sort());
    }

    @Test
    void blankValuesAreNormalizedToNull() {
        FindGamesQuery query = FindGamesQuery.of(" ", "\t");

        assertFalse(query.hasName());
        assertFalse(query.hasSort());
        assertNull(query.name());
        assertNull(query.sort());
    }
}
