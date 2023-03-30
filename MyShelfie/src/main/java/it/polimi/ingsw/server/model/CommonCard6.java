package it.polimi.ingsw.server.model;

import java.util.HashMap;
/**
 * This class check if the player has achieved or not common objective 6
 */
public class CommonCard6 extends CommonCard{
        public CommonCard6(String identifier, String description) {
            super(identifier, description);
        }
        /**
         * Method that check if there are two rows with five different types of tiles
         *
         * @param library library to check for objective matching
         * @return boolean that represents the achievement of the objective
         */
        @Override
        public boolean checkObjective(TileType[][] library){
            final int WIDTH = library.length;
            final int HEIGHT = library[0].length;

            //Attribute to count the number of rows that have 5 different types of tiles
            int numRows = 0;

            //Hashmap to check the number of tiles for each type
            HashMap<TileType, Integer> check = new HashMap<>();

            //Iteration through the entire matrix
            for(int row=0; row < HEIGHT; row++){
                //clean the hashmap for each rows
                check.put(TileType.BOOK,0);
                check.put(TileType.CAT,0);
                check.put(TileType.FRAME,0);
                check.put(TileType.PLANT,0);
                check.put(TileType.TOY,0);
                check.put(TileType.TROPHY,0);

                //Attribute to count how many types of tiles there are in a row
                int numDifferentElements = 0;

                //Increment occurrences for each TileType if the cell is != null
                for(int column=0; column < WIDTH; column++){
                    if(library[column][row] != null)
                        check.put(library[column][row],check.get(library[column][row])+1);
                }
                //Count how many types of tiles there are in the map
                //-->if the value is 0 means that the TileType isn't in the row
                for (Integer value : check.values()) {
                    if (value != 0)
                        numDifferentElements++;
                }
                //if there are exactly five types of tiles--> the row is "ok"
                if (numDifferentElements == 5)
                    numRows++;
            }
            //Return true if there are 2 rows that are "ok"
            return numRows == 2;
        }
}



