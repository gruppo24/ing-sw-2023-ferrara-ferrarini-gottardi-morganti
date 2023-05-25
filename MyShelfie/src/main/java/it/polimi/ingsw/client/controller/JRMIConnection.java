package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.common.messages.responses.ResponseStatus;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import it.polimi.ingsw.common.stubs.GameActionStub;
import it.polimi.ingsw.common.stubs.PreGameStub;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

/**
 * Class that represents the connection via JRMI, see {@link Connection}
 *
 * @author Gottardi Arianna
 */

public class JRMIConnection extends Connection{

    private final String host;
    private final int port;

    private Registry registry;
    private PreGameStub preGame;
    private GameActionStub gameAction;

    private SharedGameState cache;

    /**
     * Class constructor
     *
     * @param host server address
     * @param port socket port
     */
    public JRMIConnection(String host, int port) {
        super();
        this.host = host;
        this.port = port;
    }

    @Override
    public void establishConnection() throws RemoteException, NotBoundException {
        this.registry = LocateRegistry.getRegistry(host, port);
        this.preGame = (PreGameStub) registry.lookup("remotePreGame");
    }

    @Override
    public Map<String, int[]> getAvailableGames() throws RemoteException {
        return preGame.getAvailableGames();
    }

    @Override
    public ResponseStatus createGame(String gameID, String username, int numPlayers) throws RemoteException, NotBoundException {
        ResponseStatus status = preGame.createGame(gameID, numPlayers, username);

        if (status == ResponseStatus.SUCCESS) {
            this.gameAction = (GameActionStub) registry.lookup(gameID + "/" + username);
            cache = gameAction.getSharedGameStateImmediately();
        }
        return status;
    }

    @Override
    public ResponseStatus connectToGame(String gameID, String username, boolean rejoining) throws RemoteException, NotBoundException {
        ResponseStatus status = ResponseStatus.SUCCESS;
        if(!rejoining) {
            status = preGame.joinGame(gameID, username);

            if (status == ResponseStatus.SUCCESS) {
                this.gameAction = (GameActionStub) registry.lookup(gameID + "/" + username);
                cache = gameAction.getSharedGameStateImmediately();
            }
        } else {
            this.gameAction = (GameActionStub) registry.lookup(gameID + "/" + username);

            // We make sure any disconnection timer is immediately reset and only afterwards request latest game state
            this.gameAction.resetDisconnectionTimer();
            cache = gameAction.getSharedGameStateImmediately();
        }

        return status;
    }

    @Override
    public SharedGameState waitTurn() throws RemoteException {
        if (cache != null) {
            SharedGameState tmp = cache;
            cache = null;
            return tmp;
        }

        return gameAction.waitTurn();
    }

    @Override
    public SharedGameState selectColumn(int column) throws RemoteException {
        return gameAction.selectColumn(column);
    }

    @Override
    public SharedGameState pickTile(int x, int y) throws RemoteException {
        return gameAction.pickTile(x, y);
    }

    @Override
    public SharedGameState reorder(int first, int second, int third) throws RemoteException {
        return gameAction.reorder(first, second, third);
    }
}
