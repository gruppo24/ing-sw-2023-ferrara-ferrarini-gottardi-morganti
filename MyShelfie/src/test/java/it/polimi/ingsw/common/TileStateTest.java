package it.polimi.ingsw.common;


import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TileStateTest {

    @Test
    public void toString_forEachTile_returnsCorrectStateString() {
        assertEquals(TileState.NOT_PICKABLE.toString(), "0");
        assertEquals(TileState.PICKABLE_NEXT.toString(), "1");
        assertEquals(TileState.PICKABLE.toString(), "2");
    }

}
