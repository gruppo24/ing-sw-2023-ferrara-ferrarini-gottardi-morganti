package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.common.messages.responses.ResponseStatus;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import it.polimi.ingsw.common.stubs.GameActionStub;
import it.polimi.ingsw.common.stubs.PreGameStub;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

/**
 * Class that represents the connection via JRMI, see {@link Connection}
 * @author Gottardi Arianna
 */

public class JRMIConnection extends Connection{

    private final String host;
    private final int port;

    private final Registry registry;
    private PreGameStub preGame;
    private GameActionStub gameAction;

    public JRMIConnection(String host, int port) throws RemoteException {
        super();
        this.host = host;
        this.port = port;
        this.registry = LocateRegistry.getRegistry(host, port);
    }

    @Override
    public void establishConnection() throws IOException{
        try {
            this.preGame = (PreGameStub) registry.lookup("remotePreGame");
        }catch (NotBoundException | RemoteException e) {
            e.printStackTrace();
        }

    };

    @Override
    public Map<String, int[]> getAvailableGames() {
        return preGame.getAvailableGames();
    }

    @Override
    public ResponseStatus createGame(String gameID, String username, int numPlayers) {
        ResponseStatus status = preGame.createGame(gameID, numPlayers, username);
        if (status == ResponseStatus.SUCCESS) {
            try {
                this.gameAction = (GameActionStub) registry.lookup(gameID + "/" + username);
            } catch (NotBoundException | RemoteException e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    @Override
    public ResponseStatus connectToGame(String gameID, String username, boolean rejoining){
        ResponseStatus status = ResponseStatus.SUCCESS;
        if(!rejoining) {
            status = preGame.joinGame(gameID, username);
            if (status == ResponseStatus.SUCCESS) {
                try {
                    this.gameAction = (GameActionStub) registry.lookup(gameID + "/" + username);
                } catch (NotBoundException | RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                this.gameAction = (GameActionStub) registry.lookup(gameID + "/" + username);
            } catch (NotBoundException | RemoteException e) {
                status = ResponseStatus.INVALID_REQUEST;
            }
        }

        return status;

    }

    @Override
    public SharedGameState waitTurn(){
        return gameAction.waitTurn();
    }

    @Override
    public SharedGameState selectColumn(int column){
        return gameAction.selectColumn(column);
    }

    @Override
    public SharedGameState pickTile(int x, int y){
        return gameAction.pickTile(x, y);
    }

    @Override
    public SharedGameState reorder(int first, int second, int third){
        return gameAction.reorder(first, second, third);

    }
}
