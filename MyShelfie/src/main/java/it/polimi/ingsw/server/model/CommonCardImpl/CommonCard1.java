package it.polimi.ingsw.server.model.CommonCardImpl;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.model.CommonCard;

import java.util.HashMap;

/**
 * This class check if the player has achieved or not common objective 1
 * @author Gottardi Arianna
 */
public class CommonCard1 extends CommonCard {
    /**
     * Class constructor
     *
     * @param identifier unique identifier associated to the card. Required to find asset location on client
     * @param description textual description of the common objective
     */
    public CommonCard1(String identifier, String description) {
        super(identifier, description);
    }

    /**
     * Method that check if there are two groups each containing four tiles of the same type in a 2x2 square.
     * @param library library to check for objective matching
     * @return boolean that represents the achievement of the objective
     */
    @Override
    public boolean checkObjective(TileType[][] library){
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;

        //Matrix of boolean to check if a cell is already in a group
        boolean[][] used= new boolean[WIDTH][HEIGHT];

        //Attribute to count the number of 2x2 groups ("ok groups")
        int numGroups = 0;

        //Iteration through the matrix
        for(int column = 0; column< WIDTH-1 ; column++){
            for(int row = 0; row < HEIGHT-1 ; row++){
                //For each cell we check if it could be part of a group
                //we check if it is not null + not used + equals to its "neighbours"
                if(library[column][row] == library[column+1][row] &&
                        library[column+1][row] == library[column][row+1]
                        && library[column][row+1] == library[column+1][row+1]
                        && !used[column][row] && !used[column+1][row] && !used[column][row+1]
                        && !used[column+1][row+1] && library[column][row] != null
                        && library[column+1][row] != null && library[column][row+1] != null
                        && library[column+1][row+1] != null){
                    //if it is "ok" --> the positions become used
                    // --> we can increase the number of founded "ok groups"
                    used[column][row] = true;
                    used[column+1][row] = true;
                    used[column][row+1] = true;
                    used[column+1][row + 1] = true;
                    numGroups ++;
                }
            }
        }
        //Return true if the number of "ok groups" are >=2
        return numGroups >= 2;
    }
}

