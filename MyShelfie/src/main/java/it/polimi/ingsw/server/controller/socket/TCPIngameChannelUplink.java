package it.polimi.ingsw.server.controller.socket;

import it.polimi.ingsw.common.messages.RequestPacket;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Class in charge of handling all in-game transmissions coming
 * from client. The class will handle appropriately all requests
 *
 * @author Ferrarini Andrea
 */
public class TCPIngameChannelUplink implements Contextable, Runnable {

    private final Socket connection;

    private final GameState game;
    private final Player player;

    /**
     * Class constructor
     * @param connection Socket connection with client
     * @param game the game to which the client is connected
     * @param player the player associated to this client
     */
    public TCPIngameChannelUplink(Socket connection, GameState game, Player player) {
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
        try (ObjectInputStream request = new ObjectInputStream(connection.getInputStream())) {
            while (this.game.isGameOver()) {
                try {
                    RequestPacket requestPacket = (RequestPacket) request.readObject();
                    // Checking whether it actually is the user's turn. If it isn't, we ignore the request
                    if (this.game.actuallyIsPlayersTurn(this.player))
                        requestPacket.content.performRequestedAction(this);
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
