package it.polimi.ingsw.server.model;
/**
 * This class check if the player has achieved or not common objective 1
 */
public class CommonCard1 extends CommonCard{
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

        //Count the number of groups 2x2 of the same type of tile
        int numSubMatrix = 0;

        //Iteration through the matrix
        for(int columns = 0; columns < WIDTH - 1; columns++){
            for (int row = 0; row < HEIGHT - 1; row++){
                //Check if a cell is equals to its "neighbors" and if they are still not used
                if(library[columns][row] == library[columns+1][row] &&
                        library[columns+1][row] == library[columns][row+1]
                        && library[columns][row+1] == library[columns+1][row+1]
                        && !used[columns][row] && !used[columns+1][row] && !used[columns][row+1]
                        && !used[columns+1][row+1]) {
                    //Create a new group --> Cells become "used"
                    used[columns][row] = true;
                    used[columns+1][row] = true;
                    used[columns][row+1] = true;
                    used[columns+1][row+1] = true;
                    numSubMatrix++;

                }

            }
        }
        //Return true if there are two groups
        return numSubMatrix == 2;

    }
}
