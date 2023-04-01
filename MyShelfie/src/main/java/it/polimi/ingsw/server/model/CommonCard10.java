package it.polimi.ingsw.server.model;
import it.polimi.ingsw.common.TileType;

/**
 * This class defines if the player has achieved or not common-objective 10
 * @author Ferrara Silvia
 */
public class CommonCard10 extends CommonCard{

    /**
     * class constructor
     * @param identifier unique identifier associated to the card. Required to find asset location on client
     * @param description textual description of the common objective
     */
    public CommonCard10(String identifier, String description){
        super(identifier, description);
    }

    /**
     * Method that checks if there are 5 equals tiles forming an X configuration
     * @param library library to check for objective matching
     * @return boolean that represents the achievement of the common objective
     */
    @Override
    public boolean checkObjective(TileType[][] library){
        //Prefetching library dimensions
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;

        //we iterate through the matrix
        for (int column=0; column < WIDTH-2; column++) {
            for (int row=0; row < HEIGHT-2; row++) {
                //we check if there are cells forming an X configurations and are not null
                if(library[column][row] != null && library[column][row+2] != null && library[column+1][row+1] != null
                        && library[column+2][row] != null && library[column+2][row+2] != null){
                    //we check if the cells forming an X configuration are equals
                    if(library[column][row].equals(library[column][row+2]) &&
                            library[column][row+2].equals(library[column+1][row+1]) &&
                            library[column+1][row+1].equals(library[column+2][row]) &&
                            library[column+2][row].equals(library[column+2][row+2]))
                        return true;
                }
            }
        }
        return false;
    }
}

