package it.polimi.ingsw.server.controller.jrmi;

import it.polimi.ingsw.common.messages.responses.SharedGameState;
import it.polimi.ingsw.common.stubs.GameActionStub;
import it.polimi.ingsw.server.controller.Backupper;
import it.polimi.ingsw.server.controller.Middleware;
import it.polimi.ingsw.server.controller.socket.ReconnectionTimer;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import static it.polimi.ingsw.server.Server.TIMEOUT_MS;


/**
 * GameActionStubImpl for jRMI
 *
 * @author Ferrara Silvia
 */
public class GameActionStubImpl extends UnicastRemoteObject implements GameActionStub {

    private final GameState game;
    private final Player player;

    private Thread timeoutTimer;

    /**
     * Class constructor
     *
     * @param game game associated with this remote player
     * @param player player associated with this remote player
     * @throws RemoteException if jRMI exception
     */
    public GameActionStubImpl(GameState game, Player player) throws RemoteException {
        super();
        this.game = game;
        this.player = player;
    }

    /**
     * Method in charge of appropriately handling a player's reconnection,
     * especially when a game reconnection-timer has been started
     */
    public void resetDisconnectionTimer() {
        // We update the players connection status
        player.hasReconnected();

        // We stop the reconnection timer associated to the game
        if (game.reconnectionTimer != null && game.remainingOnline() > 1) {
            game.reconnectionTimer.interrupt();
            game.reconnectionTimer = null;
        }

        // If the game had been suspended, we resume it
        if (game.isSuspended()) {
            game.turnIsOver();
        }
    }

    /**
     * Method in charge of handling IDLE timeouts, marking a player
     * as disconnected if required
     */
    private void timeoutManager() {
        try {
            // We wait for TIMEOUT_MS milliseconds...
            Thread.sleep(TIMEOUT_MS);

            // ... if the timer elapses, we mark the player as disconnected
            player.hasDisconnected();

            // If only one player has remained online, we start a reconnection timer
            if (this.game.remainingOnline() == 1) {
                this.game.reconnectionTimer = new Thread(new ReconnectionTimer(this.game));
                this.game.reconnectionTimer.start();
            }

            // If it was the player's turn, change turn
            if (this.game.actuallyIsPlayersTurn(this.player)) {
                game.turnIsOver();
            }
        } catch (InterruptedException ex) {
            // ...if the timer is interrupted, we've received an echo packet --> client still online
//            System.out.println("jRMI PING RECEIVED from " + player.nickname + " --> TIMEOUT TIMER RESET");  // DEBUG ONLY
        }
    }


    /** @see GameActionStub#registerConnection() */
    @Override
    public SharedGameState registerConnection() throws RemoteException{
        // As a first thing, we reset any potential game reconnection-timers:
        this.resetDisconnectionTimer();

        // Then, register a connection by starting a new timeout-timer for the client
        timeoutTimer = new Thread(this::timeoutManager);
        timeoutTimer.start();

        // Finally, we send back a first SharedGameState to the player
        return game.getSharedGameState(this.player);
    }

    /** @see GameActionStub#sendKeepAlive() */
    @Override
    public void sendKeepAlive() throws RemoteException {
        // Interrupt current timeout timer
        if (timeoutTimer != null) {
            timeoutTimer.interrupt();
            timeoutTimer = null;
        }

        // Start new timeout timer
        timeoutTimer = new Thread(this::timeoutManager);
        timeoutTimer.start();
    }

    /** @see GameActionStub#waitTurn() */
    @Override
    public SharedGameState waitTurn() throws RemoteException{
        synchronized (this.game.gameLock) {
            try {
                this.game.gameLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return game.getSharedGameState(this.player);
    }

    /** @see GameActionStub#selectColumn(int)  */
    @Override
    public SharedGameState selectColumn(int column) throws RemoteException{
        // Checking if it actually is the player's turn
        if (this.game.actuallyIsPlayersTurn(this.player)) {
            Middleware.doSelectColumn(this.game, this.player, column);

            // Storing game state to disk
            Thread backupThread = new Thread(new Backupper(this.game));
            backupThread.start();
        }

        return game.getSharedGameState(this.player);
    }

    /** @see GameActionStub#pickTile(int, int) */
    @Override
    public SharedGameState pickTile(int x, int y) throws RemoteException{
        // Checking if it actually is the player's turn
        if (this.game.actuallyIsPlayersTurn(this.player)) {
            Middleware.doPickTile(this.game, this.player,x, y);

            // Storing game state to disk
            Thread backupThread = new Thread(new Backupper(this.game));
            backupThread.start();
        }

        return game.getSharedGameState(this.player);
    }
    
    /** @see GameActionStub#reorder(int, int, int) */
    @Override
    public SharedGameState reorder(int first, int second, int third) throws RemoteException{
        // Checking if it actually is the player's turn
        if (this.game.actuallyIsPlayersTurn(this.player)) {
            Middleware.doReorder(this.game, this.player, first, second, third);

            // Storing game state to disk
            Thread backupThread = new Thread(new Backupper(this.game));
            backupThread.start();
        }

        return game.getSharedGameState(this.player);
    }
}
