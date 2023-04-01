package it.polimi.ingsw.server.model;
import it.polimi.ingsw.common.TileType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class CommonCard5Test{
    private CommonCard5 TestCard5;
    final int LIBRARY_WIDTH = 5;
    final int LIBRARY_HEIGHT = 6;
    private TileType[][] libTest;

    @Before
    public void SetUp(){
        //Create a new commonCard5 and a new library
        TestCard5 = new CommonCard5("jpg.5", "Three columns each formed by 6 tiles \n"+
                "of maximum three different types.");
        libTest = new TileType[LIBRARY_WIDTH][LIBRARY_HEIGHT];
    }

    @After
    public void TearDown(){ };

    @Test
    public void checkObjective_emptyLibrary_false(){
        assertFalse(this.TestCard5.checkObjective(libTest));
    }

    @Test
    public void checkObjective_noColumnWithAtLeastThreeTypes_false(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.BOOK, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.BOOK, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.BOOK, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.BOOK, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.BOOK, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK}};
        assertFalse(this.TestCard5.checkObjective(libTest));
    }

    @Test
    public void checkObjective_noFullColumns_false(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.BOOK, TileType.CAT, TileType.BOOK, null, null},
                {TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.TROPHY, TileType.TOY, null},
                {TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT, TileType.PLANT, null},
                {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.TOY, null},
                {TileType.FRAME, TileType.TROPHY, TileType.FRAME, TileType.TROPHY, null, null}};
        assertFalse(this.TestCard5.checkObjective(libTest));
    }

    @Test
    public void checkObjective_oneColumnWithSixDifferentTileType_false(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.BOOK, TileType.TOY, TileType.TROPHY, TileType.PLANT, TileType.FRAME},
                {TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.TROPHY},
                {TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT},
                {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK},
                {TileType.CAT, TileType.TROPHY, TileType.CAT, TileType.TROPHY, TileType.CAT, TileType.TROPHY}};
        assertFalse(this.TestCard5.checkObjective(libTest));
    }

    @Test
    public void checkObjective_oneColumnsWithAtLeastThreeTypes_false(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.BOOK, TileType.CAT, TileType.BOOK, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.BOOK, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.BOOK, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.BOOK, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.BOOK, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK}};
        assertFalse(this.TestCard5.checkObjective(libTest));

    }
    @Test
    public void checkObjective_ThreeColumnsWithAtLeastThreeTypes_true(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.BOOK, TileType.CAT, TileType.BOOK, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.BOOK, TileType.TOY, TileType.CAT, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.BOOK, TileType.FRAME, TileType.CAT, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.BOOK, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.BOOK, TileType.TOY, TileType.FRAME, TileType.CAT, TileType.BOOK}};
        assertTrue(this.TestCard5.checkObjective(libTest));

    }
}
