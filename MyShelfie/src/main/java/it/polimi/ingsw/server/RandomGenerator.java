package it.polimi.ingsw.server;
import java.util.Random;

public class RandomGenerator {
    public int[] RandomPositions(int dim){
        boolean isIn = false;
        int[] randomPositions = new int[dim];
        for(int i = 0; i < dim; i++){
            do {
                int x = new Random().nextInt(0, 11);
                for (int j = 0; j < dim && !isIn; j++) {
                    if (x == randomPositions[j])
                        isIn = true;
                }
                if (!isIn)
                    randomPositions[i] = x;
            } while (isIn);

        }
        return randomPositions;
    }
}
