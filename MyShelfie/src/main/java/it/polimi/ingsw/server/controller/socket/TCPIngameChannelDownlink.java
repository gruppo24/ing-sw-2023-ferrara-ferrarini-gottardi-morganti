package it.polimi.ingsw.server.controller.socket;

import it.polimi.ingsw.common.messages.SharedGameState;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Class in charge of handling all in-game transmissions from server
 * to client.
 *
 * @author Ferrarini Andrea
 */
public class TCPIngameChannelDownlink implements Contextable, Runnable {

    private final Socket connection;

    private final GameState game;
    private final Player player;

    /**
     * Class constructor
     * @param connection Socket connection with client
     * @param game the game to which the client is connected
     * @param player the player associated to this client
     */
    public TCPIngameChannelDownlink(Socket connection, GameState game, Player player) {
        this.connection = connection;

        // Game related attributes
        this.game = game;
        this.player = player;
    }

    @Override
    public GameState getGame() {
        return this.game;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public void run() {
        // As soon as the down-link is created, we send the current game-state to the client,
        // afterwards we enter an infinite loop awaiting for game-state updates
        this.sendSharedGameState();
        try {
            while (!game.isGameOver()) {
                synchronized (this.game.gameLock) {
                    this.game.gameLock.wait();
                    this.sendSharedGameState();
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Helper method in charge of sending a SharedGameState packet to the client
     */
    private void sendSharedGameState() {
        SharedGameState firstState = this.game.getSharedGameState(this.player);
        try (ObjectOutputStream response = new ObjectOutputStream(connection.getOutputStream())) {
            response.writeObject(firstState);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
