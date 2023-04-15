package it.polimi.ingsw.server.controller.JRMI;

import it.polimi.ingsw.common.messages.responses.ResponseStatus;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;


public class PreGameStubImpl extends UnicastRemoteObject implements PreGameStub{
    public PreGameStubImpl() throws RemoteException {
        super();
    }

    @Override
    public Map<String, int[]> getAvailableGames() throws RemoteException {
       return  null;
    }
    @Override
    public ResponseStatus createGame(String username, int numPlayers) throws RemoteException{
        return null;
    }
    @Override
    public ResponseStatus connectToGame(String gameId, String username, boolean rejoining) throws RemoteException{
        return null;
    }
}
