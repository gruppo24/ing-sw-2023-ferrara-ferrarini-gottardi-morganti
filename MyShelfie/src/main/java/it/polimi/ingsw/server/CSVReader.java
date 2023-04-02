package it.polimi.ingsw.server;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import it.polimi.ingsw.common.TileType;

/**
 * Class to read from PrivateCards.csv
 * @author Gottardi Arianna
 */

public class CSVReader {
    private FileInputStream f = null;
    private Scanner scanner = null;

    /**
     * Class constructor
     */
    public CSVReader(){
        File file = new File("src/main/java/it/polimi/ingsw/server/PrivateCards.csv");

        try{
            //creation of a new fileInputStream and a new scanner to read the file
            //Throw an Exception if the given file doesn't exist
            f = new FileInputStream(file);
            scanner = new Scanner(f);

        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    /**
     * Method that return a map TileTypes - Coordinates for the given card index
     * @param index representing the line in the csv file where there is a specific private card
     * @return the required map
     */
    public Map<TileType,Integer[]> getRow(int index) {
        //String where we can put the read line
        String lineRead = null;
        //The map where we put the result --> the content of the line
        Map<TileType,Integer[]> result = new HashMap<TileType,Integer[]>();

        //We scan the file until we found the required line
        for(int i = 1; i <= index && scanner.hasNextLine(); i++){
            scanner.nextLine();
        }
        //If the required line exist we put in the map the coordinates (converted to integer[])
        //Throw Exception if the line is null so if the index is "wrong"
        if(scanner.hasNextLine()){
            lineRead = scanner.nextLine();
            //the line is split by columns and each column is saved in an array's position
            String[] parts = lineRead.split(",");

            Integer[] bookCoordinate = new Integer[]{Integer.valueOf(parts[1]), Integer.valueOf(parts[2])};
            Integer[] catCoordinate = new Integer[]{Integer.valueOf(parts[3]),Integer.valueOf(parts[4])};
            Integer[] plantCoordinate = new Integer[]{Integer.valueOf(parts[5]),Integer.valueOf(parts[6])};
            Integer[] frameCoordinate = new Integer[]{Integer.valueOf(parts[7]),Integer.valueOf(parts[8])};
            Integer[] toyCoordinate = new Integer[]{Integer.valueOf(parts[9]),Integer.valueOf(parts[10])};
            Integer[] trophyCoordinate = new Integer[]{Integer.valueOf(parts[11]),Integer.valueOf(parts[12])};

            result.put(TileType.BOOK,bookCoordinate);
            result.put(TileType.CAT, catCoordinate);
            result.put(TileType.PLANT, plantCoordinate);
            result.put(TileType.FRAME, frameCoordinate);
            result.put(TileType.TOY, toyCoordinate);
            result.put(TileType.TROPHY, trophyCoordinate);

            return result;
        }
        else
            throw new IllegalArgumentException();
    }

    /**
     * Method that return the identifier of the given card index
     * @param index representing the line in the csv file where there is a specific private card
     * @return the required id
     */
    public String getId(int index){
        String lineRead = null;
        //Almost the same thing of getRow() but we return only parts[0] that is the first column of the file
        //--> the first column = identifier
        for(int i = 1; i <= index && scanner.hasNextLine(); i++){
            scanner.nextLine();
        }

        if(scanner.hasNextLine()){
            lineRead = scanner.nextLine();
            String id = null;
            String[] parts = lineRead.split(",");
            id = parts[0];

            return id;
        }
        else
            throw new IllegalArgumentException();
    }

    /**
     * Method that close the open scanner
     */
    public void close(){
        if(f != null){
            scanner.close();
        }
    }

}
