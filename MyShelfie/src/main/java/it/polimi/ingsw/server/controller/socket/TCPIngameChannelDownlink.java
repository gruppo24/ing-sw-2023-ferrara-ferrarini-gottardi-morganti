package it.polimi.ingsw.server.controller.socket;

import it.polimi.ingsw.common.messages.ResponseStatus;
import it.polimi.ingsw.common.messages.SharedGameState;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Class in charge of handling all in-game transmissions from server
 * to client.
 *
 * @author Ferrarini Andrea
 */
public class TCPIngameChannelDownlink implements Contextable, Runnable {

    // Reference to the actual output and input channels with the client
    private final ObjectOutputStream output;

    private final GameState game;
    private final Player player;

    /**
     * Class constructor
     * @param output ObjectOutputStream associated ton the current downlink channel
     * @param game the game to which the client is connected
     * @param player the player associated to this client
     */
    public TCPIngameChannelDownlink(ObjectOutputStream output, GameState game, Player player) {
        this.output = output;

        // Game related attributes
        this.game = game;
        this.player = player;
    }

    @Override
    public ObjectInputStream getInput() { return null; }

    @Override
    public ObjectOutputStream getOutput() { return this.output; }

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
        try {
            this.output.writeObject(firstState);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
