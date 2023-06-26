package it.polimi.ingsw.common;


import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TileTypeTest {

    @Test
    public void toString_forEachTile_returnsCorrectStringName() {
        assertEquals(TileType.BOOK.toString(), "Bk");
        assertEquals(TileType.CAT.toString(), "Ca");
        assertEquals(TileType.FRAME.toString(), "Fr");
        assertEquals(TileType.PLANT.toString(), "Pl");
        assertEquals(TileType.TOY.toString(), "To");
        assertEquals(TileType.TROPHY.toString(), "Ty");
    }

    @Test
    public void getAssetName_forEachTile_returnsCorrectAssetName() {
        assertEquals(TileType.BOOK.getAssetName(), "Libri1.1.png");
        assertEquals(TileType.CAT.getAssetName(), "Gatti1.1.png");
        assertEquals(TileType.FRAME.getAssetName(), "Cornici1.1.png");
        assertEquals(TileType.PLANT.getAssetName(), "Piante1.1.png");
        assertEquals(TileType.TOY.getAssetName(), "Giochi1.1.png");
        assertEquals(TileType.TROPHY.getAssetName(), "Trofei1.1.png");
    }

}
