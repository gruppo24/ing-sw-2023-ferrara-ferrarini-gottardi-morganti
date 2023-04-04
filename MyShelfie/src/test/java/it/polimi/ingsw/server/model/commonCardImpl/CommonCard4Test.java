package it.polimi.ingsw.server.model.commonCardImpl;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.model.CommonCardImpl.CommonCard4;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;


public class CommonCard4Test {
    private CommonCard4 TestCard4;
    final int LIBRARY_WIDTH = 5;
    final int LIBRARY_HEIGHT = 6;
    private TileType[][] libTest;

    @Before
    public void SetUp(){
        //Create a new commonCard4 and a new library
        TestCard4 = new CommonCard4("jpg.4", "Six groups each containing at least\n" +
                "2 tiles of the same type");
        libTest = new TileType[LIBRARY_WIDTH][LIBRARY_HEIGHT];
    }

    @After
    public void TearDown(){ };

    @Test
    public void checkObjective_emptyLibrary_false(){
        assertFalse(this.TestCard4.checkObjective(libTest));
    }

    @Test
    public void checkObjective_noGroups_false(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.BOOK, TileType.FRAME, TileType.TOY, TileType.TROPHY, TileType.PLANT},
                {TileType.BOOK, TileType.FRAME, TileType.TOY, TileType.TROPHY, TileType.PLANT, TileType.CAT},
                {TileType.FRAME, TileType.TOY, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK},
                {TileType.TOY, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.FRAME},
                {TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.FRAME, TileType.TOY}};
        assertFalse(this.TestCard4.checkObjective(libTest));
    }

    @Test
    public void checkObjective_LConfiguration_false() {
        libTest = new TileType[][]
                {{TileType.CAT, TileType.CAT, TileType.FRAME, TileType.FRAME, TileType.TROPHY, TileType.TROPHY},
                {TileType.BOOK, TileType.BOOK, TileType.TOY, TileType.TOY, TileType.PLANT, TileType.CAT},
                {TileType.FRAME, TileType.PLANT, TileType.TOY, TileType.PLANT, TileType.CAT, TileType.BOOK},
                {TileType.TOY, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.FRAME},
                {TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.FRAME, TileType.TOY}};
        assertFalse(this.TestCard4.checkObjective(libTest));
    }

    @Test
    public void checkObjective_sixNoAdjacentGroups_true(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.CAT, TileType.BOOK, TileType.BOOK, TileType.TOY, TileType.TOY},
                {TileType.TOY, TileType.TOY, TileType.TOY, TileType.TROPHY, TileType.TROPHY, TileType.BOOK},
                {TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.BOOK},
                {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK},
                {TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.FRAME, TileType.TOY}};
        assertTrue(this.TestCard4.checkObjective(libTest));
    }

    @Test
    public void checkObjective_SixAdjacentGroups_true(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.CAT, TileType.CAT, TileType.CAT, TileType.CAT, TileType.CAT},
                {TileType.CAT, TileType.CAT, TileType.CAT, TileType.TROPHY, TileType.TOY, TileType.TROPHY},
                {TileType.PLANT, TileType.BOOK, TileType.CAT, TileType.CAT, TileType.PLANT, TileType.CAT},
                {TileType.BOOK, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.TOY, TileType.BOOK},
                {TileType.FRAME, TileType.CAT, TileType.CAT, TileType.TROPHY, TileType.CAT, TileType.TROPHY}};
        assertTrue(this.TestCard4.checkObjective(libTest));
    }

}
