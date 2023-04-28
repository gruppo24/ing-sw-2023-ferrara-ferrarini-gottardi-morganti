package it.polimi.ingsw.server.controller.socket;

import it.polimi.ingsw.common.messages.responses.SharedGameState;
import it.polimi.ingsw.server.controller.Contextable;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class in charge of handling all in-game transmissions from server
 * to client.
 *
 * @author Ferrarini Andrea
 */
public class TCPIngameChannelDownlink implements Contextable, Runnable {

    // Reference to the actual output and input channels with the client
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    private final GameState game;
    private final Player player;

    /**
     * Class constructor
     * @param input ObjectInputStream associated to the current uplink channel
     * @param output ObjectOutputStream associated ton the current downlink channel
     * @param game the game to which the client is connected
     * @param player the player associated to this client
     */
    public TCPIngameChannelDownlink(ObjectInputStream input, ObjectOutputStream output, GameState game, Player player) {
        this.input = input;
        this.output = output;

        // Game related attributes
        this.game = game;
        this.player = player;
    }

    @Override
    public ObjectInputStream getInput() {
        return this.input;
    }

    @Override
    public ObjectOutputStream getOutput() {
        return this.output;
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
        // We loop infinitely awaiting game-state updates to forward to the client
        boolean connected = true;
        while (connected) {
            try {
                synchronized (this.game.gameLock) {
                    connected = this.sendSharedGameState();
                    // Whether the game is over or not, we decide to continue looping or break out
                    if (this.game.isGameOver()) break;
                    else this.game.gameLock.wait();
                }
            } catch (InterruptedException ex) {
                System.out.println("[SockServer] INTERRUPTED-EXCEPTION IN DOWNLINK");
                break;
            }
        }

        // As soon as we break out, we immediately close both the uplink and down-link channel
        try { input.close(); } catch (IOException ex) { System.out.println("Error closing ObjectOutputStream"); }
        try { output.close(); } catch (IOException ex) { System.out.println("Error closing ObjectInputStream"); }
    }

    /**
     * Helper method in charge of sending a SharedGameState packet to the client
     *
     * @return a boolean representing whether the channel is still open with the
     *         client
     */
    private boolean sendSharedGameState() {
        SharedGameState gameState = this.game.getSharedGameState(this.player);
        try {
            System.out.print("=== SENDING SGS-'" + this.player.nickname + "' ---> ");
            this.output.writeObject(gameState);
            this.output.flush();
            System.out.println("SGS SENT! ===");
        } catch (IOException ex) {
            System.out.println("[SocketServer] DISCONNECTION IN DOWNLINK");
            return false;
        }
        return true;
    }

}
