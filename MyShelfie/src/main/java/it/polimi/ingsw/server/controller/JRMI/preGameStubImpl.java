package it.polimi.ingsw.server.controller.JRMI;

import it.polimi.ingsw.common.messages.responses.GamesList;
import it.polimi.ingsw.common.messages.responses.ResponseStatus;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.server.Server.GAMES;
import static it.polimi.ingsw.server.controller.socket.TCPPregameChannel.sendEmptyMessage;

public class PreGameStubImpl extends UnicastRemoteObject implements PreGameStub {
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
