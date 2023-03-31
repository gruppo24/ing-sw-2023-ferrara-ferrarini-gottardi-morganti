package it.polimi.ingsw.server.model;

/**
 * This class defines if the player has achieved or not common-objective 10
 * @author Ferrara Silvia
 */
public class CommonCard12 extends CommonCard {

    public CommonCard12(String identifier, String description) {
        super(identifier, description);
    }

    /**
     * @param library library to check for objective matching
     * @return boolean that represents the achievement of the common objective
     */
    @Override
    public boolean checkObjective(TileType[][] library) {
        //library dimensions
        return (diagonalDX(library) || diagonalSX(library));
    }

    public boolean diagonalDX (TileType[][] library){
        //library dimensions
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;

        int oldNum = 0;
        int newNum = 0;

        for (int column = 0; column < WIDTH; column++) {
            for (int row = 0; row < HEIGHT; row++) {
                if (library[column][row] != null) {
                    newNum++;
                }
            }
            if(newNum != oldNum +1)
                return false;
            oldNum = newNum;
            newNum = 0;
        }
        return true;
    }

    public boolean diagonalSX (TileType[][] library){
        //library dimensions
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;

        int oldNum = 0;
        int newNum = 0;

        for (int column = WIDTH-1; column >= 0; column--) {
            for (int row = 0; row < HEIGHT; row++){
                if (library[column][row] != null){
                    newNum++;
                }
            }
            if(newNum != oldNum +1)
                return false;
            oldNum = newNum;
            newNum = 0;
        }
        return true;
    }


}
