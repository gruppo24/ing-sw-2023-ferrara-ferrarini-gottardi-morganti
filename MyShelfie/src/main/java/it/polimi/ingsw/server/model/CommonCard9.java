package it.polimi.ingsw.server.model;

import java.util.EnumMap;

import static it.polimi.ingsw.server.model.TileType.*;


/**
 * This class defines if the player has achieved or not common-objective 9
 */
public class CommonCard9 extends CommonCard{

    public CommonCard9(String identifier, String description){
        super(identifier, description);
    }

    /**
     * Method that checks if there are 8 equals tiles in the player grid
     * @param library library to check for objective matching
     * @return boolean that represents the achievement of the common objective
     */
    @Override
    public boolean checkObjective(TileType[][] library){
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;


        EnumMap<TileType, Integer> cells = new EnumMap<>(TileType.class);
        cells.put(BOOK, 0);
        cells.put(CAT, 0);
        cells.put(FRAME, 0);
        cells.put(PLANT, 0);
        cells.put(TOY, 0);
        cells.put(TROPHY, 0);

        for (int column=0; column < WIDTH; column++) {
            for (int row=0; row < HEIGHT; row++) {
                if(library[column][row] != null) {
                    cells.put(library[column][row], cells.get(library[column][row]) + 1);
                    if(cells.get(library[column][row]).equals(8))
                        return true;
                }
            }
        }
        return false;
    }
}
