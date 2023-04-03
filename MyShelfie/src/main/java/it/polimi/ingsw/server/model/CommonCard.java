package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.model.CommonCardImpl.*;

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

    public static CommonCard cardBuilder(int order) throws IllegalArgumentException{
        CommonCard value = null;
        switch(order){
            case 0 -> value = new CommonCard1("1.jpg", "Two groups each containing 4 tiles of " +
                                                                        "the same type in a 2x2 square. The tiles " +
                                                                        "of one square can be different from " +
                                                                        "those of the other square.");
            case 1 -> value = new CommonCard2("2.jpg", "Two columns each formed by 6 " +
                                                                        "different types of tiles.");
            case 2 -> value = new CommonCard3("3.jpg", "Four groups each containing at least " +
                                                                        "4 tiles of the same type (not necessarily " +
                                                                        "in the depicted shape). " +
                                                                        "The tiles of one group can be different " +
                                                                        "from those of another group.");
            case 3 -> value = new CommonCard4("4.jpg", "Six groups each containing at least " +
                                                                        "2 tiles of the same type (not necessarily " +
                                                                        "in the depicted shape). " +
                                                                        "The tiles of one group can be different " +
                                                                        "from those of another group.");
            case 4 -> value = new CommonCard5("5.jpg", "Three columns each formed by 6 tiles of maximum" +
                                                                        "three different types. One column can show the" +
                                                                        "same or a different combination of another column");
            case 5 -> value = new CommonCard6("6.jpg", "Two lines each formed by 5 different " +
                                                                        "types of tiles. One line can show the " +
                                                                        "same or a different combination of the " +
                                                                        "other line.");
            case 6 -> value = new CommonCard7("7.jpg", "Four lines each formed by 5 tiles of " +
                                                                        "maximum three different types. One " +
                                                                        "line can show the same or a different " +
                                                                        "combination of another line.");
            case 7 -> value = new CommonCard8("8.jpg", "Four tiles of the same type in the four " +
                                                                        "corners of the bookshelf. ");
            case 8 -> value = new CommonCard9("9.jpg", "Eight tiles of the same type. There’s no " +
                                                                        "restriction about the position of these " +
                                                                        "tiles.");
            case 9 -> value = new CommonCard10("10.jpg", "Five tiles of the same type forming an X");
            case 10 -> value = new CommonCard11("11.jpg", "Five tiles of the same type forming a " +
                                                                        "diagonal.");
            case 11 -> value = new CommonCard12("12.jpg", "Five columns of increasing or decreasing " +
                                                                        "height. Starting from the first column on " +
                                                                        "the left or on the right, each next column " +
                                                                        "must be made of exactly one more tile. " +
                                                                        "Tiles can be of any type.");
        }
        return value;
    }
}
