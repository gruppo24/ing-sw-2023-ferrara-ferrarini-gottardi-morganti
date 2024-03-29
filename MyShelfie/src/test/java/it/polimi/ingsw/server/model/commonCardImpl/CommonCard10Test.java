package it.polimi.ingsw.server.model.commonCardImpl;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.model.CommonCardImpl.CommonCard10;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;


public class CommonCard10Test {
    private CommonCard10 testCard10;


    @Before
    public void SetUp(){
        testCard10 = new CommonCard10 ("10.jpg", "Description CommonCard10");
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
        assertFalse(this.testCard10.checkObjective(libTest));
    }

    @Test
    public void checkObjective_XConfiguration_true(){
        TileType[][] libTest =
                {{TileType.BOOK, TileType.PLANT, TileType.FRAME, TileType.PLANT, TileType.CAT, TileType.FRAME},
                        {TileType.TOY, TileType.PLANT, TileType.TOY, TileType.TROPHY, TileType.CAT, TileType.TROPHY},
                        {TileType.PLANT, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, null},
                        {TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.CAT, TileType.TROPHY, TileType.BOOK},
                        {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.TROPHY, TileType.BOOK}};

        assertTrue(this.testCard10.checkObjective(libTest));
    }

    @Test
    public void checkObjective_NoXConfiguration_false(){
        TileType[][] libTest =
                {{TileType.BOOK, TileType.PLANT, TileType.FRAME, TileType.PLANT, TileType.CAT, TileType.FRAME},
                        {TileType.TOY, TileType.PLANT, TileType.TOY, TileType.TROPHY, TileType.CAT, TileType.TROPHY},
                        {TileType.PLANT, TileType.FRAME, TileType.BOOK, TileType.TOY, TileType.BOOK, null},
                        {TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.CAT, TileType.TROPHY, TileType.BOOK},
                        {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.TROPHY, TileType.BOOK}};

        assertFalse(this.testCard10.checkObjective(libTest));
    }


}
