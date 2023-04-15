package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.TileState;
import it.polimi.ingsw.common.TileType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;

import java.util.Arrays;

import static org.junit.Assert.*;

public class BoardTest {

    private Board board;

    @Before
    public void setUp() {
        this.board = new Board();
    }

    @After
    public void tearDown() {
        /* Do nothing */ }

    // ==================== shouldBeRefilled ====================
    @Test
    public void shouldBeRefilled_boardEmpty_shouldBeTrue() {
        Assert.assertTrue(board.shouldBeRefilled());
    }

    @Test
    public void shouldBeRefilled_boardNotEmpty_shouldBeFalse() {
        board.refillBoard(2);
        Assert.assertFalse(board.shouldBeRefilled());
    }

    @Test
    public void shouldBeRefilled_boardNotEmptyAfterPick_shouldBeFalse() {
        board.refillBoard(2);
        board.pick(4, 1, 1);
        Assert.assertFalse(board.shouldBeRefilled());
    }

    @Test
    public void shouldBeRefilled_boardNotEmptyAfterPickAll_shouldBeTrue() {
        board.refillBoard(2);
        // pick all tiles except the (4,4) and (5,5), not adjacent cells
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board.getBoardContent()[i][j] != null && !(i == 4 && j == 4) && !(i == 5 && j == 5)) {
                    board.pick(i, j, 1);
                    board.definePickable();
                }
            }
        }
        Assert.assertTrue(board.shouldBeRefilled());
    }

    @Test
    public void shouldBeRefilled_boardNotEmptyAfterPickExceptAdjacent_shouldBeTrue() {
        board.refillBoard(2);

        // pick all tiles except (4,4) and (5,4) two adjacent cells
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board.getBoardContent()[i][j] != null && !(i == 4 && j == 4) && !(i == 5 && j == 4))
                    board.pick(i, j, 1);
            }
        }
        Assert.assertFalse(board.shouldBeRefilled());
    }

    // ==================== refillBoard ====================
    @Test
    public void refillBoard_newBoard_shouldBeEmpty() {
        // board should be empty by default
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Assert.assertNull(board.getBoardContent()[i][j]);
            }
        }
    }

    @Test
    public void refillBoard_boardRefilled_shouldNotBeEmpty() {
        // for each player the board should be not empty when a tile is usable
        // for the given number of players, null otherwise
        for (int numPlayers = 2; numPlayers <= 4; numPlayers++) {
            board.refillBoard(numPlayers);
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (Board.isTileUsable(i, j, numPlayers))
                        Assert.assertNotNull(board.getBoardContent()[i][j]);
                    else
                        Assert.assertNull(board.getBoardContent()[i][j]);
                }
            }
        }
    }

    @Test
    public void refillBoard_refillAfterPick_tilesShouldntChange() {
        board.refillBoard(2);
        board.definePickable();

        // create a copy of old content
        TileType[][] oldContent = Arrays
                .stream(board.getBoardContent())
                .map(TileType[]::clone)
                .toArray(TileType[][]::new);

        // 4, 1 is an always pickable cell
        board.pick(4, 1, 1);
        Assert.assertNull(board.getBoardContent()[4][1]); // picked cell should be null
        board.refillBoard(2);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (i != 4 || j != 1)
                    // all other cells should be the same
                    assertEquals(oldContent[i][j], board.getBoardContent()[i][j]);
                else
                    // the picked cell should at least not be null now
                    Assert.assertNotNull(board.getBoardContent()[i][j]);
            }
        }
    }

    // ==================== pick ====================
    @Test
    public void pick_pickATile_shouldBeEmpty() {
        board.refillBoard(2);
        board.definePickable();

        board.pick(4, 1, 1);
        Assert.assertNull(board.getBoardContent()[4][1]);
    }

    @Test
    public void pick_outOfRange_shouldReturnNUll() {
        board.refillBoard(2);
        board.definePickable();

        Assert.assertNull(this.board.pick(-1, 1, 2));
        Assert.assertNull(this.board.pick(4, -1, 2));
        Assert.assertNull(this.board.pick(99, 1, 2));
        Assert.assertNull(this.board.pick(4, 99, 2));
    }

    @Test
    public void pick_pickATile_shouldBeSameType() {
        board.refillBoard(2);
        board.definePickable();

        TileType shouldBePicked = board.getBoardContent()[4][1];
        TileType isPicked = board.pick(4, 1, 1);
        assertEquals(isPicked, shouldBePicked);
    }

    @Test
    public void pick_picATile_neighborShouldBePickable() {
        board.refillBoard(2);
        board.definePickable();
        assertEquals(board.getBoardState()[4][1], TileState.PICKABLE);
        assertEquals(board.getBoardState()[5][1], TileState.PICKABLE);
        assertEquals(board.getBoardState()[5][2], TileState.PICKABLE);

        board.pick(5, 1, 2);
        TileState[][] newState = board.getBoardState();
        for (int column = 0; column < newState.length; column++)
            for (int row = 0; row < newState[0].length; row++)
                assertTrue(
                        newState[column][row] == TileState.NOT_PICKABLE ||
                        newState[column][row] == TileState.PICKABLE && (
                                (column == 4 && row == 1) || (column == 5 && row == 2)
                        )
                );
    }

    @Test
    public void pick_pickTwice_oneShouldBePickableOneShouldBePickableNext() {
        board.refillBoard(2);
        board.definePickable();

        // We, again, pick a couple of first tiles
        board.pick(5, 1, 2);
        board.pick(4, 1, 2);

        board.definePickable();

        board.pick(5, 2, 2);
        TileState[][] newState = board.getBoardState();
        for (int column = 0; column < newState.length; column++)
            for (int row = 0; row < newState[0].length; row++)
                assertTrue(
                        newState[column][row] == TileState.NOT_PICKABLE ||
                        newState[column][row] == TileState.PICKABLE && column == 4 && row == 2 ||
                        newState[column][row] == TileState.PICKABLE_NEXT && column == 3 && row == 2
                );
    }

    @Test
    public void pick_pickThreeTimes_noShouldBePickableNext() {
        board.refillBoard(2);
        board.definePickable();

        // We, again, pick a couple of first tiles
        board.pick(5, 1, 2);
        board.pick(4, 1, 2);

        board.definePickable();

        board.pick(5, 2, 2);
        board.pick(4, 2, 2);
        TileState[][] newState = board.getBoardState();
        for (int column = 0; column < newState.length; column++)
            for (int row = 0; row < newState[0].length; row++)
                assertTrue(
                        newState[column][row] == TileState.NOT_PICKABLE ||
                        newState[column][row] == TileState.PICKABLE && column == 3 && row == 2
                );
    }

    @Test
    public void pick_limitWithConstraint_allShouldBeNotPickable() {
        board.refillBoard(2);
        board.definePickable();

        // We make a pick with limiting constraint
        board.pick(5, 1, 0);

        TileState[][] newState = board.getBoardState();
        for (int column = 0; column < newState.length; column++)
            for (int row = 0; row < newState[0].length; row++)
                assertEquals(newState[column][row], TileState.NOT_PICKABLE);
    }

    @Test
    public void pick_pickTwiceLimitWithConstraint_shouldBeNoPickableNext() {
        board.refillBoard(2);
        board.definePickable();

        // We make a pick with limiting constraint
        board.pick(5, 1, 1);
        board.pick(4, 1, 1);
        board.definePickable();

        board.pick(5, 2, 1);

        TileState[][] newState = board.getBoardState();
        for (int column = 0; column < newState.length; column++)
            for (int row = 0; row < newState[0].length; row++)
                assertTrue(
                        newState[column][row] == TileState.NOT_PICKABLE ||
                        newState[column][row] == TileState.PICKABLE && column == 4 && row == 2
                );
    }

    @Test
    public void pick_pickSixTiles_shouldBeTwoPickableNextAndTwoPickable() {
        board.refillBoard(2);
        board.definePickable();

        board.pick(5, 1, 2);
        board.pick(4, 1, 1);
        board.definePickable();

        board.pick(5, 2, 2);
        board.pick(4, 2, 1);
        board.pick(3, 2, 0);
        board.definePickable();

        board.pick(4, 3, 2);
        TileState[][] newState = board.getBoardState();
        for (int column = 0; column < newState.length; column++)
            for (int row = 0; row < newState[0].length; row++)
                assertTrue(
                        newState[column][row] == TileState.NOT_PICKABLE ||
                        newState[column][row] == TileState.PICKABLE && (column == 3 || column == 5) && row == 3 ||
                        newState[column][row] == TileState.PICKABLE_NEXT && (column == 2 || column == 6) && row == 3
                );
    }

    @Test
    public void pick_pickSixTilesWithConstraint_shouldBeTwoPickableAndNoPickableNext() {
        board.refillBoard(2);
        board.definePickable();

        board.pick(5, 1, 2);
        board.pick(4, 1, 1);
        board.definePickable();

        board.pick(5, 2, 2);
        board.pick(4, 2, 1);
        board.pick(3, 2, 0);
        board.definePickable();

        board.pick(4, 3, 1);
        TileState[][] newState = board.getBoardState();
        for (int column = 0; column < newState.length; column++)
            for (int row = 0; row < newState[0].length; row++)
                assertTrue(
                        newState[column][row] == TileState.NOT_PICKABLE ||
                        newState[column][row] == TileState.PICKABLE && (column == 3 || column == 5) && row == 3
                );
    }

    @Test
    public void pick_pickSevenTilesWithConstraint_onlyOneShouldBePickable() {
        board.refillBoard(2);
        board.definePickable();

        board.pick(5, 1, 2);
        board.pick(4, 1, 1);
        board.definePickable();

        board.pick(5, 2, 2);
        board.pick(4, 2, 1);
        board.pick(3, 2, 0);
        board.definePickable();

        board.pick(4, 3, 2);
        board.pick(5, 3, 1);
        TileState[][] newState = board.getBoardState();
        for (int column = 0; column < newState.length; column++)
            for (int row = 0; row < newState[0].length; row++)
                assertTrue(
                        newState[column][row] == TileState.NOT_PICKABLE ||
                                newState[column][row] == TileState.PICKABLE && (column == 6 || column == 3)
                                        && row == 3
                );
    }

    @Test
    public void pick_verticalInlineScenario_distanceTwoTileShouldBePickable() {
        board.refillBoard(2);
        board.definePickable();

        board.pick(3, 7, 2);
        board.pick(3, 6, 2);
        board.definePickable();

        board.pick(3, 5, 2);
        board.definePickable();

        board.pick(4, 6, 2);
        board.pick(4, 7, 2);

        assertEquals(board.getBoardState()[4][5], TileState.PICKABLE);
    }

    // ==================== definePickable ====================
    @Test
    public void definePickable_boardStateDefined_noPickableNext() {
        board.refillBoard(2);
        board.definePickable();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Assert.assertNotEquals(board.getBoardState()[i][j], TileState.PICKABLE_NEXT);
            }
        }
    }

    @Test
    public void definePickable_boardStateDefined_allEmptyCellsAreNotPickable() {
        board.refillBoard(2);
        board.definePickable();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board.getBoardContent()[i][j] == null)
                    assertEquals(board.getBoardState()[i][j], TileState.NOT_PICKABLE);
            }
        }
    }

    @Test
    public void definePickable_boardStateDefined_allPickablesHaveEmptyNeighbor() {
        board.refillBoard(2);
        board.definePickable();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board.getBoardState()[i][j] == TileState.PICKABLE) {
                    // check if there is an empty neighbor
                    boolean emptyNeighbor = false;
                    if (i > 0 && board.getBoardContent()[i - 1][j] == null)
                        emptyNeighbor = true;
                    if (i < 8 && board.getBoardContent()[i + 1][j] == null)
                        emptyNeighbor = true;
                    if (j > 0 && board.getBoardContent()[i][j - 1] == null)
                        emptyNeighbor = true;
                    if (j < 8 && board.getBoardContent()[i][j + 1] == null)
                        emptyNeighbor = true;

                    Assert.assertTrue(emptyNeighbor);
                }
            }
        }
    }

    @Test
    public void definePickable_boardStateDefined_allNotPickablesHaveSetNeighbors() {
        board.refillBoard(2);
        board.definePickable();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board.getBoardState()[i][j] == TileState.NOT_PICKABLE && board.getBoardContent()[i][j] != null) {
                    // check if there is a set neighbor
                    boolean emptyNeighbor = false;
                    if (i > 0 && board.getBoardContent()[i - 1][j] == null)
                        emptyNeighbor = true;
                    if (i < 8 && board.getBoardContent()[i + 1][j] == null)
                        emptyNeighbor = true;
                    if (j > 0 && board.getBoardContent()[i][j - 1] == null)
                        emptyNeighbor = true;
                    if (j < 8 && board.getBoardContent()[i][j + 1] == null)
                        emptyNeighbor = true;

                    Assert.assertFalse(emptyNeighbor);
                }
            }
        }
    }

    // ==================== isTileUsable ====================
    @Test
    public void isTileUsable_tileNeverUsable_shouldReturnFalse() {
        // We check some cells which certainly are not to be used,
        // no matter how many players
        assertFalse(Board.isTileUsable(0, 0, 2));
        assertFalse(Board.isTileUsable(0, 0, 3));
        assertFalse(Board.isTileUsable(0, 0, 4));
        assertFalse(Board.isTileUsable(2, 8, 2));
        assertFalse(Board.isTileUsable(6, 1, 3));
        assertFalse(Board.isTileUsable(7, 7, 4));
    }

    @Test
    public void isTileUsable_tileNotUsableTooFewPlayers_shouldReturnFalse() {
        // We check a tiles which require more players than the ones we pass
        assertFalse(Board.isTileUsable(0, 5, 2));
        assertFalse(Board.isTileUsable(0, 5, 3));
    }

    @Test
    public void isTileUsable_tileUsableEnoughPlayers_shouldReturnTrue() {
        // We test the three cases of 2, 3, 4 players and check the
        assertTrue(Board.isTileUsable(1, 3, 2));
        assertTrue(Board.isTileUsable(1, 3, 3));
        assertTrue(Board.isTileUsable(1, 3, 4));
    }
}
