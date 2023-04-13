package it.polimi.ingsw.server;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class containing method that generates numbers randomly
 *
 * @author Ferrara Silvia and Gottardi Arianna
 */

public class RandomGenerator {

    /**
     * Method that populates an array with different random numbers from 0 to 11
     *
     * @param dim amount of numbers we want to be generated
     * @return array that will contain random numbers from 0 to 11
     */
    public static int[] random(int dim) {
        int[] myArray = new int[dim];
        ArrayList<Integer> numOfCards = new ArrayList<>();

        for(int i=0; i<12; i++) {
            numOfCards.add(i);
        }

        Collections.shuffle(numOfCards);
        for(int i=0; i<dim; i++)
            myArray[i] = numOfCards.get(i);

        return myArray;
    }
}
