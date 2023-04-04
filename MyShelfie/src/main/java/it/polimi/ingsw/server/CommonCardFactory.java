package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.CommonCard;
import it.polimi.ingsw.server.model.CommonCardImpl.*;

/**
 * Class containing method used to insert common cards instantiations in an array
 * @author Ferrara Silvia
 */
public class CommonCardFactory {
    /**
     *
     * Method used to insert the instances of common cards iterating on an array
     *
     * @param numCard indicates the number of the card we want to instantiate minus one (0 -> Common Card 1)
     * @return the instance of one of the CommonCards
     * @throws IllegalArgumentException if the numCard is not between 0 and 11 (number of common cards available)
     */
    public static CommonCard cardBuilder(int numCard) throws IllegalArgumentException{
        return switch(numCard){
            case 0 -> new CommonCard1("1.jpg", "Two groups each containing 4 tiles of " +
                                                                "the same type in a 2x2 square. The tiles " +
                                                                "of one square can be different from " +
                                                                "those of the other square.");
            case 1 -> new CommonCard2("2.jpg", "Two columns each formed by 6 " +
                                                                "different types of tiles.");
            case 2 -> new CommonCard3("3.jpg", "Four groups each containing at least " +
                                                                "4 tiles of the same type (not necessarily " +
                                                                "in the depicted shape). " +
                                                                "The tiles of one group can be different " +
                                                                "from those of another group.");
            case 3 -> new CommonCard4("4.jpg", "Six groups each containing at least " +
                                                                "2 tiles of the same type (not necessarily " +
                                                                "in the depicted shape). " +
                                                                "The tiles of one group can be different " +
                                                                "from those of another group.");
            case 4 -> new CommonCard5("5.jpg", "Three columns each formed by 6 tiles of maximum" +
                                                                "three different types. One column can show the" +
                                                                "same or a different combination of another column");
            case 5 -> new CommonCard6("6.jpg", "Two lines each formed by 5 different " +
                                                                "types of tiles. One line can show the " +
                                                                "same or a different combination of the " +
                                                                "other line.");
            case 6 -> new CommonCard7("7.jpg", "Four lines each formed by 5 tiles of " +
                                                                "maximum three different types. One " +
                                                                "line can show the same or a different " +
                                                                "combination of another line.");
            case 7 -> new CommonCard8("8.jpg", "Four tiles of the same type in the four " +
                                                                "corners of the bookshelf. ");
            case 8 -> new CommonCard9("9.jpg", "Eight tiles of the same type. There’s no " +
                                                                "restriction about the position of these " +
                                                                "tiles.");
            case 9 -> new CommonCard10("10.jpg", "Five tiles of the same type forming an X");
            case 10 -> new CommonCard11("11.jpg", "Five tiles of the same type forming a " +
                                                                "diagonal.");
            case 11 -> new CommonCard12("12.jpg", "Five columns of increasing or decreasing " +
                                                                "height. Starting from the first column on " +
                                                                "the left or on the right, each next column " +
                                                                "must be made of exactly one more tile. " +
                                                                "Tiles can be of any type.");
            default -> throw new IllegalArgumentException("Invalid number of commonCard");
        };
    }
}
