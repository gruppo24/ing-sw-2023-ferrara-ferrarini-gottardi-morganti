package it.polimi.ingsw.server;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.model.PrivateCard;

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
     * @throws IllegalArgumentException
     */
    public PrivateCard getRow(int index) throws IllegalArgumentException {
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
            int CSVColumn = 1;

            for (TileType tile : TileType.values()) {
                result.put(tile, new Integer[]
                        {Integer.valueOf(parts[CSVColumn]), Integer.valueOf(parts[CSVColumn + 1])});
                CSVColumn += 2;
            }
            return new PrivateCard(parts[0],result);
        }
        else
            throw new IllegalArgumentException("ERROR: Index: " + index + "out of CSV file range");
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
