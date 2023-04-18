package it.polimi.ingsw.server.controller.JRMI;

import it.polimi.ingsw.common.messages.responses.ResponseStatus;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface PreGameStub extends Remote  {

    public Map<String, int[]> getAvailableGames() throws RemoteException;

    public ResponseStatus createGame(String username, int numPlayers) throws RemoteException;

    public ResponseStatus connectToGame(String gameId, String username, boolean rejoining) throws RemoteException;

}
