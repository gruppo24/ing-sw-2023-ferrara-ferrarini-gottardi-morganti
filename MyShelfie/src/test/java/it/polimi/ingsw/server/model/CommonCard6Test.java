package it.polimi.ingsw.server.model;
import it.polimi.ingsw.common.TileType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class CommonCard6Test {
    private CommonCard6 TestCard6;
    final int LIBRARY_WIDTH = 5;
    final int LIBRARY_HEIGHT = 6;
    private TileType[][] libTest;

    @Before
    public void SetUp(){
        //Create a new commonCard6 and a new library
        TestCard6 = new CommonCard6("jpg.6", "Two lines each formed by 5 different \n" +
                "types of tiles.");
        libTest = new TileType[LIBRARY_WIDTH][LIBRARY_HEIGHT];
    }

    @After
    public void TearDown(){ };

    @Test
    public void checkObjective_emptyLibrary_false(){
        assertFalse(this.TestCard6.checkObjective(libTest));
    }

    @Test
    public void checkObjective_noRowsWithFiveTypes_false(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.BOOK, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.BOOK, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.BOOK, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.BOOK, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.BOOK, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK}};
        assertFalse(this.TestCard6.checkObjective(libTest));
    }

    @Test
    public void checkObjective_noFullRows_false(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.BOOK, TileType.CAT, TileType.BOOK, null, null},
                {TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.TROPHY, TileType.TOY, null},
                {TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT, TileType.PLANT, null},
                {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.TOY, null},
                {null, null, null, null, null, null}};
        assertFalse(this.TestCard6.checkObjective(libTest));
    }

    @Test
    public void checkObjective_oneRowWithFiveDifferentTileType_false(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.BOOK, TileType.TOY, TileType.TROPHY, TileType.PLANT, TileType.FRAME},
                {TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.TROPHY},
                {TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT},
                {TileType.BOOK, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK},
                {TileType.FRAME, TileType.TROPHY, TileType.CAT, TileType.TROPHY, TileType.CAT, TileType.TROPHY}};
        assertFalse(this.TestCard6.checkObjective(libTest));
    }

    @Test
    public void checkObjective_twoRowsWithFiveDifferentTileType_true(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.BOOK, TileType.CAT, TileType.BOOK, TileType.CAT, TileType.BOOK},
                {TileType.BOOK, TileType.CAT, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK},
                {TileType.FRAME, TileType.TOY, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK},
                {TileType.TOY, TileType.FRAME, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK},
                {TileType.TROPHY, TileType.TROPHY, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK}};
        assertTrue(this.TestCard6.checkObjective(libTest));

    }

}
