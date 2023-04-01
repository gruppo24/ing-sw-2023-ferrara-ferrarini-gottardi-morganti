package it.polimi.ingsw.server.model;
import it.polimi.ingsw.common.TileType;
import java.util.HashMap;

/**
 * This class check if the player has achieved or not common objective 5
 * @author Gottardi Arianna
 */
public class CommonCard5 extends CommonCard{
    /**
     * Class constructor
     *
     * @param identifier unique identifier associated to the card. Required to find asset location on client
     * @param description textual description of the common objective
     */
    public CommonCard5(String identifier, String description) {
        super(identifier, description);
    }

    /**
     * Method that check if there are three full column each containing at least three different types of tiles
     *
     * @param library library to check for objective matching
     * @return boolean that represents the achievement of the objective
     */
    @Override
    public boolean checkObjective(TileType[][] library){
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;

        //Attribute to count the number of columns that have 1,2,3 different types of tiles
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
            //Boolean to check if the column is full
            boolean isFull = true;
            //Attribute to count how many types of tiles there are in a column
            int numDifferentElements = 0;

            //Increment occurrences for each TileType if the cell is != null
            //If the cell is == null the column is not full
            for(int row=0; row < HEIGHT && isFull; row++){
                if(library[column][row] != null)
                    check.put(library[column][row],check.get(library[column][row])+1);
                else
                    isFull = false;
            }
            //If isFull == false --> The objective is not achieved for this column
            if(isFull) {
                //Count how many types of tiles there are in the map
                //-->if the value is 0 means that the TileType isn't in the column
                for (Integer value : check.values()) {
                    if (value != 0)
                        numDifferentElements++;
                }
                //if there are at least 3 different types of tile in the map the column is "ok"
                if (numDifferentElements <= 3)
                    numColumn++;
            }
        }
        //Return true if there are 3 columns that are "okay"
        return numColumn >= 3;
    }
}


