package it.polimi.ingsw.server.model;

import java.util.EnumMap;

import static it.polimi.ingsw.server.model.TileType.*;


/**
 * This class defines if the player has achieved or not common-objective 9
 * @author Ferrara Silvia
 */
public class CommonCard9 extends CommonCard{

    /**
     * class constructor
     * @param identifier unique identifier associated to the card. Required to find asset location on client
     * @param description textual description of the common objective
     */
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
        //Prefetching library dimensions
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;

        //Following map will assign to each tile the corresponding occurrence in the library
        EnumMap<TileType, Integer> cells = new EnumMap<>(TileType.class);
        cells.put(BOOK, 0);
        cells.put(CAT, 0);
        cells.put(FRAME, 0);
        cells.put(PLANT, 0);
        cells.put(TOY, 0);
        cells.put(TROPHY, 0);

        //we iterate through the entire matrix...
        for (int column=0; column < WIDTH; column++) {
            for (int row=0; row < HEIGHT; row++) {
                if(library[column][row] != null) {
                    //...for each cell we update our map appropriately
                    cells.put(library[column][row], cells.get(library[column][row]) + 1);
                    //...and we check if the number of the cells equals to the one considered are 8
                    if(cells.get(library[column][row]).equals(8))
                        return true;
                }
            }
        }
        return false;
    }
}
