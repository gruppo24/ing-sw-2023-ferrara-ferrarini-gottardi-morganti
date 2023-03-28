package it.polimi.ingsw.server.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Ferrarini Andrea
 * Class representing a player in a game
 */
public class Player implements Serializable {

    // Attribute required for serialization
    @Serial
    private static final long serialVersionUID = 1L;

    // Game interaction attributes
    public final String nickname;
    private TileType[][] library = new TileType[5][6];
    private PrivateCard privateCard;

    // Point related attribute: we compute them and store them here for faster retrieval
    private int privatePoints = 0;
    private int clusterPoints = 0;
    private int[] commonsOrder = {0, 0};
    private boolean firstFilled = false;

    // Game action attributes
    private int selectedColumn;
    private TileType[] selectionBuffer;


    /**
     * Class constructor
     * @param nickname username to be associated to this user
     */
    public Player(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Method in charge of updating the privatePoints attribute by checking how many private
     * objectives the player has completed and mapping them to an amount of points
     */
    public void updatePrivatePoints() {

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
