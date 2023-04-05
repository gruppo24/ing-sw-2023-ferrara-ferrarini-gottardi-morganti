package it.polimi.ingsw.server.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PrivateCardTest {
    @Before
    public void setUp() { /* Do nothing */ }

    @After
    public void tearDown() { /* Do nothing */ }

    // ==================== mapPrivatePoints ====================
    @Test (expected = IllegalArgumentException.class)
    public void mapPrivatePoints_illegalAmountOfMatches_shouldThrowException() {
        // The only use-case which isn't covered already by the PlayerTest
        // class is the scenario in which we give mapPrivatePoints an
        // illegal number of completed objectives...
        PrivateCard.mapPrivatePoints(7);
    }
}
