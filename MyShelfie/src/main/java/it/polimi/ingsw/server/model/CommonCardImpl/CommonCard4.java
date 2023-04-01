package it.polimi.ingsw.server.model.CommonCardImpl;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.model.CommonCard;

/**
 * This class check if the player has achieved or not common objective 4
 * @author Gottardi Arianna
 */
public class CommonCard4 extends CommonCard {
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
     * Method that checks if there are six groups each containing at least 2 tiles of the same type
     * @param library library to check for objective matching
     * @return boolean that represents the achievement of the objective
     */
    @Override
    public boolean checkObjective(TileType[][] library) {
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;

        //Matrix of boolean to check if a cell is already in a group
        boolean[][] used = new boolean[WIDTH][HEIGHT];

        //Attribute to count the number of groups containing 2 tiles of the same type ("ok groups")
        int numGroups = 0;

        //Iteration through the matrix (vertically)
        for(int column = 0; column < WIDTH ; column++){
            for(int row = 0; row < HEIGHT - 1; row++){
                //For each cell we check if it could be part of a group-of-two
                //equals to its upper cell + !=null + !used
                if(library[column][row] != null && library[column][row+1] != null
                        && !used[column][row] && !used[column][row+1]
                        && library[column][row] == library[column][row+1]){
                    //If it is ok we increase numGroups and we put the tiles as used
                    numGroups ++;
                    used[column][row] = true;
                    used[column][row+1] = true;
                }
            }
        }
        //We do the same thing horizontally and we check if there are some horizontal group-of-two
        for(int row = 0; row < HEIGHT ; row++){
            for(int column = 0; column < WIDTH - 1; column++){
                if(library[column][row] != null && library[column+1][row] != null
                        && !used[column][row] && !used[column+1][row]
                        && library[column][row] == library[column+1][row]){
                    numGroups ++;
                    used[column][row] = true;
                    used[column +1 ][row] = true;
                }
            }
        }
        //Return true if the number of "ok groups" is >=6
        return numGroups >= 6;
    }
}
