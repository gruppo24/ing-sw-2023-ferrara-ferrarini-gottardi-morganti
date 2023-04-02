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
 *
 */

public class CSVReader {
    private FileInputStream f = null;
    private Scanner scanner = null;

    /**
     *
     */
    public CSVReader(){
        File file = new File("src/main/java/it/polimi/ingsw/server/PrivateCards.csv");

        try{

            f = new FileInputStream(file);
            scanner = new Scanner(f);

        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @param index
     * @return
     */
    public Map<TileType,Integer[]> getRow(int index) {
        String lineRead = null;
        Map<TileType,Integer[]> result = new HashMap<TileType,Integer[]>();

        for(int i = 1; i <= index && scanner.hasNextLine(); i++){
            scanner.nextLine();
        }

        if(scanner.hasNextLine()){
            lineRead = scanner.nextLine();
            String[] parts = lineRead.split(",");

            Integer[] bookCoordinate = new Integer[]{Integer.valueOf(parts[1]), Integer.valueOf(parts[2])};
            Integer[] catCoordinate = new Integer[]{Integer.valueOf(parts[3]),Integer.valueOf(parts[4])};
            Integer[] plantCoordinate =new Integer[]{Integer.valueOf(parts[5]),Integer.valueOf(parts[6])};
            Integer[] frameCoordinate =new Integer[]{Integer.valueOf(parts[7]),Integer.valueOf(parts[8])};
            Integer[] toyCoordinate =new Integer[]{Integer.valueOf(parts[9]),Integer.valueOf(parts[10])};
            Integer[] trophyCoordinate =new Integer[]{Integer.valueOf(parts[11]),Integer.valueOf(parts[12])};

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
     *
     * @param index
     * @return
     */
    public String getId(int index){
        String lineRead = null;

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
     *
     */
    public void close(){
        if(f != null){
            scanner.close();
        }
    }

}
