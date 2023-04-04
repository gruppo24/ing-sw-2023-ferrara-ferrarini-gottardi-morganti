package it.polimi.ingsw.server.model.commonCardImpl;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.model.CommonCardImpl.CommonCard8;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommonCard8Test {
    private CommonCard8 testCard8;
    @Before
    public void SetUp(){
        testCard8 = new CommonCard8("8.jpg", "Description CommonCard8");
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
        assertFalse(this.testCard8.checkObjective(libTest));
    }

    @Test
    public void checkObjective_nullColumns_false(){
        TileType[][] libTest =
                {{null, null, null, null, null, null},
                {TileType.TOY, TileType.PLANT, TileType.BOOK, TileType.TROPHY, TileType.CAT, TileType.TROPHY},
                {TileType.PLANT, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, null},
                {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.BOOK},
                {null, null, null, null, null, null}};

        assertFalse(this.testCard8.checkObjective(libTest));
    }

    @Test
    public void checkObjective_differentCorners_false() {
        TileType[][] libTest =
                {{TileType.BOOK, TileType.PLANT, TileType.FRAME, TileType.PLANT, TileType.CAT, TileType.FRAME},
                {TileType.TOY, TileType.PLANT, TileType.BOOK, TileType.TROPHY, TileType.CAT, TileType.TROPHY},
                {TileType.PLANT, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, null},
                {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.BOOK},
                {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.BOOK}};

        assertFalse(this.testCard8.checkObjective(libTest));
    }

    @Test
    public void checkObjective_correctConfiguration_true(){
        TileType[][] libTest =
                {{TileType.BOOK, TileType.PLANT, TileType.FRAME, TileType.PLANT, TileType.CAT, TileType.BOOK},
                {TileType.TOY, TileType.PLANT, TileType.BOOK, TileType.TROPHY, TileType.CAT, TileType.TROPHY},
                {TileType.PLANT, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, null},
                {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.BOOK},
                {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.BOOK}};

        assertTrue(this.testCard8.checkObjective(libTest));
    }

    @Test
    public void checkObjective_incompleteConfigurationWithNull_true(){
        TileType[][] libTest =
                {{TileType.BOOK, TileType.PLANT, TileType.FRAME, TileType.PLANT, TileType.CAT, TileType.BOOK},
                {TileType.TOY, TileType.PLANT, TileType.BOOK, TileType.TROPHY, TileType.CAT, TileType.TROPHY},
                {TileType.PLANT, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, null},
                {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.BOOK},
                {TileType.BOOK, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, null}};

        assertFalse(this.testCard8.checkObjective(libTest));
    }

}
