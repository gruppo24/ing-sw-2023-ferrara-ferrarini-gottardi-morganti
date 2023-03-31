package it.polimi.ingsw.server.model;

import java.util.HashMap;

/**
 * This class defines if the player has achieved or not common-objective 7
 * @author Ferrara Silvia
 */
public class CommonCard7 extends CommonCard{

    /**
     * class constructor
     * @param identifier unique identifier associated to the card. Required to find asset location on client
     * @param description textual description of the common objective
     */
    public CommonCard7(String identifier, String description) {
        super(identifier, description);
    }

    /**
     * Method that checks if there are 4 different rows containing 1, 2 or 3 types of tiles
     * @param library library to check for objective matching
     * @return boolean that represents the achievement of the common objective
     */
    @Override
    public boolean checkObjective(TileType[][] library) {
        //Prefetching library dimensions
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;

        int different = 0;
        int numRows = 0;

        HashMap<TileType, Integer> found = new HashMap<>();
        found.put(TileType.BOOK, 0);
        found.put(TileType.CAT, 0);
        found.put(TileType.FRAME, 0);
        found.put(TileType.PLANT, 0);
        found.put(TileType.TOY, 0);
        found.put(TileType.TROPHY, 0);

        for (int row = 0; row < HEIGHT; row++){
            for (int column = 0; column < WIDTH; column++){
                if (library[column][row] != null){
                    found.put(library[column][row], found.get(library[column][row]) + 1);
                } else if(library[column][row] == null && numRows<=3){
                    return false;
                }
            }
            for (TileType key : found.keySet()){
                if (found.get(key) != 0){
                    different++;
                    found.put(key, 0);
                }
            }
            System.out.println(different);
            if (different <= 3 && different > 0){
                numRows++;
            }

            if (numRows == 4) {
                return true;
            }
            different = 0;
        }
        return false;
    }


}
