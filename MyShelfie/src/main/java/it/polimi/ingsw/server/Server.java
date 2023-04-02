package it.polimi.ingsw.server;
import it.polimi.ingsw.server.model.PrivateCard;

public class Server {
    public static final PrivateCard[] privateCards = new PrivateCard[12];

    /**
     * This class fills a PrivateCard array that contains PrivateCard instances
     * The information needed to PrivateCard constructor are read from csv files with the CSVReader
     */
    private static void createPrivateCards(){
        for(int i = 0; i < 12; i++){
            CSVReader reader1 = new CSVReader();
            CSVReader reader2 = new CSVReader();
            privateCards[i] = new PrivateCard(reader1.getId(i+1),reader2.getRow(i+1));
            reader1.close();
            reader2.close();
        }
    }

    public static void main(String[] args) {
    }
}
