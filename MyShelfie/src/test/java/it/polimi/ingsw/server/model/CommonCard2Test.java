package it.polimi.ingsw.server.model;
import it.polimi.ingsw.common.TileType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class CommonCard2Test {

    private CommonCard2 TestCard2;
    final int LIBRARY_WIDTH = 5;
    final int LIBRARY_HEIGHT = 6;
    private TileType[][] libTest;

    @Before
    public void SetUp(){
        //Create a new commonCard2 and a new library
        TestCard2 = new CommonCard2("jpg.2", "Two columns each formed by 6 \n" +
                "different types of tiles.");
        libTest = new TileType[LIBRARY_WIDTH][LIBRARY_HEIGHT];
    }

    @After
    public void TearDown(){ };

    @Test
    public void checkObjective_emptyLibrary_false(){
        assertFalse(this.TestCard2.checkObjective(libTest));
    }

    @Test
    public void checkObjective_noColumnsWithSixDifferentTileType_false(){
        libTest = new TileType[][]{{TileType.CAT, TileType.BOOK, TileType.CAT, TileType.BOOK, TileType.CAT, TileType.BOOK},
                {TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.TROPHY},
                {TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT},
                {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK},
                {TileType.FRAME, TileType.TROPHY, TileType.FRAME, TileType.TROPHY, TileType.FRAME, TileType.TROPHY}};
        assertFalse(this.TestCard2.checkObjective(libTest));
    }

    @Test
    public void checkObjective_columnWithNull_false(){
        libTest = new TileType[][]{{TileType.CAT, TileType.BOOK, TileType.CAT, TileType.BOOK, null, null},
                {TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.TROPHY},
                {TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT},
                {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK},
                {TileType.FRAME, TileType.TROPHY, TileType.FRAME, TileType.TROPHY, null, null}};
        assertFalse(this.TestCard2.checkObjective(libTest));
    }

    @Test
    public void checkObjective_oneColumnWithSixDifferentTileType_false(){
        libTest = new TileType[][]{{TileType.CAT, TileType.BOOK, TileType.TOY, TileType.TROPHY, TileType.PLANT, TileType.FRAME},
                {TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.TROPHY},
                {TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT},
                {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK},
                {TileType.CAT, TileType.TROPHY, TileType.CAT, TileType.TROPHY, TileType.CAT, TileType.TROPHY}};
        assertFalse(this.TestCard2.checkObjective(libTest));
    }

    @Test
    public void checkObjective_twoColumnsWithSixDifferentTileType_true(){
        libTest = new TileType[][]{{TileType.CAT, TileType.BOOK, TileType.CAT, TileType.BOOK, TileType.CAT, TileType.BOOK},
                {TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.TROPHY},
                {TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.TROPHY, TileType.FRAME, TileType.TOY},
                {TileType.TOY, TileType.BOOK, TileType.CAT, TileType.FRAME, TileType.TROPHY, TileType.PLANT},
                {TileType.CAT, TileType.TROPHY, TileType.CAT, TileType.TROPHY, TileType.CAT, TileType.TROPHY}};
        assertTrue(this.TestCard2.checkObjective(libTest));

    }

}

