package it.polimi.ingsw.server.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Ferrara Silvia
 * Class that describes the state of a specif game
 */
public class GameState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String gameUniqueCode;

    // attributes related to turn management
    private int armchair;
    private int currPlayerIndex;
    private boolean finalRound = false;
    private Player[] players;

    private Board board;

    // attributes related to common objectives
    private int[] nextCommons = {1,1};
    private CommonCard[] commonCards;

    public Object gameLock = new Object();

    /**
     * class constructor
     * @param GameID unique game identifier
     * @param numOfPlayers number of players for the current game
     */
    public GameState(String GameID, int numOfPlayers){
        this.gameUniqueCode = GameID;
        this.players = new Player[numOfPlayers];
        this.board = new Board();
    }

    /**
     * method that updates the library of the player (identified with the currPlayerIndex) with the Tiles picked previously
     * @param currPlayerIndex the index associated to the player whose library will be updated
     */
    public void updateLibrary(int currPlayerIndex) {
    }

    /**
     * method that checks if a Player has achieved or not any Common Objectives
     * @param currPlayerIndex index of player to check for common objectives
     */
    public void obtainedCommons(int currPlayerIndex) {
        // We iterate through all common cards of the current player
        for (int i=0; i < commonCards.length; i++) {
            // If the player hasn't already obtained the common objective...
            if (players[currPlayerIndex].commonsOrder[i] == 0) {
                // ...We check whether they have completed it now...
                if (commonCards[i].checkObjective(players[currPlayerIndex].getLibrary())) {
                    // ...in which case we assign the current commonOrder value and then increment it
                    players[currPlayerIndex].commonsOrder[i] = nextCommons[i];
                    nextCommons[i]++;
                }
            }
        }
    }

}
