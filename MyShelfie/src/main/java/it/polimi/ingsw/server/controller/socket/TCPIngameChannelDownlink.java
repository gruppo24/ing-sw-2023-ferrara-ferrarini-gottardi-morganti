package it.polimi.ingsw.server.controller.socket;

import it.polimi.ingsw.common.messages.responses.SharedGameState;
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

    // Reference to the uplink thread
    private final Thread uplinkThread;

    private final GameState game;
    private final Player player;

    /**
     * Class constructor
     * 
     * @param input        ObjectInputStream associated to the current uplink
     *                     channel
     * @param output       ObjectOutputStream associated ton the current downlink
     *                     channel
     * @param uplinkThread a reference to the TCPIngameChannelUplink thread
     * @param game         the game to which the client is connected
     * @param player       the player associated to this client
     */
    public TCPIngameChannelDownlink(ObjectInputStream input, ObjectOutputStream output, Thread uplinkThread,
            GameState game, Player player) {
        this.input = input;
        this.output = output;

        this.uplinkThread = uplinkThread;

        // Game related attributes
        this.game = game;
        this.player = player;
    }

    @Override
    public ObjectInputStream getInput() {
        return null;
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
        // We loop infinitely awaiting for game-state updates to forward to the client
        boolean connected = true;
        while (!game.isGameOver() || connected) {
            try {
                synchronized (this.game.gameLock) {
                    connected = this.sendSharedGameState();
                    this.game.gameLock.wait();
                }
            } catch (InterruptedException ex) {
                System.out.println("[SockServer] INTERRUPTED-EXCEPTION IN DOWNLINK");
                break;
            }
        }

        // If we broke out of the loop because of game-ending we reopen a pregame
        // channel for the client
        if (game.isGameOver()) {
            Thread pregameChannel = new Thread(new TCPPregameChannel(this.input, this.output));
            pregameChannel.start();
        }

        // Otherwise, the TCP channel simply closes...
        this.uplinkThread.interrupt();
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
