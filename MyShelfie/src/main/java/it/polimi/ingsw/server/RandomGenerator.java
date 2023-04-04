package it.polimi.ingsw.server;
import java.util.Random;

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
        boolean isIn = false; //indicates if a number has already been generated
        int[] randomPositions = new int[dim]; //array that will contain all the random numbers from 0 to 11
        int count = 0; //indicates the indices of the last number saved in the array randomPositions

        //we iterate in order to populate with different
        //random numbers from 0 to 12 the array randomPositions
        do {
            int x = new Random().nextInt(12);
            for (int i = 0; i < dim; i++) {
                if (x == randomPositions[i]) {
                    isIn = true;
                    break;
                }
            }
            if(!isIn){
                randomPositions[count] = x;
                count++;
            }
            isIn = false;
        } while (count < dim);
        return randomPositions;
    }
}
