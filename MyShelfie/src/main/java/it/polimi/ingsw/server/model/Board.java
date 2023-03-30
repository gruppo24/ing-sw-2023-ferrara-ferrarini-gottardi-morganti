package it.polimi.ingsw.server.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * Representation of the game board, one for each game.
 * Also keeps track of the tiles in the bag for statistical accuracy.
 * 
 * @author Morganti Tommaso
 */
public class Board implements Serializable {
    @Serial
    public static final long serialVersionUID = 1L;

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
        return null;
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
     * @param numplayers number of players in the game
     * @return true if the tile is valid, false otherwise
     */
    public static boolean isTileUsable(int x, int y, int numplayers) {
        return false;
    }
}
