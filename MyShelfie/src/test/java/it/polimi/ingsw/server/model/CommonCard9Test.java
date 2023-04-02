package it.polimi.ingsw.server.model;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.model.CommonCardImpl.CommonCard9;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommonCard9Test {
    private CommonCard9 testCard9;

    @Before
    public void SetUp(){
        testCard9 = new CommonCard9("9.jpg", "Description CommonCard9");
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
        assertFalse(this.testCard9.checkObjective(libTest));
    }

    @Test
    public void checkObjective_8EqualsTiles_true(){
        TileType[][] libTest =
                        {{TileType.BOOK, TileType.PLANT, TileType.FRAME, TileType.PLANT, TileType.CAT, TileType.FRAME},
                        {TileType.TOY, TileType.PLANT, TileType.BOOK, TileType.TROPHY, TileType.CAT, TileType.TROPHY},
                        {TileType.PLANT, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, null},
                        {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.TROPHY, TileType.BOOK},
                        {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.TROPHY, TileType.BOOK}};

        assertTrue(this.testCard9.checkObjective(libTest));
    }

    @Test
    public void checkObjective_LessThan8_Occurrence_false(){
        TileType[][] libTest =
                {{TileType.BOOK, TileType.PLANT, TileType.FRAME, TileType.PLANT, TileType.CAT, TileType.FRAME},
                        {TileType.TOY, TileType.PLANT, TileType.BOOK, TileType.TROPHY, TileType.CAT, TileType.TROPHY},
                        {TileType.PLANT, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, null},
                        {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.TROPHY, TileType.TOY},
                        {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.TROPHY, TileType.BOOK}};

        assertFalse(this.testCard9.checkObjective(libTest));
    }


}
