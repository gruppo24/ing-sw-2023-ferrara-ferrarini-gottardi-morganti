package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.messages.responses.SharedGameState;
import it.polimi.ingsw.server.RandomGenerator;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.exceptions.GameAlreadyFullException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Class that describes the state of a specif game
 *
 * @author Ferrara Silvia
 * @author Ferrarini Andrea
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
    private int[] nextCommons = { 1, 1 };
    private CommonCard[] commonCards;

    public final Object gameLock = new Object();
    private boolean gameOver = false;

    /**
     * class constructor
     * 
     * @param GameID       unique game identifier
     * @param numOfPlayers number of players for the current game
     */
    public GameState(String GameID, int numOfPlayers) {
        this.gameUniqueCode = GameID;
        this.players = new Player[numOfPlayers];
        this.board = new Board();
        this.commonCards = new CommonCard[2];

        //we populate the CommonCards array with random CommonCards
        int[] numOfCard = RandomGenerator.random(2);
        for(int i = 0; i < numOfCard.length; i++){
            this.commonCards[i] = Server.commonCards[numOfCard[i]];
        }
    }

    /**
     * getter function for gameOver attribute
     * 
     * @return the gameOver attribute
     */
    public boolean isGameOver() {
        return this.gameOver;
    }

    /**
     * getter method for gameUniqueCode
     * 
     * @return the unique game ID of the game
     */
    public String getGameID() {
        return this.gameUniqueCode;
    }

    /**
     * Method in charge of returning the current player status of the game, that is,
     * the number of players this game takes and the number of players which have
     * actually already joined the game
     * 
     * @return an array of two ints: players who have already joined, total players
     *         of the game
     */
    public int[] getPlayerStatus() {
        int numOfPlayers = this.players.length;

        // Finding how many players have actually already joined the game
        int currPlayers = (int) Arrays.stream(this.players).filter(Objects::nonNull).count();

        return new int[] { currPlayers, numOfPlayers };
    }

    /**
     * Method in charge of checking whether a player with a given username already
     * exists
     * within current game
     * 
     * @param testUsername username to look for
     * @return whether the provided username has already been used
     */
    public boolean usernameAlreadyUsed(String testUsername) {
        return Arrays.stream(this.players).filter(Objects::nonNull).anyMatch(p -> p.nickname.equals(testUsername));
    }

    /**
     * Method in charge of adding a new player to the current game. Some
     * preconditions
     * are that the player has a unique username and that the game can still accept
     * players
     * 
     * @param player the player to be added
     * @throws GameAlreadyFullException whenever it is attempted to add a new
     *                                  player, but the game is already full
     */
    public void addNewPlayerToGame(Player player) throws GameAlreadyFullException {
        int newPlayerIndex = this.getPlayerStatus()[0];
        if (newPlayerIndex >= this.getPlayerStatus()[1])
            throw new GameAlreadyFullException(this.getPlayerStatus()[1]);
        this.players[newPlayerIndex] = player;
    }

    /**
     * Function in charge of telling the caller if it is a certain player's turn
     * 
     * @param player player of whom we want to know if it is the turn
     * @return whether it is the passed player's turn
     */
    public synchronized boolean actuallyIsPlayersTurn(Player player) {
        return player.equals(this.players[this.currPlayerIndex]);
    }

    /**
     * getter method for board attribute
     * 
     * @return the board associated to this game
     */
    public Board getBoard() {
        return this.board;
    }

    public SharedGameState getSharedGameState(Player player) {

        return null;
    }

    /**
     * method that checks if a Player has achieved or not any Common Objectives
     * 
     * @param currPlayerIndex index of player to check for common objectives
     */
    public void obtainedCommons(int currPlayerIndex) {
        // We iterate through all common cards of the current player
        for (int i = 0; i < commonCards.length; i++) {
            // If the player hasn't already obtained the common objective...
            if (players[currPlayerIndex].commonsOrder[i] == 0) {
                // ...We check whether they have completed it now...
                if (commonCards[i].checkObjective(players[currPlayerIndex].getLibrary())) {
                    // ...in which case we assign the current commonOrder value and then increment
                    // it
                    players[currPlayerIndex].commonsOrder[i] = nextCommons[i];
                    nextCommons[i]++;
                }
            }
        }
    }

}
