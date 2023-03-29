package it.polimi.ingsw.server.model;

/**
 * This class defines if the player has achieved or not common-objective 8
 */
public class CommonCard8 extends CommonCard {

    /**
     * Class constructor
     *
     * @param identifier  unique identifier associated to the card. Required to find asset location on client
     * @param description textual description of the common objective
     */
    public CommonCard8(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public boolean checkObjective(TileType[][] library) {
        final int WIDTH = library.length;
        final int HEIGHT = library[0].length;

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
