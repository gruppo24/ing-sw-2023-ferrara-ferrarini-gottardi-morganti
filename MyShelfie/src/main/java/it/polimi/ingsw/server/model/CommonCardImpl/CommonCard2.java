package it.polimi.ingsw.server.model.CommonCardImpl;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.model.CommonCard;

import java.util.HashMap;

/**
 * This class check if the player has achieved or not common objective 2
 * @author Gottardi Arianna
 */
public class CommonCard2 extends CommonCard {
    /**
     * Class constructor
     *
     * @param identifier  unique identifier associated to the card. Required to find asset location on client
     * @param description textual description of the common objective
     */
    public CommonCard2(String identifier, String description) {
        super(identifier, description);
    }

    /**
     * Method that check if there are two columns with six different type of tiles
     * @param library library to check for objective matching
     * @return boolean that represents the achievement of the objective
     */
    @Override
    public boolean checkObjective(TileType[][] library){
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;

        //Attribute to check how many columns have 6 different tiles
        int numColumn = 0;

        //Hashmap to check the number of tiles for each type
        HashMap<TileType, Integer> check = new HashMap<>();

        //Iteration through the entire matrix
        for(int column=0; column < WIDTH; column++){
            //clean the hashmap for each column
            check.put(TileType.BOOK,0);
            check.put(TileType.CAT,0);
            check.put(TileType.FRAME,0);
            check.put(TileType.PLANT,0);
            check.put(TileType.TOY,0);
            check.put(TileType.TROPHY,0);
            //Increment occurrences for each TileType
            for(int row=0; row < HEIGHT; row++){
                if(library[column][row] != null)
                    check.put(library[column][row],check.get(library[column][row])+1);
            }
            //Check if in a column there are exactly one tile for each type
            if(check.get(TileType.BOOK) == 1 && check.get(TileType.CAT) == 1
                    && check.get(TileType.FRAME) == 1 && check.get(TileType.PLANT) == 1
                        && check.get(TileType.TOY) == 1 && check.get(TileType.TROPHY) == 1)
                            numColumn++;
        }

        //Return true if there are two columns that are "ok"
       return numColumn >= 2;
    }
}
