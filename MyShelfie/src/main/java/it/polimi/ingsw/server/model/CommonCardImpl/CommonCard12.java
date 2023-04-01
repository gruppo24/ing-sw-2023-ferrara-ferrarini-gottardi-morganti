package it.polimi.ingsw.server.model.CommonCardImpl;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.model.CommonCard;

/**
 * This class defines if the player has achieved or not common-objective 12
 * @author Ferrara Silvia
 */
public class CommonCard12 extends CommonCard {

    /**
     * class constructor
     * @param identifier unique identifier associated to the card. Required to find asset location on client
     * @param description textual description of the common objective
     */
    public CommonCard12(String identifier, String description) {
        super(identifier, description);
    }

    /**
     * Method that checks if there are columns of rising height, starting from the right or left corner
     * @param library library to check for objective matching
     * @return boolean that represents the achievement of the common objective
     */
    @Override
    public boolean checkObjective(TileType[][] library) {
        ///the player achieve the common-objective if he/she has a right
        // "rising diagonal" config. or a "left diagonal" config.
        return (diagonalDX(library) || diagonalSX(library));
    }

    private boolean diagonalDX (TileType[][] library){
        //library dimensions
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;

        int oldNum = 0; //counter that indicates the number of tiles contained in the previous column
        int newNum = 0; //counter that indicates the number of tiles contained in the current column

        //we iterate through the matrix
        for (int column = 0; column < WIDTH; column++) {
            for (int row = 0; row < HEIGHT; row++) {
                //we count the number of tiles in each column
                if (library[column][row] != null) {
                    newNum++;
                }
            }
            //we check if the column rising height condition (in right direction) is respected
            if(newNum != oldNum +1)
                return false;
            //counters are set for the next iteration
            oldNum = newNum;
            newNum = 0;
        }
        return true;
    }

    private boolean diagonalSX (TileType[][] library){
        //library dimensions
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;

        int oldNum = 0; //counter that indicates the number of tiles contained in the previous column
        int newNum = 0; //counter that indicates the number of tiles contained in the current column

        //we iterate through the matrix
        for (int column = WIDTH-1; column >= 0; column--) {
            for (int row = 0; row < HEIGHT; row++){
                //we count the number of tiles in each column
                if (library[column][row] != null){
                    newNum++;
                }
            }
            //we check if the column rising height condition (in left direction) is respected
            if(newNum != oldNum +1)
                return false;
            //counters are set for the next iteration
            oldNum = newNum;
            newNum = 0;
        }
        return true;
    }


}
