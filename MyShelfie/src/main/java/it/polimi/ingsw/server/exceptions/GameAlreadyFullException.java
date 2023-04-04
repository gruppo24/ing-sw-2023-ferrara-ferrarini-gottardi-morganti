package it.polimi.ingsw.server.exceptions;

/**
 * This class is in charge of throwing an error message whenever
 * it is attempted to add a new player to an already full game
 *
 * @author Ferrarini Andrea
 */
public class GameAlreadyFullException extends RuntimeException {

    /**
     * Class constructor
     */
    public GameAlreadyFullException() {
        super("ERROR: This game can't accept any more players!");
    }

    /**
     * Class constructor
     * @param maxPlayers maximum number of players allowed for the
     *                   game which threw the exception
     */
    public GameAlreadyFullException(int maxPlayers) {
        super("ERROR: this game can accept at most " + maxPlayers + " players!");
    }
}
