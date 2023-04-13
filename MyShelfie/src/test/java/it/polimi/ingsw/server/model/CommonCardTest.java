package it.polimi.ingsw.server.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommonCardTest {

    @Before
    public void setUp() {
        /* Do Nothing */ }

    @After
    public void tearDown() {
        /* Do nothing */ }

    // ==================== mapCommonPoints ====================
    @Test
    public void mapCommonPoints_twoPlayers_shouldMatchAllPoints() {
        assertEquals(CommonCard.mapCommonPoints(2, 1), 8);
        assertEquals(CommonCard.mapCommonPoints(2, 2), 4);
    }

    @Test
    public void mapCommonPoints_threePlayers_shouldMatchAllPoints() {
        assertEquals(CommonCard.mapCommonPoints(3, 1), 8);
        assertEquals(CommonCard.mapCommonPoints(3, 2), 6);
        assertEquals(CommonCard.mapCommonPoints(3, 3), 4);
    }

    @Test
    public void mapCommonPoints_fourPlayers_shouldMatchAllPoints() {
        assertEquals(CommonCard.mapCommonPoints(4, 1), 8);
        assertEquals(CommonCard.mapCommonPoints(4, 2), 6);
        assertEquals(CommonCard.mapCommonPoints(4, 3), 4);
        assertEquals(CommonCard.mapCommonPoints(4, 4), 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void mapCommonPoints_twoPlayersInvalidOrder_shouldThrowException() {
        CommonCard.mapCommonPoints(2, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void mapCommonPoints_threePlayersInvalidOrder_shouldThrowException() {
        CommonCard.mapCommonPoints(3, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void mapCommonPoints_fourPlayersInvalidOrder_shouldThrowException() {
        CommonCard.mapCommonPoints(4, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void mapCommonPoints_invalidNumberOfPlayers_shouldThrowException() {
        CommonCard.mapCommonPoints(5, 1);
    }
}
