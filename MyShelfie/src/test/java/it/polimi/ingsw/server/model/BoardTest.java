package it.polimi.ingsw.server.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoardTest {
    @Before
    public void setUp() { /* Do nothing*/ }

    @After
    public void tearDown() { /* Do nothing */ }


    // ==================== refillBoard ====================
    // TODO


    // ==================== pick ====================
    // TODO


    // ==================== definePickable ====================
    // TODO


    // ==================== isTileUsable ====================
    @Test
    public void isTileUsable_tileNeverUsable_shouldReturnFalse() {
        // We check some cells which certainly are not to be used,
        // no matter how many players
        assertFalse(Board.isTileUsable(0, 0, 2));
        assertFalse(Board.isTileUsable(0, 0, 3));
        assertFalse(Board.isTileUsable(0, 0, 4));
        assertFalse(Board.isTileUsable(2, 8, 2));
        assertFalse(Board.isTileUsable(6, 1, 3));
        assertFalse(Board.isTileUsable(7, 7, 4));
    }

    @Test
    public void isTileUsable_tileNotUsableTooFewPlayers_shouldReturnFalse() {
        // We check a tiles which require more players than the ones we pass
        assertFalse(Board.isTileUsable(0, 5, 2));
        assertFalse(Board.isTileUsable(0, 5, 3));
    }

    @Test
    public void isTileUsable_tileUsableEnoughPlayers_shouldReturnTrue() {
        // We test the three cases of 2, 3, 4 players and check the
        assertTrue(Board.isTileUsable(1, 3, 2));
        assertTrue(Board.isTileUsable(1, 3, 3));
        assertTrue(Board.isTileUsable(1, 3, 4));
    }
}
