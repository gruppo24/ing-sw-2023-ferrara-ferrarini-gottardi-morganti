package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.TileType;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Ferrarini Andrea
 * This abstract class will be implemented by each common-objective-card.
 */
public abstract class CommonCard implements Serializable {

    // Attribute required for serialization
    @Serial
    private static final long serialVersionUID = 1L;

    public final String identifier;
    public final String description;

    /**
     * Class constructor
     * @param identifier unique identifier associated to the card. Required to find asset location on client
     * @param description textual description of the common objective
     */
    public CommonCard(String identifier, String description) {
        this.identifier = identifier;
        this.description = description;
    }

    /**
     * Function in charge of checking whether the library of a player contains the tiles required
     * to obtain the current objective
     * @param library library to check for objective matching
     * @return whether the current common objective has been achieved by the caller or not
     */
    public abstract boolean checkObjective(TileType[][] library);

    /**
     * This function is in charge of computing the amount of points obtained after achieving a common-objective
     * @param numOfPlayers number of players in the current game
     * @param order ordinal number representing when the player has achieved the common objective
     * @return amount of points achieved
     * @throws IllegalArgumentException when numOfPlayers is not between 1 and 4 or when order isn't
     *                                  between 1 and numOfPlayers
     */
    public static int mapCommonPoints(int numOfPlayers, int order) throws IllegalArgumentException {
        // Based on number of players we map the order of common-objective achievement to an amount of points
        if (numOfPlayers == 2) {
            return switch (order) {
                case 1 -> 8;
                case 2 -> 4;
                default -> throw new IllegalArgumentException(
                        "ERROR: common points for numOfPlayers == 2 requires " +
                                "order to be between 1 and 2 inclusive."
                );
            };
        } else if (numOfPlayers == 3) {
            return switch (order) {
                case 1 -> 8;
                case 2 -> 6;
                case 3 -> 4;
                default -> throw new IllegalArgumentException(
                        "ERROR: common points for numOfPlayers == 3 requires " +
                                "order to be between 1 and 3 inclusive."
                );
            };
        } else if (numOfPlayers == 4) {
            return switch (order) {
                case 1 -> 8;
                case 2 -> 6;
                case 3 -> 4;
                case 4 -> 2;
                default -> throw new IllegalArgumentException(
                        "ERROR: common points for numOfPlayers == 4 requires " +
                                "order to be between 1 and 4 inclusive."
                );
            };
        }

        // Fallthrough exception... numOfPlayers is invalid
        throw new IllegalArgumentException(
                "ERROR: numOfPlayers has to be between 1 and 4 inclusive."
        );
    }
}
