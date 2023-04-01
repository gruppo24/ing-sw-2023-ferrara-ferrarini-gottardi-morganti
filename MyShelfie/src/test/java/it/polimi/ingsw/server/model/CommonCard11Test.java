package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.model.CommonCardImpl.CommonCard11;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;


public class CommonCard11Test {
    private CommonCard11 testCard11;

    @Before
    public void SetUp(){
        testCard11 = new CommonCard11 ("11.jpg", "Description CommonCard11");
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
        assertFalse(this.testCard11.checkObjective(libTest));
    }

    @Test
    public void checkObjective_rightDiagonal_true(){
        TileType[][] libTest =
                        {{TileType.BOOK, TileType.PLANT, TileType.FRAME, TileType.PLANT, TileType.CAT, TileType.FRAME},
                        {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.TROPHY, TileType.CAT, TileType.TROPHY},
                        {TileType.PLANT, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, null},
                        {TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.BOOK, TileType.TROPHY, TileType.BOOK},
                        {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.BOOK}};

        assertTrue(this.testCard11.checkObjective(libTest));
    }

    @Test
    public void checkObjective_leftDiagonal_true(){
        TileType[][] libTest =
                        {{TileType.BOOK, TileType.PLANT, TileType.FRAME, TileType.PLANT, TileType.CAT, TileType.FRAME},
                        {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.CAT, TileType.CAT, TileType.TROPHY},
                        {TileType.PLANT, TileType.TOY, TileType.CAT, TileType.TOY, TileType.BOOK, null},
                        {TileType.TOY, TileType.CAT, TileType.TOY, TileType.BOOK, TileType.TROPHY, TileType.BOOK},
                        {TileType.CAT, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.BOOK}};

        assertTrue(this.testCard11.checkObjective(libTest));
    }

    @Test
    public void checkObjective_leftAndRightDiagonal_true(){
        TileType[][] libTest =
                        {{TileType.BOOK, TileType.PLANT, TileType.FRAME, TileType.PLANT, TileType.BOOK, TileType.FRAME},
                        {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.CAT, TileType.TROPHY},
                        {TileType.PLANT, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, null},
                        {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.TROPHY, TileType.BOOK},
                        {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.BOOK}};

        assertTrue(this.testCard11.checkObjective(libTest));
    }

    @Test
    public void checkObjective_wrongConfiguration_false(){
        TileType[][] libTest =
                        {{TileType.BOOK, TileType.PLANT, TileType.FRAME, TileType.PLANT, TileType.CAT, null},
                        {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.CAT, TileType.TROPHY},
                        {TileType.PLANT, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, null},
                        {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.TROPHY, TileType.BOOK},
                        {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, null, null}};

        assertFalse(this.testCard11.checkObjective(libTest));
    }




}
