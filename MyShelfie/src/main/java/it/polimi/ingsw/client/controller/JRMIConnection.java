package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.common.messages.responses.ResponseStatus;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import it.polimi.ingsw.common.stubs.GameActionStub;
import it.polimi.ingsw.common.stubs.PreGameStub;

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

    private Thread keepAliveThread;

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

    /**
     * Method in charge of starting a new keep-alive ping thread
     */
    private void startAsyncKeepAliveEcho() {
        // Start new thread for keep-alive periodic pings
        this.keepAliveThread = new Thread(this::asyncKeepAliveEcho);
        this.keepAliveThread.start();
    }

    @Override
    public void establishConnection() throws RemoteException, NotBoundException {
        // Firstly, reset all in-game parameters (if there are any)
        if (this.keepAliveThread != null) {
            this.keepAliveThread.interrupt();
            this.keepAliveThread = null;
            this.gameAction = null;
        }

        this.registry = LocateRegistry.getRegistry(host, port);
        this.preGame = (PreGameStub) registry.lookup("remotePreGame");
    }

    @Override
    public void asyncKeepAliveEcho(int echoMillisDelay) {
        // As long as, we send keep-alive echoes
        // to the server. When (at the end of a game for instance) the channel
        // closes, we break out of the following loop
        while (true) {
            try {
                gameAction.sendKeepAlive();
//                System.out.println("[DEBUG]: ping sent!");  // DEBUG ONLY

                Thread.sleep(echoMillisDelay);
            } catch (RemoteException e) {
                System.out.println("WARNING: AsyncKeepAliveEcho detected disconnection...");  // DEBUG ONLY
                break;
            } catch (InterruptedException e) {
                System.out.println("=== ASYNC-KEEP-ALIVE-ECHO INTERRUPTED --> LEAVING IN-GAME MODE ===");  // DEBUG ONLY
                break;
            }
        }
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
            cache = gameAction.registerConnection();
            this.startAsyncKeepAliveEcho();
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
                cache = gameAction.registerConnection();
                this.startAsyncKeepAliveEcho();
            }
        } else {
            try {
                this.gameAction = (GameActionStub) registry.lookup(gameID + "/" + username);

                // Register connection and request latest game state
                cache = gameAction.registerConnection();
                this.startAsyncKeepAliveEcho();
            } catch (NotBoundException ex) {
                status = ResponseStatus.INVALID_REQUEST;
            }
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
