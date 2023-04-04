package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.exceptions.AlreadyUsedIndex;
import it.polimi.ingsw.server.exceptions.InvalidReorderingIndices;
import it.polimi.ingsw.server.exceptions.SelectionBufferFullException;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;


public class PlayerTest {

    private Player testPlayer;

    private int LIBRARY_WIDTH;
    private int LIBRARY_HEIGHT;

    @Before
    public void setUp() {
        // Create a new sample player with a generic private objective
        // NOTE: all private objective cards are tested else where,
        // thus, if private objective cards' tests pass, any card will
        // do for Player testing
        PrivateCard privateCard = new PrivateCard("Personal_Goals3",
                Map.ofEntries(
                    Map.entry(TileType.BOOK, new Integer[]{0, 0}),
                    Map.entry(TileType.FRAME, new Integer[]{0, 4}),
                    Map.entry(TileType.CAT, new Integer[]{1, 2}),
                    Map.entry(TileType.PLANT, new Integer[]{2, 3}),
                    Map.entry(TileType.TOY, new Integer[]{3, 4}),
                    Map.entry(TileType.TROPHY, new Integer[]{4, 2})
                )
        );
        testPlayer = new Player("randomUsername");
        testPlayer.setPrivateCard(privateCard);

        // We now request a copy of its library, so that we
        // can store its size (for later use...)
        TileType[][] library = testPlayer.getLibrary();
        this.LIBRARY_WIDTH = library.length;
        this.LIBRARY_HEIGHT = library[0].length;
    }

    @After
    public void tearDown() { /* Do nothing */ }

    // ==================== equals ====================
    @Test
    public void equals_withNull_shouldReturnFalse() {
        assertFalse(this.testPlayer.equals(null));
    }

    @Test
    public void equals_withRandomObject_shouldReturnFalse() {
        assertFalse(this.testPlayer.equals(new Board()));
    }

    @Test
    public void equals_withDifferentPlayer_shouldReturnFalse() {
        assertFalse(this.testPlayer.equals(new Player("Random")));
    }

    @Test
    public void equals_withIdenticalPlayer_shouldReturnTrue() {
        assertTrue(this.testPlayer.equals(this.testPlayer));
    }

    @Test
    public void equals_withDifferentPlayerWithSameNickname_shouldReturnTrue() {
        assertTrue(this.testPlayer.equals(new Player(this.testPlayer.nickname)));
    }

    // ==================== updatePrivatePoints ====================
    @Test
    public void updatePrivatePoints_emptyLibrary_shouldReturnZero() {
        this.testPlayer.updatePrivatePoints();
        assertEquals(this.testPlayer.getPrivatePoints(), 0);
    }

    @Test
    public void updatePrivatePoints_noObjectiveCompleted_shouldReturnZero() {
        // This time we fill the library with sample values

        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(1);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(3);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.pushTileToSelectionBuffer(TileType.PLANT);
        this.testPlayer.flushBufferIntoLibrary();
        this.testPlayer.selectColumn(3);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(4);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.updatePrivatePoints();
        assertEquals(this.testPlayer.getPrivatePoints(), 0);
    }

    @Test
    public void updatePrivatePoints_allObjectivesCompleted_shouldReturn12() {
        // As we complete all objectives, we assert the exact number of points
        // which the player should have achieved until then

        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.updatePrivatePoints();
        assertEquals(this.testPlayer.getPrivatePoints(), 1);

        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.updatePrivatePoints();
        assertEquals(this.testPlayer.getPrivatePoints(), 2);

        this.testPlayer.selectColumn(1);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.updatePrivatePoints();
        assertEquals(this.testPlayer.getPrivatePoints(), 4);

        this.testPlayer.selectColumn(2);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.pushTileToSelectionBuffer(TileType.PLANT);
        this.testPlayer.flushBufferIntoLibrary();
        this.testPlayer.selectColumn(2);
        this.testPlayer.pushTileToSelectionBuffer(TileType.PLANT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.updatePrivatePoints();
        assertEquals(this.testPlayer.getPrivatePoints(), 6);

        this.testPlayer.selectColumn(3);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.pushTileToSelectionBuffer(TileType.PLANT);
        this.testPlayer.flushBufferIntoLibrary();
        this.testPlayer.selectColumn(3);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.updatePrivatePoints();
        assertEquals(this.testPlayer.getPrivatePoints(), 9);

        this.testPlayer.selectColumn(4);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TROPHY);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.updatePrivatePoints();
        assertEquals(this.testPlayer.getPrivatePoints(), 12);
    }

    // ==================== updateClusterPoints ====================
    @Test
    public void updateClusterPoints_libraryEmpty_shouldReturnZero() {
        testPlayer.updateClusterPoints();
        assertEquals(testPlayer.getClusterPoints(), 0);
    }

    @Test
    public void updateClusterPoints_noClusters_shouldReturnZero() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(1);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(3);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.pushTileToSelectionBuffer(TileType.PLANT);
        this.testPlayer.flushBufferIntoLibrary();
        this.testPlayer.selectColumn(3);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(4);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.flushBufferIntoLibrary();

        testPlayer.updateClusterPoints();
        assertEquals(testPlayer.getClusterPoints(), 0);
    }

    @Test
    public void updateClusterPoints_oneClusterSizeTwo_shouldReturnZero() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.updateClusterPoints();
        assertEquals(this.testPlayer.getClusterPoints(), 0);
    }

    @Test
    public void updateClusterPoints_oneClusterSizeThree_shouldReturnTwo() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(1);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.updateClusterPoints();
        assertEquals(this.testPlayer.getClusterPoints(), 2);
    }

    @Test
    public void updateClusterPoints_oneClusterSizeFour_shouldReturnThree() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(1);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.updateClusterPoints();
        assertEquals(this.testPlayer.getClusterPoints(), 3);
    }

    @Test
    public void updateClusterPoints_oneClusterSizeFive_shouldReturnFive() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(1);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(2);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.updateClusterPoints();
        assertEquals(this.testPlayer.getClusterPoints(), 5);
    }

    @Test
    public void updateClusterPoints_oneClusterSizeSix_shouldReturnEight() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(1);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(2);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.updateClusterPoints();
        assertEquals(this.testPlayer.getClusterPoints(), 8);
    }

    @Test
    public void updateClusterPoints_oneClusterLargerThanSix_shouldReturnEight() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(1);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(2);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(3);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.updateClusterPoints();
        assertEquals(this.testPlayer.getClusterPoints(), 8);
    }

    @Test
    public void updateClusterPoints_oneClusterPerSize_shouldReturnEightTeen() {
        /*
         * Board state which we are setting up for test
         5 |
         4 |                   TOY   TOY
         3 |                   FRAME TOY
         2 | BOOK  BOOK        FRAME TOY
         1 | BOOK  FRAME FRAME FRAME TOY
         0 | CAT   CAT   CAT   CAT   TOY
         *   0     1     2     3     4
         */
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(1);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(2);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(3);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.flushBufferIntoLibrary();
        this.testPlayer.selectColumn(3);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(4);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.flushBufferIntoLibrary();
        this.testPlayer.selectColumn(4);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.flushBufferIntoLibrary();


        this.testPlayer.updateClusterPoints();
        assertEquals(this.testPlayer.getClusterPoints(), 18);
    }

    // ==================== checkIfFilled ====================
    @Test
    public void checkIfFilled_emptyLibrary_shouldReturnFalse() {
        assertFalse(this.testPlayer.checkIfFilled());
    }

    @Test
    public void checkIfFilled_notFilledLibrary_shouldReturnFalse() {
        // We fill the library a bit...
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(1);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(2);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(3);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.flushBufferIntoLibrary();
        this.testPlayer.selectColumn(3);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(4);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.flushBufferIntoLibrary();
        this.testPlayer.selectColumn(4);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.flushBufferIntoLibrary();

        assertFalse(this.testPlayer.checkIfFilled());
    }

    @Test
    public void checkIfFilled_libraryIsFull_shouldReturnTrue() {
        for (int column=0; column < this.LIBRARY_WIDTH; column++) {
            for (int counter=0; counter < 2; counter++) {
                this.testPlayer.selectColumn(column);
                this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
                this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
                this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
                this.testPlayer.flushBufferIntoLibrary();
            }
        }

        assertTrue(this.testPlayer.checkIfFilled());
    }

    // ==================== getLibrary ====================
    @Test
    public void getLibrary_emptyLibrary_shouldReturnEmptyLibrary() {
        // At user creation, library is by default empty. Let's check:
        TileType[][] library = this.testPlayer.getLibrary();

        // We simply count how many NON-NULL cells we have
        for (int column=0; column < this.LIBRARY_WIDTH; column++)
            for (int row=0; row < this.LIBRARY_HEIGHT; row++)
                assertNull(library[column][row]);
    }

    @Test
    public void getLibrary_editingLibraryCopy_shouldNotEditPlayersLibrary() {
        // To test this feature, we initially populate our library.
        // Subsequently, we request a copy via the getLibrary method,
        // we edit a cell, then request a copy of the library again.
        // The returned library shouldn't present any modification.

        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(1);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.pushTileToSelectionBuffer(TileType.PLANT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(3);
        this.testPlayer.pushTileToSelectionBuffer(TileType.TOY);
        this.testPlayer.flushBufferIntoLibrary();

        // We now request a copy and edit it ===> no modification should occur
        TileType[][] lib_copy = this.testPlayer.getLibrary();
        lib_copy[0][1] = TileType.FRAME;

        assertEquals(lib_copy[0][1], TileType.FRAME);
        assertEquals(this.testPlayer.getLibrary()[0][1], TileType.BOOK);
    }

    // ==================== selectColumn ====================
    @Test
    public void selectColumn_selectColumn_shouldSelectColumn() {
        // We select, one at the time, all columns, then check all
        // selections occur successfully
        for (int column=0; column < this.LIBRARY_WIDTH; column++) {
            this.testPlayer.selectColumn(column);
            assertEquals(column, this.testPlayer.getSelectedColumn());
        }
    }

    @Test (expected = IndexOutOfBoundsException.class)
    public void selectColumn_selectionOutOfRange_shouldThrowException() {
        testPlayer.selectColumn(this.LIBRARY_WIDTH);
    }


    // ==================== getSelectionBufferSize ====================
    @Test
    public void getSelectionBufferSize_noBuffer_shouldReturnZero() {
        assertEquals(this.testPlayer.getSelectionBufferSize(), 0);
    }

    @Test
    public void getSelectionBufferSize_emptyBuffer_shouldReturn3() {
        for (int column=0; column < this.LIBRARY_WIDTH; column++) {
            this.testPlayer.selectColumn(column);
            assertEquals(this.testPlayer.getSelectionBufferSize(), 3);
        }
    }

    @Test
    public void getSelectionBufferSize_columnUsed_shouldReturn2() {
        // We start filling each column and check that the buffer-size
        // updates correctly
        for (int column=0; column < this.LIBRARY_WIDTH; column++) {
            // Selecting a column, filling the buffer
            this.testPlayer.selectColumn(column);
            this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
            this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
            this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
            this.testPlayer.flushBufferIntoLibrary();

            // Adding only one more tile
            this.testPlayer.selectColumn(column);
            this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
            this.testPlayer.flushBufferIntoLibrary();

            this.testPlayer.selectColumn(column);
            assertEquals(this.testPlayer.getSelectionBufferSize(), 2);
        }
    }

    @Test
    public void getSelectionBufferSize_columnUsed_shouldReturn1() {
        // We start filling each column and check that the buffer-size
        // updates correctly
        for (int column=0; column < this.LIBRARY_WIDTH; column++) {
            // Selecting a column, filling the buffer
            this.testPlayer.selectColumn(column);
            this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
            this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
            this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
            this.testPlayer.flushBufferIntoLibrary();

            // Adding only two more tiles
            this.testPlayer.selectColumn(column);
            this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
            this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
            this.testPlayer.flushBufferIntoLibrary();

            this.testPlayer.selectColumn(column);
            assertEquals(this.testPlayer.getSelectionBufferSize(), 1);
        }
    }

    @Test
    public void getSelectionBufferSize_columnUsed_shouldReturn0() {
        // We start filling each column and check that the buffer-size
        // updates correctly
        for (int column=0; column < this.LIBRARY_WIDTH; column++) {
            // Selecting a column, filling the buffer
            this.testPlayer.selectColumn(column);
            this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
            this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
            this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
            this.testPlayer.flushBufferIntoLibrary();

            // Filling out also this second buffer
            this.testPlayer.selectColumn(column);
            this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
            this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
            this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
            this.testPlayer.flushBufferIntoLibrary();

            this.testPlayer.selectColumn(column);
            assertEquals(this.testPlayer.getSelectionBufferSize(), 0);
        }
    }

    // ==================== pushTileToSelectionBuffer ====================
    @Test
    public void pushTileToSelectionBuffer_oneTilePushed_pushesOneTile() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);

        TileType[] buffer_copy = this.testPlayer.getSelectionBufferCopy();
        assertEquals(buffer_copy[0], TileType.CAT);
        assertNull(buffer_copy[1]);
        assertNull(buffer_copy[2]);
    }

    @Test
    public void pushTileToSelectionBuffer_twoTilesPushed_pushesTwoTiles() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);

        TileType[] buffer_copy = this.testPlayer.getSelectionBufferCopy();
        assertEquals(buffer_copy[0], TileType.CAT);
        assertEquals(buffer_copy[1], TileType.BOOK);
        assertNull(buffer_copy[2]);
    }

    @Test
    public void pushTileToSelectionBuffer_threeTilesPushed_pushesThreeTiles() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);

        TileType[] buffer_copy = this.testPlayer.getSelectionBufferCopy();
        assertEquals(buffer_copy[0], TileType.CAT);
        assertEquals(buffer_copy[1], TileType.BOOK);
        assertEquals(buffer_copy[2], TileType.FRAME);
    }

    @Test (expected = SelectionBufferFullException.class)
    public void pushTileToSelectionBuffer_bufferFull_shouldThrowException() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
    }

    @Test (expected = SelectionBufferFullException.class)
    public void pushTileToSelectionBuffer_undersizedBufferFull_shouldThrowException() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        // Now the selection buffer will be only of size 1, but we
        // will push two tiles to it
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
    }

    // ==================== getSelectionBufferCopy ====================
    @Test
    public void getSelectionBufferCopy_uninitializedBuffer_shouldReturnNull() {
        assertNull(this.testPlayer.getSelectionBufferCopy());
    }

    @Test
    public void getSelectionBufferCopy_flushedBuffer_shouldReturnNull() {
        // We select a column, push a couple of tiles then flush the buffer.
        // We check that the buffer has been flushed correctly
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        assertNull(this.testPlayer.getSelectionBufferCopy());
    }

    @Test
    public void getSelectionBufferCopy_emptyBuffer_shouldReturnEmptyArray() {
        // We select a column and request a buffer copy. Initial buffer
        // should be empty (all cells null)
        this.testPlayer.selectColumn(0);

        for (TileType tile : this.testPlayer.getSelectionBufferCopy())
            assertNull(tile);
    }

    @Test
    public void getSelectionBufferCopy_editingBufferCopy_shouldNotEditOriginalBuffer() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);

        TileType[] buffer_copy = this.testPlayer.getSelectionBufferCopy();
        buffer_copy[1] = TileType.PLANT;

        assertEquals(buffer_copy[1], TileType.PLANT);
        assertEquals(this.testPlayer.getSelectionBufferCopy()[1], TileType.BOOK);
    }

    // ==================== reorderSelectionBuffer ====================
    @Test
    public void reorderSelectionBuffer_emptyBufferReordering_doesNothing() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.reorderSelectionBuffer(0, 1, 2);

        for (TileType tile : this.testPlayer.getSelectionBufferCopy())
            assertNull(tile);
    }

    @Test
    public void reorderSelectionBuffer_oneItemBuffer_doesNothing() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.reorderSelectionBuffer(0, 1, 2);

        TileType[] buffer_copy = this.testPlayer.getSelectionBufferCopy();
        assertEquals(buffer_copy[0], TileType.CAT);
        assertNull(buffer_copy[1]);
        assertNull(buffer_copy[2]);
    }

    @Test (expected = InvalidReorderingIndices.class)
    public void reorderSelectionBuffer_oneItemBufferInvalidReorder_throwsException() {
        // We try to reorder a two element buffer in an invalid way:
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);

        this.testPlayer.reorderSelectionBuffer(1, 0, 2);
    }

    @Test
    public void reorderSelectionBuffer_twoItemBuffer_reordersCorrectly() {
        // We try to reorder a two element buffer in an invalid way:
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.reorderSelectionBuffer(1, 0, 2);

        TileType[] buffer_copy = this.testPlayer.getSelectionBufferCopy();
        assertEquals(buffer_copy[0], TileType.BOOK);
        assertEquals(buffer_copy[1], TileType.CAT);
        assertNull(buffer_copy[2]);
    }

    @Test (expected = InvalidReorderingIndices.class)
    public void reorderSelectionBuffer_twoItemBufferInvalidReorder_throwsException() {
        // We try to reorder a two element buffer in an invalid way:
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);

        // Index 3 can't be used in reordering since there are only two tiles!
        this.testPlayer.reorderSelectionBuffer(0, 2, 1);
    }

    @Test
    public void reorderSelectionBuffer_threeItemBuffer_reordersCorrectly() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.reorderSelectionBuffer(2, 0, 1);

        TileType[] buffer_copy = this.testPlayer.getSelectionBufferCopy();
        assertEquals(buffer_copy[0], TileType.FRAME);
        assertEquals(buffer_copy[1], TileType.CAT);
        assertEquals(buffer_copy[2], TileType.BOOK);
    }

    @Test (expected = AlreadyUsedIndex.class)
    public void reorderSelectionBuffer_reusedIndices_throwsException() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);

        this.testPlayer.reorderSelectionBuffer(0, 0, 2);
    }

    // ==================== flushBufferIntoLibrary ====================
    @Test (expected = NullPointerException.class)
    public void flushBufferIntoLibrary_flushUninitializedBuffer_throwsException() {
        this.testPlayer.flushBufferIntoLibrary();
    }

    @Test
    public void flushBufferIntoLibrary_flushEmptyBuffer_doesNothing() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.flushBufferIntoLibrary();

        // We now request a copy of the library and check that nothing
        // has been flushed into it ==> all cells are still null
        TileType[][] lib_copy = this.testPlayer.getLibrary();
        for (int column = 0; column < this.LIBRARY_WIDTH; column++)
            for (int row = 0; row < this.LIBRARY_HEIGHT; row++)
                assertNull(lib_copy[column][row]);
    }

    @Test
    public void flushBufferIntoLibrary_oneTileInBuffer_flushesOneTile() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.flushBufferIntoLibrary();

        TileType[][] lib_copy = this.testPlayer.getLibrary();
        for (int column = 0; column < this.LIBRARY_WIDTH; column++)
            for (int row = 0; row < this.LIBRARY_HEIGHT; row++)
                if (column == 0 && row == 0)
                    assertEquals(lib_copy[column][row], TileType.CAT);
                else
                    assertNull(lib_copy[column][row]);
    }

    @Test
    public void flushBufferIntoLibrary_twoTilesInBuffer_flushesTwoTiles() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.flushBufferIntoLibrary();

        TileType[][] lib_copy = this.testPlayer.getLibrary();
        for (int column = 0; column < this.LIBRARY_WIDTH; column++)
            for (int row = 0; row < this.LIBRARY_HEIGHT; row++)
                if (column == 0 && row == 0)
                    assertEquals(lib_copy[column][row], TileType.CAT);
                else if (column == 0 && row == 1)
                    assertEquals(lib_copy[column][row], TileType.BOOK);
                else
                    assertNull(lib_copy[column][row]);
    }

    @Test
    public void flushBufferIntoLibrary_threeTilesInBuffer_flushesThreeTiles() {
        this.testPlayer.selectColumn(0);
        this.testPlayer.pushTileToSelectionBuffer(TileType.CAT);
        this.testPlayer.pushTileToSelectionBuffer(TileType.BOOK);
        this.testPlayer.pushTileToSelectionBuffer(TileType.FRAME);
        this.testPlayer.flushBufferIntoLibrary();

        TileType[][] lib_copy = this.testPlayer.getLibrary();
        for (int column = 0; column < this.LIBRARY_WIDTH; column++)
            for (int row = 0; row < this.LIBRARY_HEIGHT; row++)
                if (column == 0 && row == 0)
                    assertEquals(lib_copy[column][row], TileType.CAT);
                else if (column == 0 && row == 1)
                    assertEquals(lib_copy[column][row], TileType.BOOK);
                else if (column == 0 && row == 2)
                    assertEquals(lib_copy[column][row], TileType.FRAME);
                else
                    assertNull(lib_copy[column][row]);
    }
}
