package it.polimi.ingsw.server.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.exceptions.AlreadyUsedIndex;
import it.polimi.ingsw.server.exceptions.InvalidReorderingIndices;
import it.polimi.ingsw.server.exceptions.SelectionBufferFullException;

import static java.lang.Math.min;

/**
 * Class representing a player in a game
 * @author Ferrarini Andrea
 */
public class Player implements Serializable {

    // Attribute required for serialization
    @Serial
    private static final long serialVersionUID = 1L;

    // Game interaction attributes
    public final String nickname;
    private final TileType[][] library = new TileType[5][6];
    private PrivateCard privateCard;

    // Point related attribute: we compute them and store them here for faster retrieval
    private int privatePoints = 0;
    private int clusterPoints = 0;
    protected int[] commonsOrder = {0, 0};  // OK to read-write within same package
    private boolean firstFilled = false;

    // Game action attributes
    private int selectedColumn;
    private TileType[] selectionBuffer;
    private int bufferTop = 0;


    /**
     * Class constructor
     *
     * @param nickname username to be associated to this user
     */
    public Player(String nickname) {
        this.nickname = nickname;
    }

    public void setPrivateCard(PrivateCard privateCard) {
        this.privateCard = privateCard;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof Player)
            return this.nickname.equals(((Player) other).nickname);
        return false;
    }

    /**
     * Method in charge of updating the privatePoints attribute by checking how many private
     * objectives the player has completed and mapping them to an amount of points
     */
    public void updatePrivatePoints() {
        // We iterate over all private objectives and check how many matches we have in our library
        int matches = 0;
        for (TileType tile : privateCard.objectives.keySet())
            if (this.library[privateCard.objectives.get(tile)[0]][privateCard.objectives.get(tile)[1]] == tile)
                matches++;

        this.privatePoints = PrivateCard.mapPrivatePoints(matches);
    }

    /**
     * getter method for privatePoints attribute
     * @return private points achieved by player
     */
    public int getPrivatePoints() {
        return this.privatePoints;
    }

    /**
     * Method in charge of updating the clusterPoints attribute by analysing the player's
     * library, finding all clusters and mapping them to an amount of points
     */
    public void updateClusterPoints() {
        // Prefetching (for future use) width and height of the library:
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;

        // Creating a matrix which will mirror the player's library. In it, we flag which
        // tiles have already been considered in clusters
        boolean[][] used = new boolean[WIDTH][HEIGHT];

        // Following map will assign to each accepted cluster size (key) the number of
        // clusters found (value)
        HashMap<Integer, Integer> found = new HashMap<>();
        found.put(3, 0);
        found.put(4, 0);
        found.put(5, 0);
        found.put(6, 0);

        // At this point, we iterate through the entire matrix...
        for (int column=0; column < WIDTH; column++) {
            for (int row=0; row < HEIGHT; row++) {
                // ...for each cell, we check whether it contains a tile
                // and hasn't already been used to build another cluster
                if (!used[column][row] && this.library[column][row] != null) {
                    int size = walkMatrix(column, row, used, WIDTH, HEIGHT);

                    // After having computed a cluster size, we update our map appropriately
                    if (size >= 6)
                        found.put(6, found.get(6)+1);
                    else if (size > 2)
                        found.put(size, found.get(size)+1);
                }
            }
        }

        // Finally, we add up all points achieved
        this.clusterPoints = found.get(3)*2 + found.get(4)*3 + found.get(5)*5 + found.get(6)*8;
    }

    /**
     * getter method for clusterPoints attribute
     * @return cluster points achieved by player
     */
    public int getClusterPoints() {
        return this.clusterPoints;
    }

    /**
     * Method in charge of checking if the player has filled in their
     * library entirely or not
     * @return whether the player has filled the library entirely
     */
    public boolean checkIfFilled() {
        for (TileType[] libraryColumn : this.library)
            for (TileType tile : libraryColumn)
                if (tile == null)
                    return false;  // NOT filled cell found...

        // If we fall through the entire loop, we return true
        this.firstFilled = true;
        return true;
    }

    /**
     * This method is in charge of returning a deep-copy of the player's library
     * @return a deep-copy of the player's library
     */
    public TileType[][] getLibrary() {
        TileType[][] lib_copy = new TileType[5][6];
        for (int column=0; column < library.length; column++)
            for (int row = 0; row < library[0].length; row++)
                lib_copy[column][row] = this.library[column][row];

        return lib_copy;
    }

    /**
     * This function is in charge of assigning a value to the selectedColumn attribute and allocating a selection
     * buffer of the appropriate size (maximum size is constrained to 3)
     * @param column desired column
     * @throws IndexOutOfBoundsException when the selected column exceeds the library's width
     */
    public void selectColumn(int column) throws IndexOutOfBoundsException {
        if (column >= this.library.length)
            throw new IndexOutOfBoundsException("ERROR: libraries have " + this.library.length + " columns");
        this.selectedColumn = column;

        // Finding the first available row for tile insertion
        int row;
        for (row=0; row < this.library[0].length; row++)
            if (this.library[column][row] == null)
                break;

        // Allocating selection buffer space
        int cellsAvailable = this.library[0].length - row;
        if (cellsAvailable > 0)
            this.selectionBuffer = new TileType[min(cellsAvailable, 3)];
        else
            this.selectionBuffer = null;
        this.bufferTop = 0;
    }

    /**
     * getter function of the selectedColumn attribute
     * @return selectedColumn attribute value
     */
    public int getSelectedColumn() {
        return this.selectedColumn;
    }

    /**
     * This function simply returns the number of available slots in the current selectionBuffer
     * @return space remaining in current selection buffer
     */
    public int getSelectionBufferSize() {
        // If buffer is uninitialized or flushed, we return 0
        if (this.selectionBuffer == null) return 0;
        return this.selectionBuffer.length - this.bufferTop;
    }

    /**
     * Function in charge of pushing a new tile in the current selection buffer
     * @param tile tile to be pushed in the selection buffer
     * @return number of available slots in the selection buffer (after having pushed)
     * @throws SelectionBufferFullException when a push is attempted and the buffer has already been filled
     */
    public int pushTileToSelectionBuffer(TileType tile) throws SelectionBufferFullException {
        if (this.bufferTop == this.selectionBuffer.length)
            throw new SelectionBufferFullException(this.selectedColumn);

        // If there still is space, we push the provided tile and increment the buffer pointer
        this.selectionBuffer[this.bufferTop] = tile;
        this.bufferTop++;

        // Finally, we return the remaining space inside the selection buffer
        return getSelectionBufferSize();
    }

    /**
     * Function in charge of returning a deep-copy of the current selection buffer
     * @return a deep-copy of the current selection buffer
     */
    public TileType[] getSelectionBufferCopy() {
        // If buffer is uninitialized or flushed, simply return null
        if (this.selectionBuffer == null)
            return null;

        TileType[] deep_copy = new TileType[this.selectionBuffer.length];
        for (int i=0; i < this.selectionBuffer.length; i++)
            deep_copy[i] = this.selectionBuffer[i];

        return deep_copy;
    }

    /**
     * Function in charge of reordering the current selection buffer
     * @param firstIndex index (inside the current selection buffer) of the NEW first tile
     * @param secondIndex index (inside the current selection buffer) of the NEW second tile
     * @param thirdIndex index (inside the current selection buffer) of the NEW third tile
     * @throws AlreadyUsedIndex when the same index is provided more than once
     * @throws InvalidReorderingIndices when the index of an unused cell (no tile pushed into
     *                                  it) is provided for reordering
     */
    public void reorderSelectionBuffer(int firstIndex, int secondIndex, int thirdIndex)
            throws AlreadyUsedIndex, InvalidReorderingIndices {
        // Checking if the indices are all different
        if (firstIndex == secondIndex) throw new AlreadyUsedIndex(1);
        if (secondIndex == thirdIndex) throw new AlreadyUsedIndex(2);
        if (firstIndex == thirdIndex) throw new AlreadyUsedIndex(2);

        // Checking that all unused cells are untouched during reordering
        if (this.selectionBuffer[0] == null && firstIndex != 0) throw new InvalidReorderingIndices(0);
        if (this.selectionBuffer[1] == null && secondIndex != 1) throw new InvalidReorderingIndices(1);
        if (this.selectionBuffer[2] == null && thirdIndex != 2) throw new InvalidReorderingIndices(2);

        // Making a temp copy of the current buffer
        TileType[] tmp = {
                this.selectionBuffer[0],
                this.selectionBuffer[1],
                this.selectionBuffer[2]
        };

        // Reordering the selection buffer
        this.selectionBuffer[0] = tmp[firstIndex];
        this.selectionBuffer[1] = tmp[secondIndex];
        this.selectionBuffer[2] = tmp[thirdIndex];
    }

    /**
     * Function in charge of moving the tiles of the current selection buffer into the selected column. After having
     * done so, current selection buffer is automatically flushed (set to null)
     * NOTE: the tiles are moved to the column with a "gravity" logic starting from index=0. That
     * is to say, going from index = 0 to index = this.bufferTop-1 the buffered tiles are pushed
     * from the bottom, going up
     * @throws NullPointerException if a flushed / null buffer is pushed
     */
    public void flushBufferIntoLibrary() throws NullPointerException {
        if (this.selectionBuffer == null)
            throw new NullPointerException("ERROR: current selection buffer hasn't been initialized!");

        // Pushing tiles with "gravity" logic into the library
        int row, buffer_pointer;
        for (row=0, buffer_pointer=0; row < this.library[0].length && buffer_pointer < this.bufferTop; row++) {
            if (this.library[selectedColumn][row] == null) {
                this.library[selectedColumn][row] = this.selectionBuffer[buffer_pointer];
                buffer_pointer++;
            }
        }

        // Flushing buffer
        this.selectionBuffer = null;
        this.bufferTop = 0;
    }

    /**
     * This helper function recursively finds a cluster (of ANY size) around a given tile
     * @param x x coordinate of the selected tile
     * @param y y coordinate of the selected tile
     * @param used matrix containing a map of already used tiles in other clusters
     * @param WIDTH width of the matrix
     * @param HEIGHT height of the matrix
     * @return size of cluster around the given tile
     */
    private int walkMatrix(int x, int y, boolean[][] used, int WIDTH, int HEIGHT) {
        // Setting current cluster size to 1 (only one tile currently analysed)
        // and setting current tile as used...
        int size = 1;
        used[x][y] = true;

        // Subsequently, we check for possible tile matches above current tile
        if (x > 0 && this.library[x-1][y] != null && !used[x-1][y]
                && this.library[x-1][y] == this.library[x][y])
            size += walkMatrix(x-1, y, used, WIDTH, HEIGHT);

        // Subsequently, we check for possible tile matches below current tile
        if (x < WIDTH-1 && this.library[x+1][y] != null && !used[x+1][y]
                && this.library[x+1][y] == this.library[x][y])
            size += walkMatrix(x+1, y, used, WIDTH, HEIGHT);

        // Subsequently, we check for possible tile matches to the right of the current tile
        if (y > 0 && this.library[x][y-1] != null && !used[x][y-1]
                && this.library[x][y-1] == this.library[x][y])
            size += walkMatrix(x, y-1, used, WIDTH, HEIGHT);

        // Subsequently, we check for possible tile matches to the left of the current tile
        if (y < HEIGHT-1 && this.library[x][y+1] != null && !used[x][y+1]
                && this.library[x][y+1] == this.library[x][y])
            size += walkMatrix(x, y+1, used, WIDTH, HEIGHT);

        // Finally, we return the final computed size
        return size;
    }

}
