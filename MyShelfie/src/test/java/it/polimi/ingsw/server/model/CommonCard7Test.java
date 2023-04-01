package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.TileType;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;


public class CommonCard7Test {
    private CommonCard7 testCard7;

    @Before
    public void SetUp(){
        testCard7 = new CommonCard7("7.jpg", "Description CommonCard7");
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

        assertFalse(this.testCard7.checkObjective(libTest));
    }

    @Test
    public void checkObjective_4RightRows_true(){
        TileType[][] libTest =
                        {{TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.BOOK},
                        {TileType.TOY, TileType.PLANT, TileType.BOOK, TileType.FRAME, TileType.CAT, TileType.TROPHY},
                        {TileType.PLANT, TileType.PLANT, TileType.BOOK, TileType.TOY, TileType.BOOK, null},
                        {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.BOOK},
                        {TileType.TOY, TileType.PLANT, TileType.BOOK, TileType.TROPHY, TileType.CAT, TileType.TROPHY}};

        assertTrue(this.testCard7.checkObjective(libTest));
    }

    @Test
    public void checkObjective_3RightRows_false(){
        TileType[][] libTest =
                        {{TileType.BOOK, TileType.TROPHY, TileType.PLANT, null, null, null},
                        {TileType.TOY, TileType.PLANT, TileType.BOOK, TileType.FRAME, TileType.CAT, TileType.TROPHY},
                        {TileType.PLANT, TileType.PLANT, TileType.BOOK, TileType.TOY, TileType.BOOK, null},
                        {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.BOOK},
                        {TileType.TOY, TileType.PLANT, TileType.BOOK, TileType.TROPHY, TileType.CAT, TileType.TROPHY}};

        assertFalse(this.testCard7.checkObjective(libTest));
    }

    @Test
    public void checkObjective_allDifferentInRowsConfiguration_false(){
        TileType[][] libTest =
                        {{TileType.BOOK, TileType.FRAME, TileType.TROPHY, TileType.CAT, TileType.BOOK, TileType.BOOK},
                        {TileType.TOY, TileType.PLANT, TileType.FRAME, TileType.FRAME, TileType.CAT, TileType.TROPHY},
                        {TileType.PLANT, TileType.TROPHY, TileType.BOOK, TileType.TOY, TileType.TROPHY, TileType.CAT},
                        {TileType.FRAME, TileType.BOOK, TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.BOOK},
                        {TileType.TOY, TileType.PLANT, TileType.BOOK, TileType.TROPHY, TileType.CAT, TileType.PLANT}};

        assertFalse(this.testCard7.checkObjective(libTest));
    }

}
