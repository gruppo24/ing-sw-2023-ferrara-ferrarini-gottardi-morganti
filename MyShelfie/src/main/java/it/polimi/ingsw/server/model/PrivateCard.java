package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.TileType;

import java.io.Serializable;
import java.util.Map;

/**
 * A Private Objective card, which containes a map with coordinates for each
 * tile type and a description
 * 
 * @author Morganti Tommaso
 */
public final class PrivateCard implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * unique identifier for the card, used to get the right asset in the GUI
     */
    public final String identifier;

    /**
     * Map with coordinates for each {@link TileType}.
     * When the the tile in the coordinates is of the same type as the key, the
     * player gets points as defined by {@link PrivateCard#mapPrivatePoints(int)}
     */
    public final Map<TileType, Integer[]> objectives;

    /**
     * A description of the card, which is used in the CLI to describe the card
     */
    public final String description;

    /**
     * @param identifier unique identifier for the card, used to get the right asset
     *                   in the GUI
     * @param objectives a map with coordinates for each tile type
     */
    public PrivateCard(String identifier, Map<TileType, Integer[]> objectives) {
        this.identifier = identifier;
        this.objectives = objectives;
        String desc = "Private Objective:\n";
        for (TileType type : objectives.keySet()) {
            desc += " " + type.toString() + ": (" + objectives.get(type)[0] + ","
                    + objectives.get(type)[1] + ")\n";
        }
        this.description = desc;
    }

    /**
     * Maps the number of correct cells in the library as described in
     * PrivateCard.objectives to the number of points
     * 
     * @param objectives the number of cells in the library that correspond with
     *                   PrivateCard.objectives TileType and coordinates entries
     * @return the number of points
     * @throws IllegalArgumentException if objectives is not between 0 and 6
     */
    public static int mapPrivatePoints(int objectives) throws IllegalArgumentException {
        return switch (objectives) {
            case 0 -> 0;
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 4;
            case 4 -> 6;
            case 5 -> 9;
            case 6 -> 12;
            default -> throw new IllegalArgumentException(
                    "Invalid number of objectives (must be between 0 and 6, is " + objectives + ")");
        };
    }
}
