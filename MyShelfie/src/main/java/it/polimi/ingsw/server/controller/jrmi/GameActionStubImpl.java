package it.polimi.ingsw.server.controller.jrmi;

import it.polimi.ingsw.common.messages.responses.SharedGameState;
import it.polimi.ingsw.common.stubs.GameActionStub;
import it.polimi.ingsw.server.controller.Backupper;
import it.polimi.ingsw.server.controller.Middleware;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


/**
 * GameActionStubImpl for JRMI
 *
 * @author Ferrara Silvia
 */
public class GameActionStubImpl extends UnicastRemoteObject implements GameActionStub {

    private final GameState game;
    private final Player player;

    /**
     * Class constructor
     * @param game game associated with this remote player
     * @param player player associated with this remote player
     * @throws RemoteException
     */
    public GameActionStubImpl(GameState game, Player player) throws RemoteException {
        super();
        this.game = game;
        this.player = player;
    }

    @Override
    public SharedGameState getSharedGameStateImmediately() throws RemoteException{
        return game.getSharedGameState(this.player);
    }

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
