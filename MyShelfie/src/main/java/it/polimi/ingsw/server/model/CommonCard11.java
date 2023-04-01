package it.polimi.ingsw.server.model;

/**
 * This class defines if the player has achieved or not common-objective 11
 * @author Ferrara Silvia
 */
public class CommonCard11 extends CommonCard {

    /**
     * class constructor
     * @param identifier unique identifier associated to the card. Required to find asset location on client
     * @param description textual description of the common objective
     */
    public CommonCard11(String identifier, String description) {
        super(identifier, description);
    }

    /**
     * Method that checks if there are 5 tiles forming a diagonal
     * @param library library to check for objective matching
     * @return boolean that represents the achievement of the common objective
     */
    @Override
    public boolean checkObjective(TileType[][] library) {
        //the player achieve the common-objective if he has a right diagonal config. or a left diagonal config.
        return (diagonal1(library) || diagonal2(library));
    }

    private boolean diagonal1(TileType[][] library) {
        //library dimension
        final int WIDTH = library.length;
        //we initialize the row counter
        int row = 0;
        //we save the tile in the left bottom corner
        TileType cell = library[0][0];

        //we iterate through the matrix checking only the cells in the right diagonal
        for(int column = 0; column < WIDTH; column++){
            //we check the equality between the tile in the left bottom corner and the others in the diagonal
            if(library[column][row] != null && library[column][row] == cell){
                row++;
            } else
                return false;
        }
        return true;
    }

    private boolean diagonal2(TileType[][] library) {
        //library dimension
        final int WIDTH = library.length;
        //we initialize the row counter
        int row = 0;
        //we save the tile in the right bottom corner
        TileType cell = library[WIDTH-1][0];

        //we iterate through the matrix checking only the cells in the left diagonal
        for(int column = WIDTH-1; column >= 0; column--){
            //we check the equality between the tile in the right bottom corner and the others in the diagonal
            if(library[column][row] != null && library[column][row] == cell){
                row++;
            } else
                return false;
        }
        return true;
    }
}
