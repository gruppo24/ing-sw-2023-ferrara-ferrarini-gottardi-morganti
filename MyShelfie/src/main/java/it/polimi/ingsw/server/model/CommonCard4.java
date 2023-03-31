package it.polimi.ingsw.server.model;
/**
 * This class check if the player has achieved or not common objective 4
 */
public class CommonCard4 extends CommonCard{
    /**
     * Common class constructor
     *
     * @param identifier unique identifier associated to the card. Required to find asset location on client
     * @param description textual description of the common objective
     */
    public CommonCard4(String identifier, String description) {
        super(identifier, description);
    }

    /**
     *Method that checks if there are six groups each containing at least 2 tiles of the same type
     * @param library library to check for objective matching
     * @return boolean that represents the achievement of the objective
     */
    @Override
    public boolean checkObjective(TileType[][] library) {
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;

        //Matrix of boolean to check if a cell is already in a group
        boolean[][] used = new boolean[WIDTH][HEIGHT];

        //Attribute to count the number of groups found
        int numGroups = 0;

        //Attributes to count the horizontal and vertical size of equals type of tiles
        int sizeUp = 0;
        int sizeBeside = 0;

        //Iteration thought the matrix
        for(int column = 0; column < WIDTH ; column++){
            for(int row = 0; row < HEIGHT ; row++){
                //For Each cell if it isn't in a group we check the "horizontal" and "vertical" size
                if(library[column][row] != null && !used[column][row]) {
                    sizeUp = GoUp(column, row, used, WIDTH, HEIGHT, library);
                    sizeBeside = GoBeside(column, row, used, WIDTH, HEIGHT, library);
                }
                //If the UpSize is exactly 2 AND BesidesSize exactly 1 (Vertical combination)
                // OR vice-versa (Horizontal combination) --> The group is ok!!
                if((sizeUp == 2 && sizeBeside == 1) || (sizeUp == 1 && sizeBeside == 2))
                    numGroups++;
            }
        }
        //Return true if there are four "ok groups"
        return numGroups == 6;
    }
    /**
     * This helper method count recursively the number of equals verticals tiles
     * @param c column coordinate of the tile
     * @param r row coordinate of the tile
     * @param used the matrix of the already used tiles
     * @param WIDTH width dimension
     * @param HEIGHT height dimension
     * @param lib the library to check
     * @return vertical size of equals tiles
     */
    private int GoUp(int c, int r, boolean[][] used, int WIDTH, int HEIGHT, TileType[][] lib){
        //Set the vertical group size to 1 (one current tile) and the tile as used
       int size = 1;
       used[c][r] = true;
        //if there is an "Upper equal tile" and THERE ISN'T an equal "Beside equal tile"--> GoUP
       if(r < HEIGHT - 1 && c < WIDTH - 1 && lib[c][r] == lib[c][r + 1] && lib[c + 1][r] != lib[c][r]) {
           size += GoUp(c, r + 1, used, WIDTH, HEIGHT, lib);
       //We need an "else if" to check the last column (because it hasn't the "beside tile")
       }else if (r < HEIGHT - 1 && lib[c][r] == lib[c][r + 1] && c == WIDTH - 1)
           size += GoUp(c, r + 1, used, WIDTH, HEIGHT, lib);
       return size;
    }
    /**
     * This helper method count recursively the number of equals verticals cells
     * @param c column coordinate of the tile
     * @param r row coordinate of the tile
     * @param used the matrix of the already used tiles
     * @param WIDTH width dimension
     * @param HEIGHT height dimension
     * @param lib the library to check
     * @return vertical size of equals tiles
     */
    private int GoBeside(int c, int r, boolean[][] used, int WIDTH, int HEIGHT, TileType[][] lib){
        //Set the horizontal group size to 1 (one current tile) and the tile as used
        int size = 1;
        used[c][r] = true;
        //if there is a "Beside equal tile" and THERE ISN'T an equal "Upper equal tile"--> GoBeside
        if(c < WIDTH - 1 && r < HEIGHT - 1 && lib[c][r] == lib[c + 1][r] && lib[c][r] != lib[c][r + 1]) {
            size += GoBeside(c + 1, r, used, WIDTH, HEIGHT, lib);
        //We need an "else if" to check the last row (because it hasn't the "Upper tile")
        }else if(c < WIDTH - 1  && lib[c][r] == lib[c + 1][r] && r == HEIGHT - 1)
            size += GoBeside(c + 1,  r, used, WIDTH, HEIGHT, lib);
        return size;
    }

}
