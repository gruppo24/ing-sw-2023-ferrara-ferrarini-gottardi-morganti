package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.TileState;
import it.polimi.ingsw.common.TileType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

public class BoardTest {
    @Before
    public void setUp() {
        /* Do nothing */ }

    @After
    public void tearDown() {
        /* Do nothing */ }

    // ==================== refillBoard ====================
    @Test
    public void refillBoard_newBoard_shouldBeEmpty() {
        Board board = new Board();
        // board should be empty by default
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Assert.assertNull(board.getBoardContent()[i][j]);
            }
        }
    }

    @Test
    public void refillBoard_boardRefilled_shouldNotBeEmpty() {
        Board board = new Board();
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
        Board board = new Board();
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
                    Assert.assertEquals(oldContent[i][j], board.getBoardContent()[i][j]);
                else
                    // the picked cell should at least not be null now
                    Assert.assertNotNull(board.getBoardContent()[i][j]);
            }
        }
    }

    // ==================== pick ====================
    @Test
    public void pick_pickATile_shouldBeEmpty() {
        Board board = new Board();
        board.refillBoard(2);
        board.definePickable();

        board.pick(4, 1, 1);
        Assert.assertNull(board.getBoardContent()[4][1]);
    }

    @Test
    public void pick_pickATile_shouldBeSameType() {
        Board board = new Board();
        board.refillBoard(2);
        board.definePickable();

        TileType shouldBePicked = board.getBoardContent()[4][1];
        TileType isPicked = board.pick(4, 1, 1);
        Assert.assertEquals(isPicked, shouldBePicked);
    }

    @Test
    public void pick_picATile_neighborShouldBePickableNext() {
        Board board = new Board();
        board.refillBoard(2);
        board.definePickable();

        board.pick(4, 1, 2);
        Assert.assertEquals(board.getBoardState()[5][1], TileState.PICKABLE_NEXT);
    }

    // ==================== definePickable ====================
    @Test
    public void definePickable_boardStateDefined_noPickableNext() {
        Board board = new Board();
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
        Board board = new Board();
        board.refillBoard(2);
        board.definePickable();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board.getBoardContent()[i][j] == null)
                    Assert.assertEquals(board.getBoardState()[i][j], TileState.NOT_PICKABLE);
            }
        }
    }

    @Test
    public void definePickable_boardStateDefined_allPickablesHaveEmptyNeighbor() {
        Board board = new Board();
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
        Board board = new Board();
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
