package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.TileState;
import it.polimi.ingsw.common.TileType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * Representation of the game board, one for each game.
 * Also keeps track of the tiles in the bag for statistical accuracy.
 * 
 * @author Morganti Tommaso
 * @author Ferrarini Andrea
 */
public class Board implements Serializable {
    @Serial
    public static final long serialVersionUID = 1L;

    /**
     * This is a helper attribute used to map how many players are required
     * for each cell of the board to be usable during a game. A '0' in a cell
     * means the cell is never to be used, while any number greater than 0
     * is indicative of the exact minimum number of players required.
     */
    private static final int[][] usableTiles = {
            {0, 0, 0, 3, 4, 0, 0, 0, 0},
            {0, 0, 0, 2, 2, 4, 0, 0, 0},
            {0, 0, 3, 2, 2, 2, 3, 0, 0},
            {0, 4, 2, 2, 2, 2, 2, 2, 3},
            {4, 2, 2, 2, 2, 2, 2, 2, 4},
            {3, 2, 2, 2, 2, 2, 2, 4, 0},
            {0, 0, 3, 2, 2, 2, 3, 0, 0},
            {0, 0, 0, 4, 2, 2, 0, 0, 0},
            {0, 0, 0, 0, 4, 3, 0, 0, 0}
    };

    /**
     * Map with the number of tiles of each type in the bag
     */
    private Map<TileType, Integer> tilesInBag;

    /**
     * 2D array with the type of each tile on the board
     */
    private TileType[][] boardContent = new TileType[9][9];

    /**
     * 2D array with the state of each cell on the board, the meaning of which is
     * documented at {@link TileState}
     */
    private TileState[][] boardState = new TileState[9][9];

    /**
     * Initializes the board with 22 tiles of each type in its bag
     */
    public Board() {
        this.tilesInBag = Map.ofEntries(
                Map.entry(TileType.BOOK, 22),
                Map.entry(TileType.CAT, 22),
                Map.entry(TileType.FRAME, 22),
                Map.entry(TileType.PLANT, 22),
                Map.entry(TileType.TOY, 22),
                Map.entry(TileType.TROPHY, 22));
    }

    /**
     * Refills the board with tiles from the bag when there are not enough on the
     * board
     */
    public void refillBoard() {
    }

    /**
     * Picks a tile from the board, updates the game state accordingly and returns
     * the tile type
     * 
     * @param x          x coordinate of the tile
     * @param y          y coordinate of the tile
     * @param constraint maximum number of tiles that can be picked (based on free
     *                   cells in a player's library)
     * @return the type of the picked tile to be put in the player's pick buffer
     */
    public TileType pick(int x, int y, int constraint) {
        // We simply iterate over the board...
        for (int column=0; column < boardState.length; column++) {
            for (int row=0; row < boardState[0].length; row++) {
                if (constraint == 0) {
                    // If, at current time, the player is limited to zero more picks, we simply
                    // zero-out the entire boardState matrix
                    boardState[column][row] = TileState.NOT_PICKABLE;
                } else if (boardState[column][row] == TileState.PICKABLE) {
                    // If the player can actually make some picks (constraint > 1), we check only
                    // those cells which were already pick-able before (=they already had a free edge)
                    if (((column == x && (row == x-2 || row == x+2)) || (row == y && (column == y-2 || column == y+2)))
                            && constraint > 1) {
                        // If constraint is even greater than 1, and the analysed cell is inline with the
                        // picked one, we set the cell-state to PICKABLE_NEXT (= can be picked on subsequent pick)
                        boardState[column][row] = TileState.PICKABLE_NEXT;
                    } else if ( !(
                            (column == x && (row == x-1 || row == x+1)) ||
                            (row == y && (column == y-1 || column == y+1)))
                                ) {
                        // Any other cell (not inline with the current one, or which are "too far",
                        // are not pick-able even in future picks
                        boardState[column][row] = TileState.NOT_PICKABLE;
                    }
                } else if (boardState[column][row] == TileState.PICKABLE_NEXT) {
                    // If a cell had state equal to 2 (and constraint != 0), then now,
                    // if it is adjacent to the last picked cell, it will change state to 1
                    if (column == x-1 || column == x+1 || row == y-1 || row == y+1)
                        boardState[column][row] = TileState.PICKABLE;
                    else
                        boardState[column][row] = TileState.NOT_PICKABLE;
                }
            }
        }

        // Removing picked tile and returning it to caller
        TileType picked_tile = this.boardContent[x][y];
        this.boardContent[x][y] = null;
        this.boardState[x][y] = TileState.NOT_PICKABLE;
        return picked_tile;
    }

    /**
     * At the beginning of a player's turn, the board state is updated to reflect
     * which cells can be picked.
     * Info on the game state is documented at {@link TileState}.
     * 
     * This method defines which cells are considered pickable at the beginning of a
     * player's turn. Cells can either be pickable or not-pickable, PICKABLE_NEXT is
     * set by the pick method, after a player picks his first tile.
     */
    public void definePickable() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (boardContent[i][j] == null) {
                    // if the board content is null, the state should be not-pickable
                    boardState[i][j] = TileState.NOT_PICKABLE;
                } else if (i == 0 || i == 8 || j == 0 || j == 8) {
                    // if it is a non-null border cell, it is always pickable
                    boardState[i][j] = TileState.PICKABLE;

                } else if (boardContent[i - 1][j] == null
                        || boardContent[i + 1][j] == null
                        || boardContent[i][j - 1] == null
                        || boardContent[i][j + 1] == null) {
                    // if at least one of adiacent cells is null, the state should
                    // be pickable
                    boardState[i][j] = TileState.PICKABLE;
                } else {
                    boardState[i][j] = TileState.NOT_PICKABLE;
                }
            }
        }
    }

    /**
     * Checks if a specific coordinate is a valid tile on the board for a specific
     * number of players
     * 
     * @param x          x coordinate of the tile
     * @param y          y coordinate of the tile
     * @param numPlayers number of players in the game
     * @return true if the tile is valid, false otherwise
     */
    public static boolean isTileUsable(int x, int y, int numPlayers) {
        return usableTiles[x][y] <= numPlayers && usableTiles[x][y] != 0;
    }
}
