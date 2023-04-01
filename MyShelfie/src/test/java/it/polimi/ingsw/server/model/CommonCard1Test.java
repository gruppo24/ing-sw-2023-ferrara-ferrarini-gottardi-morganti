package it.polimi.ingsw.server.model;
import it.polimi.ingsw.common.TileType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class CommonCard1Test {
    private CommonCard1 TestCard1;
    final int LIBRARY_WIDTH = 5;
    final int LIBRARY_HEIGHT = 6;
    private TileType[][] libTest;

    @Before
    public void SetUp(){
        //Create a new commonCard2 and a new library
        TestCard1 = new CommonCard1("jpg.1", "Two groups each containing 4 tiles of \n" +
                "the same type in a 2x2 square.");
        libTest = new TileType[LIBRARY_WIDTH][LIBRARY_HEIGHT];
    }
    @After
    public void TearDown(){ };

    @Test
    public void checkObjective_emptyLibrary_false(){

        assertFalse(this.TestCard1.checkObjective(libTest));
    }

    @Test
    public void checkObjective_no2X2SubMatrices_false(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.BOOK, TileType.CAT, TileType.BOOK, TileType.CAT, TileType.BOOK},
                {TileType.CAT, TileType.TROPHY, TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.TROPHY},
                {TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT},
                {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK},
                {TileType.FRAME, TileType.TROPHY, TileType.FRAME, TileType.TROPHY, TileType.FRAME, TileType.TROPHY}};
        assertFalse(this.TestCard1.checkObjective(libTest));
    }

    @Test
    public void checkObjective_one2X2SubMatrix_false(){
        libTest = new TileType[][]{{TileType.CAT, TileType.CAT, TileType.CAT, TileType.BOOK, TileType.BOOK, TileType.BOOK},
                {TileType.CAT, TileType.CAT, TileType.TOY, TileType.TROPHY, TileType.TOY, TileType.TROPHY},
                {TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT},
                {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK},
                {TileType.FRAME, TileType.TROPHY, TileType.FRAME, TileType.TROPHY, TileType.BOOK, TileType.BOOK}};
        assertFalse(this.TestCard1.checkObjective(libTest));
    }

    @Test
    public void checkObjective_matrixWithNull_false(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.CAT, TileType.CAT, TileType.BOOK, TileType.BOOK, TileType.BOOK},
                {TileType.CAT, TileType.CAT, TileType.TOY, TileType.TROPHY, TileType.BOOK, null},
                {TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT},
                {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, null, null},
                {TileType.FRAME, TileType.TROPHY, TileType.FRAME, TileType.TROPHY, null, null}};
        assertFalse(this.TestCard1.checkObjective(libTest));
    }

    @Test
    public void checkObjective_twoAdjacent2x2SubMatrices_true(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.CAT, TileType.BOOK, TileType.BOOK, TileType.PLANT, TileType.FRAME},
                {TileType.CAT, TileType.CAT, TileType.BOOK, TileType.BOOK, TileType.TOY, TileType.TROPHY},
                {TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT},
                {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK},
                {TileType.CAT, TileType.TROPHY, TileType.CAT, TileType.TROPHY, TileType.CAT, TileType.TROPHY}};
        assertTrue(this.TestCard1.checkObjective(libTest));
    }

    @Test
    public void checkObjective_twoNoAdjacentSubMatrices_true(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.CAT, TileType.CAT, TileType.BOOK, TileType.CAT, TileType.CAT},
                {TileType.CAT, TileType.CAT, TileType.TOY, TileType.TROPHY, TileType.CAT, TileType.CAT},
                {TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.TROPHY, TileType.FRAME, TileType.TOY},
                {TileType.TOY, TileType.BOOK, TileType.CAT, TileType.FRAME, TileType.TROPHY, TileType.PLANT},
                {TileType.CAT, TileType.TROPHY, TileType.CAT, TileType.TROPHY, TileType.CAT, TileType.TROPHY}};
        assertTrue(this.TestCard1.checkObjective(libTest));

    }
}
