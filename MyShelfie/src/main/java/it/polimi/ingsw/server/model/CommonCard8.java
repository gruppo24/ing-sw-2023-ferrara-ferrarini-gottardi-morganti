package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.TileType;

/**
 * This class defines if the player has achieved or not common-objective 8
 * @author Ferrara Silvia
 */
public class CommonCard8 extends CommonCard {

    /**
     * class constructor
     * @param identifier unique identifier associated to the card. Required to find asset location on client
     * @param description textual description of the common objective
     */
    public CommonCard8(String identifier, String description) {
        super(identifier, description);
    }

    /**
     * Method that checks if the tiles at the corners are equals
     * @param library library to check for objective matching
     * @return boolean that represent the achievement of the common-objective
     */
    @Override
    public boolean checkObjective(TileType[][] library) {
        //library dimensions
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;

        //coordinates to check
        TileType pos0 = library[0][0];
        TileType pos1 = library[0][HEIGHT-1];
        TileType pos2 = library[WIDTH-1][0];
        TileType pos3 = library[WIDTH-1][HEIGHT-1];

        if (pos0 != null && pos1 != null && pos2 != null && pos3 != null) {
            return pos0 == pos1 && pos1 == pos2 && pos2 == pos3;
        }
        return false;
    }
}
