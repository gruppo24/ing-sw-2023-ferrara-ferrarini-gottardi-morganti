package it.polimi.ingsw.server.model.commonCardImpl;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.model.CommonCardImpl.CommonCard3;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

public class CommonCard3Test {
    private CommonCard3 TestCard3;
    final int LIBRARY_WIDTH = 5;
    final int LIBRARY_HEIGHT = 6;
    private TileType[][] libTest;

    @Before
    public void SetUp(){
        //Create a new commonCard3 and a new library
        TestCard3 = new CommonCard3("jpg.3", "Four groups each containing at least\n" +
                "4 tiles of the same type");
        libTest = new TileType[LIBRARY_WIDTH][LIBRARY_HEIGHT];
    }

    @After
    public void TearDown(){ };

    @Test
    public void checkObjective_emptyLibrary_false(){
        assertFalse(this.TestCard3.checkObjective(libTest));
    }

    @Test
    public void checkObjective_noGroups_false(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.BOOK, TileType.FRAME, TileType.TOY, TileType.TROPHY, TileType.PLANT},
                {TileType.BOOK, TileType.FRAME, TileType.TOY, TileType.TROPHY, TileType.PLANT, TileType.CAT},
                {TileType.FRAME, TileType.TOY, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK},
                {TileType.TOY, TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.FRAME},
                {TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.BOOK, TileType.FRAME, TileType.TOY}};
        assertFalse(this.TestCard3.checkObjective(libTest));
    }

    @Test
    public void checkObjective_TConfiguration_false() {
        libTest = new TileType[][]
                {{TileType.CAT, TileType.CAT, TileType.CAT, TileType.FRAME, TileType.FRAME, TileType.TROPHY},
                {TileType.BOOK, TileType.BOOK, TileType.FRAME, TileType.FRAME, TileType.FRAME, TileType.FRAME},
                {TileType.FRAME, TileType.PLANT, TileType.TOY, TileType.FRAME, TileType.FRAME, TileType.BOOK},
                {TileType.TOY, TileType.TROPHY, TileType.FRAME, TileType.FRAME, TileType.FRAME, TileType.FRAME},
                {TileType.TROPHY, TileType.PLANT, TileType.CAT, TileType.PLANT, TileType.CAT, TileType.TOY}};
        assertFalse(this.TestCard3.checkObjective(libTest));
    }

    @Test
    public void checkObjective_fourNoAdjacentGroups_true(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.CAT, TileType.CAT, TileType.CAT, TileType.TOY, TileType.BOOK},
                {TileType.TOY, TileType.FRAME, TileType.TOY, TileType.FRAME, TileType.TROPHY, TileType.BOOK},
                {TileType.PLANT, TileType.PLANT, TileType.PLANT, TileType.PLANT, TileType.PLANT, TileType.BOOK},
                {TileType.TOY, TileType.BOOK, TileType.TOY, TileType.TOY, TileType.TOY, TileType.BOOK},
                {TileType.TROPHY, TileType.TROPHY, TileType.TROPHY, TileType.TROPHY, TileType.FRAME, TileType.TOY}};
        assertTrue(this.TestCard3.checkObjective(libTest));
    }

    @Test
    public void checkObjective_SixAdjacentGroups_true(){
        libTest = new TileType[][]
                {{TileType.CAT, TileType.CAT, TileType.CAT, TileType.CAT, TileType.CAT, TileType.CAT},
                {TileType.CAT, TileType.CAT, TileType.CAT, TileType.CAT, TileType.CAT, TileType.CAT},
                {TileType.CAT, TileType.CAT, TileType.CAT, TileType.CAT, TileType.CAT, TileType.CAT},
                {TileType.BOOK, TileType.BOOK, TileType.TOY, TileType.BOOK, TileType.CAT, TileType.CAT},
                {TileType.FRAME, TileType.CAT, TileType.CAT, TileType.TROPHY, TileType.CAT, TileType.TROPHY}};
        assertTrue(this.TestCard3.checkObjective(libTest));
    }

}
