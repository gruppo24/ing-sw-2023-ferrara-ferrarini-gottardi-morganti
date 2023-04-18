package it.polimi.ingsw.common.stubs;
import it.polimi.ingsw.common.messages.responses.ResponseStatus;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * PreGameStub for JRMI
 *
 * @author Ferrara Silvia
 */
public interface PreGameStub extends Remote {
    HashMap<String, int[]> getAvailableGames() throws RemoteException;

    ResponseStatus createGame(String gameID, int numPlayers, String username) throws RemoteException;

    ResponseStatus joinGame(String gameID, String username) throws RemoteException;

}
