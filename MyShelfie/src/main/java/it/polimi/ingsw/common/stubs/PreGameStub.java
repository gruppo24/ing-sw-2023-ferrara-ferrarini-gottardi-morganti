package it.polimi.ingsw.common.stubs;
import it.polimi.ingsw.common.messages.responses.ResponseStatus;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * PreGameStub for jRMI
 *
 * @author Ferrara Silvia
 */
public interface PreGameStub extends Remote {

    /**
     * Method in charge of returning all connection available games
     *
     * @return Map of games. Key is game-Id, value is int[], containing curr players and max plyers
     * @throws RemoteException if jRMI exception
     */
    HashMap<String, int[]> getAvailableGames() throws RemoteException;

    /**
     * Method in charge of creating a new game
     *
     * @param gameID unique game identifier for the new game
     * @param numPlayers number of players who can join the game
     * @param username username of the creator
     * @return a ResponseStatus object representing the outcome of the operation
     * @throws RemoteException if jRMI exception
     */
    ResponseStatus createGame(String gameID, int numPlayers, String username) throws RemoteException;

    /**
     * Method in charge of joining a particular game
     *
     * @param gameID unique game identifier of the game we want to join
     * @param username username of the player who i requesting to join
     * @return a ResponseStatus object representing the outcome of the operation
     * @throws RemoteException if jRMI exception
     */
    ResponseStatus joinGame(String gameID, String username) throws RemoteException;

}
