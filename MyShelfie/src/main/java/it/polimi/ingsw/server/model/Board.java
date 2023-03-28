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
     * 2D array with the state of each cell on the board, where:
     * - 0: cell cannot be picked
     * - 1: cell can be picked immediately
     * - 2: cell could be picked on subsequent picks
     */
    private int[][] boardState = new int[9][9];

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
     * which cells can be picked
     */
    public void definePickable() {
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
