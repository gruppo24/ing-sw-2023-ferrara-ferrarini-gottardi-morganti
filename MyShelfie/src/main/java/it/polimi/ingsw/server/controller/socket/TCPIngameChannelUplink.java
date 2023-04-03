package it.polimi.ingsw.server.controller.socket;

import it.polimi.ingsw.common.messages.RequestPacket;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Class in charge of handling all in-game transmissions coming
 * from client. The class will handle appropriately all requests
 *
 * @author Ferrarini Andrea
 */
public class TCPIngameChannelUplink implements Contextable, Runnable {

    // Reference to the actual output and input channels with the client
    private final ObjectInputStream input;

    private final GameState game;
    private final Player player;

    /**
     * Class constructor
     * @param input ObjectInputStream associated to the current uplink channel
     * @param game the game to which the client is connected
     * @param player the player associated to this client
     */
    public TCPIngameChannelUplink(ObjectInputStream input, GameState game, Player player) {
        this.input = input;

        // Game related attributes
        this.game = game;
        this.player = player;
    }

    @Override
    public ObjectInputStream getInput() { return this.input; }

    @Override
    public ObjectOutputStream getOutput() { return null; }

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
        while (this.game.isGameOver()) {
            try {
                RequestPacket requestPacket = (RequestPacket) this.input.readObject();
                // Checking whether it actually is the user's turn. If it isn't, we ignore the request
                if (this.game.actuallyIsPlayersTurn(this.player))
                    requestPacket.content.performRequestedAction(this);
            } catch (ClassNotFoundException | IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
