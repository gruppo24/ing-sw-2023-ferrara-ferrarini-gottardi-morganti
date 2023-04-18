package it.polimi.ingsw.common.stubs;
import it.polimi.ingsw.common.messages.responses.ResponseStatus;

import java.rmi.Remote;
import java.util.HashMap;

/**
 * PreGameStub for JRMI
 *
 * @author Ferrara Silvia
 */
public interface PreGameStub extends Remote {
    HashMap<String, int[]> getAvailableGames();

    ResponseStatus createGame(String gameId, int numPlayers, String username);

    ResponseStatus joinGame(String gameId, String username);

}
