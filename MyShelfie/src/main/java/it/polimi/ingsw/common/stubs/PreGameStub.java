package it.polimi.ingsw.common.stubs;
import it.polimi.ingsw.common.messages.responses.ResponseStatus;

import java.rmi.Remote;
import java.util.HashMap;

public interface PreGameStub extends Remote {
    public HashMap<String, int[]> getAvailableGames();

    public ResponseStatus createGame(String gameId, int numPlayers, String username);

    public ResponseStatus joinGame(String gameId, String username);

}
