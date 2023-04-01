package it.polimi.ingsw.server.model;
import it.polimi.ingsw.common.TileType;
/**
 * This class check if the player has achieved or not common objective 3
 * @author Gottardi Arianna
 */
public class CommonCard3 extends CommonCard{
    /**
     * Common class constructor
     *
     * @param identifier unique identifier associated to the card. Required to find asset location on client
     * @param description textual description of the common objective
     */
    public CommonCard3(String identifier, String description) {
        super(identifier, description);
    }

    /**
     * Method that checks if there are four groups each containing at least 4 tiles of the same type
     * @param library library to check for objective matching
     * @return boolean that represents the achievement of the objective
     */
    @Override
    public boolean checkObjective(TileType[][] library) {
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;

        //Matrix of boolean to check if a cell is already in a group
        boolean[][] used = new boolean[WIDTH][HEIGHT];

        //Attribute to count the number of groups containing 4 tiles of the same type ("ok groups")
        int numGroups = 0;

        //Boolean to check if the group that we are checking it is an "ok group"
        boolean isOk = true;

        //Iteration through the matrix (vertically)
        for(int column = 0; column < WIDTH ; column++){
            for(int row = 0; row < HEIGHT - 3; row++){
                //For each cell we check if there is a vertical group-of-four
                for(int i = 0;i < 4 && isOk; i++){
                    //If it isn't an "ok group" we stop the iteration
                    //"no ok" means null cell or not equals cell
                    if(library[column][row] == null || library[column][row+i] == null
                            || used[column][row] || used[column][row+i]
                            || library[column][row] != library[column][row+i])
                        isOk = false;
                }
                if(isOk){
                    //If the group is ok we increase numGroup and we put the cells as used
                    numGroups ++;
                    for(int i = 0; i < 4; i++)
                        used[column][row+i] = true;
                }else
                    //If isOk is false we re-initialised it as true and we go to next tile
                    isOk = true;
            }
        }
        //We do the same thing horizontally and we check if there are some horizontal group-of-four
        for(int row = 0; row < HEIGHT ; row++){
            for(int column = 0; column < WIDTH - 3; column++){
                for(int i = 0;i < 4 && isOk; i++) {
                    if(library[column][row] == null || library[column + 1][row] == null
                            || used[column][row] || used[column + 1][row]
                            || library[column][row] != library[column + 1][row]) {
                        isOk = false;
                    }
                }
                if(isOk){
                    numGroups++;
                    for(int i = 0;i < 4; i++)
                        used[column+i][row] = true;
                }else
                    isOk = true;
            }
        }
        //Return true if the number of groups is >= 4
        return numGroups >= 4;
    }
}
