package it.polimi.ingsw.server.model;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommonCard12Test {

    private CommonCard12 testCard12;

    @Before
    public void SetUp(){
        testCard12 = new CommonCard12 ("Jpeg.12", "Description CommonCard12");
    }

    @After
    public void TearDown(){
        /* Do nothing */
    }

    @Test
    public void checkObjective_emptyLibrary_false(){
        final int LIBRARY_WIDTH = 5;
        final int LIBRARY_HEIGHT = 6;

        TileType[][] libTest = new TileType[LIBRARY_WIDTH][LIBRARY_HEIGHT];

        assertFalse(this.testCard12.checkObjective(libTest));
    }

    @Test
    public void checkObjective_risingRightConfig_true(){
        TileType[][] libTest =
                        {{TileType.BOOK, null, null, null, null, null},
                        {TileType.TOY, TileType.PLANT, null, null, null, null},
                        {TileType.PLANT, TileType.PLANT, TileType.BOOK, null, null, null},
                        {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, null, null},
                        {TileType.TOY, TileType.PLANT, TileType.BOOK, TileType.TROPHY, TileType.CAT, null}};

        assertTrue(this.testCard12.checkObjective(libTest));
    }

    @Test
    public void checkObjective_risingLeftConfig_true(){
        TileType[][] libTest =
                        {{TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, null},
                        {TileType.TOY, TileType.PLANT, TileType.BOOK, TileType.FRAME, null, null},
                        {TileType.PLANT, TileType.PLANT, TileType.BOOK, null, null, null},
                        {TileType.BOOK, TileType.TROPHY, null, null, null, null},
                        {TileType.TOY, null, null, null, null, null}};

        assertTrue(this.testCard12.checkObjective(libTest));
    }

    @Test
    public void checkObjective_noConfiguration_false(){
        TileType[][] libTest =
                {{TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.BOOK},
                        {TileType.TOY, TileType.PLANT, TileType.BOOK, TileType.FRAME, TileType.CAT, TileType.TROPHY},
                        {TileType.PLANT, TileType.PLANT, TileType.BOOK, TileType.TOY, TileType.BOOK, null},
                        {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.BOOK},
                        {TileType.TOY, TileType.PLANT, TileType.BOOK, TileType.TROPHY, TileType.CAT, TileType.TROPHY}};

        assertFalse(this.testCard12.checkObjective(libTest));
    }
}
