package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.TileState;
import it.polimi.ingsw.common.TileType;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

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
            { 0, 0, 0, 3, 4, 0, 0, 0, 0 },
            { 0, 0, 0, 2, 2, 4, 0, 0, 0 },
            { 0, 0, 3, 2, 2, 2, 3, 0, 0 },
            { 0, 4, 2, 2, 2, 2, 2, 2, 3 },
            { 4, 2, 2, 2, 2, 2, 2, 2, 4 },
            { 3, 2, 2, 2, 2, 2, 2, 4, 0 },
            { 0, 0, 3, 2, 2, 2, 3, 0, 0 },
            { 0, 0, 0, 4, 2, 2, 0, 0, 0 },
            { 0, 0, 0, 0, 4, 3, 0, 0, 0 }
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
     * This attribute keeps track of the coordinates of the tiles picked by a player during their turn
     */
    private List<int[]> picked = new LinkedList<>();;

    public TileType[][] getBoardContent() {
        // Computing deep-copy of the board content matrix
        TileType[][] deepCopy = new TileType[boardContent.length][boardContent[0].length];
        for (int i=0; i < deepCopy.length; i++)
            for (int j=0; j < deepCopy[0].length; j++)
                deepCopy[i][j] = this.boardContent[i][j];
        return deepCopy;
    }

    public TileState[][] getBoardState() {
        // Computing deep-copy of the board state matrix
        TileState[][] deepCopy = new TileState[boardState.length][boardState[0].length];
        for (int i=0; i < deepCopy.length; i++)
            for (int j=0; j < deepCopy[0].length; j++)
                deepCopy[i][j] = this.boardState[i][j];
        return deepCopy;
    }

    /**
     * Initializes the board with 22 tiles of each type in its bag
     */
    public Board() {
        // initialize the bag with 22 tiles of each type
        // has to be a hashmap because ofEntries returns an immutable map
        this.tilesInBag = new HashMap<>(Map.ofEntries(
                Map.entry(TileType.BOOK, 22),
                Map.entry(TileType.CAT, 22),
                Map.entry(TileType.FRAME, 22),
                Map.entry(TileType.PLANT, 22),
                Map.entry(TileType.TOY, 22),
                Map.entry(TileType.TROPHY, 22)));
    }

    /**
     * Picks a random tile from the bag and returns it. If the bag is empty, the
     * method returns null.
     * 
     * @return the type of the picked tile, or null if the bag is empty
     */
    private TileType drawRandomTile() {
        // this method counts all the tiles, and pick a random number between 0 and
        // the total number of tiles.
        // The random number generated is a tile as if all the tiles were in a single
        // array, ordered by type.

        // count the total number of tiles in the bag
        int totalTiles = 0;
        for (TileType type : this.tilesInBag.keySet())
            totalTiles += this.tilesInBag.get(type);

        // if the bag is empty, return null
        if (totalTiles == 0)
            return null;

        // pick a random tile
        int randomTileNum = (new Random()).nextInt(0, totalTiles);

        // for each type, if the random number is smaller than the number of tiles
        // of that type, return that type (as if the tile was picked in that position
        // of an ordered list). Otherwise subtract the number of tiles of that type
        // and continue with the next type.
        for (TileType type : this.tilesInBag.keySet()) {
            if (randomTileNum < this.tilesInBag.get(type)) {
                this.tilesInBag.put(type, this.tilesInBag.get(type) - 1);
                return type;
            }
            randomTileNum -= this.tilesInBag.get(type);
        }

        // this point should never be reached, generated number greater than total
        // number of tiles ??
        return null;
    }

    /**
     * Helper method which checks whether a pair of coordinates if contained in the picked attribute
     * @param test coordinates to test (as an array of int of length 2)
     * @return whether the provided coordinates are already present in the picked linked-list
     */
    private boolean pickedContains(int[] test) {
        for (int [] coords : this.picked)
            if (coords[0] == test[0] && coords[1] == test[1])
                return true;
        return false;
    }

    /**
     * method that checks at the end of the current player turn if there are "legal"
     * available moves for the next player
     * 
     * @return a boolean representing whether the board should be refilled or not
     */
    public boolean shouldBeRefilled() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (boardContent[i][j] != null) {
                    // if there is at leas one not null neighbour, the board should not be refilled
                    if (i > 0 && boardContent[i - 1][j] != null) {
                        return false;
                    }
                    if (i < 8 && boardContent[i + 1][j] != null) {
                        return false;
                    }
                    if (j > 0 && boardContent[i][j - 1] != null) {
                        return false;
                    }
                    if (j < 8 && boardContent[i][j + 1] != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Refills the board with tiles from the bag when there are not enough on the
     * board
     */
    public void refillBoard(int numplayers) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (this.boardContent[i][j] == null && isTileUsable(i, j, numplayers)) {
                    // if the board content is null on a usable tile, the cell is
                    // empty and can be refilled
                    TileType newTile = drawRandomTile();
                    if (newTile == null) // if the bag is empty, no more tiles to add
                        return;

                    this.boardContent[i][j] = newTile;
                }
            }
        }
    }

    /**
     * Picks a tile from the board, updates the game state accordingly and returns
     * the tile type
     * 
     * @param x          x coordinate of the tile
     * @param y          y coordinate of the tile
     * @param constraint maximum number of tiles that can be picked (based on free
     *                   cells in a player's library)
     * @return the type of the picked tile to be put in the player's pick buffer, null if the tile isn't pick-able
     */
    public TileType pick(int x, int y, int constraint) {
        // If indices provided are out of range, we return null immediately
        if (x >= this.boardContent.length || y >= this.boardContent[0].length || x < 0 || y < 0)
            return null;

        // If the selected tile isn't pick-able, we return null immediately
        if (this.boardState[x][y] != TileState.PICKABLE)
            return null;

        // We add these coordinates to the picked cache
        this.picked.add(new int[]{x, y});

        // Otherwise,
        TileState[][] nextBoardState = new TileState[this.boardState.length][this.boardState[0].length];

        // By default, after a pick we consider all tiles not pick-able
        for (int row=0; row < nextBoardState.length; row++)
            for (int column=0; column < nextBoardState[row].length; column++)
                nextBoardState[row][column] = TileState.NOT_PICKABLE;

        // Now, we move in a line upward, rightward, downward and leftward from the picked tile,
        // and check, depending on the constraint argument, values of (x, y) and whether tiles
        // are present or not, which ones are still pick-able, which are pick-able next
        int increment = 0;
        for (int up=1; up <= constraint + increment; up++) {
            if (y + up >= nextBoardState[0].length ||
                    ((this.boardContent[x][y+up] == null || this.boardState[x][y+up] == TileState.NOT_PICKABLE) &&
                            !this.pickedContains(new int[]{x, y+up})))
                            break;

            // Tiles which have been picked are not to be counted while moving inline
            if (!this.pickedContains(new int[]{x, y+up}))
                nextBoardState[x][y+up] = up - increment == 1 ? TileState.PICKABLE : TileState.PICKABLE_NEXT;
            else
                increment++;
        }

        increment = 0;
        for (int right=1; right <= constraint + increment; right++) {
            if (x + right >= nextBoardState[0].length ||
                    ((this.boardContent[x+right][y] == null || this.boardState[x+right][y] == TileState.NOT_PICKABLE) &&
                            !this.pickedContains(new int[]{x+right, y})))
                break;

            // Tiles which have been picked are not to be counted while moving inline
            if (!this.pickedContains(new int[]{x+right, y}))
                nextBoardState[x+right][y] = right - increment == 1 ? TileState.PICKABLE : TileState.PICKABLE_NEXT;
            else
                increment++;
        }

        increment = 0;
        for (int down=1; down <= constraint + increment; down++) {
            if (y - down < 0 ||
                    ((this.boardContent[x][y-down] == null || this.boardState[x][y-down] == TileState.NOT_PICKABLE) &&
                            !this.pickedContains(new int[]{x, y-down})))
                break;

            // Tiles which have been picked are not to be counted while moving inline
            if (!this.pickedContains(new int[]{x, y-down}))
                nextBoardState[x][y-down] = down - increment == 1 ? TileState.PICKABLE : TileState.PICKABLE_NEXT;
            else
                increment++;
        }

        increment = 0;
        for (int left=1; left <= constraint + increment; left++) {
            if (y - left < 0 ||
                    ((this.boardContent[x-left][y] == null || this.boardState[x-left][y] == TileState.NOT_PICKABLE) &&
                            !this.pickedContains(new int[]{x-left, y})))
                break;

            // Tiles which have been picked are not to be counted while moving inline
            if (!this.pickedContains(new int[]{x-left, y}))
                nextBoardState[x-left][y] = left - increment == 1 ? TileState.PICKABLE : TileState.PICKABLE_NEXT;
            else
                increment++;
        }

        // Removing picked tile and returning it to caller
        TileType picked_tile = this.boardContent[x][y];
        this.boardContent[x][y] = null;
        this.boardState = nextBoardState;  //this.boardState[x][y] = TileState.NOT_PICKABLE;
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
        // When this method is called, the picked tiles cache should be reset
        this.picked = new LinkedList<>();

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
