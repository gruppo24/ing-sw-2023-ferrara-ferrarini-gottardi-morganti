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
    private Integer[] nextCommons = {1,1};
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
    public void updateLibrary(int currPlayerIndex){
    }

    /**
     * method that checks at the end of the current player turn if there are "legal" available moves for the next player
     * @return a boolean representing whether the board should be refilled or not
     */
    public boolean shouldRefillBoard(){
        return false;
    }

    /**
     * method that checks if a Player has achieved or not any Common Objectives
     * @param currPlayerIndex index of player to check for common objectives
     */
    public void obtainedCommons(int currPlayerIndex){
    }


}
