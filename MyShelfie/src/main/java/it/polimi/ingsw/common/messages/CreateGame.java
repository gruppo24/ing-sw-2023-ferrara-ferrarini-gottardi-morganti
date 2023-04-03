package it.polimi.ingsw.common.messages;

import it.polimi.ingsw.server.controller.socket.Contextable;
import it.polimi.ingsw.server.controller.socket.TCPIngameChannelDownlink;
import it.polimi.ingsw.server.controller.socket.TCPIngameChannelUplink;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

import java.io.Serial;
import java.net.Socket;
import java.util.Optional;

import static it.polimi.ingsw.server.Server.GAMES;
import static it.polimi.ingsw.server.controller.socket.TCPPregameChannel.sendEmptyMessage;

/**
 * This class is in charge of attaching the required pregame request
 * parameters to create a new game
 *
 * @author Ferrarini Andrea
 */
public class CreateGame extends PacketContent {
    @Serial
    private final static long serialVersionUID = 1L;

    // The following attributes are used as arguments to a request
    public String gameID;
    public String username;
    public int numPlayers;

    @Override
    public boolean performRequestedAction(Contextable context) {
        // Prefetching connection object for future use
        Socket connection = context.getConnection();

        // Checking if a game with the requested gameID already exists
        Optional<GameState> maybeGame = GAMES.stream().
                filter((game) -> game.getGameID().equals(this.gameID)).findFirst();

        // Checking if the optional returned something or not...
        if (maybeGame.isPresent()) {
            // If it did contain something, we return an error message
            sendEmptyMessage(connection, ResponseStatus.GAME_ID_TAKEN);
            return false;
        }

        // Send a success response message to the client
        sendEmptyMessage(connection, ResponseStatus.SUCCESS);

        // Otherwise, we actually create a new game
        GameState newGame = new GameState(this.gameID, this.numPlayers);
        GAMES.add(newGame);

        // We create a new user for the client
        Player firstPlayer = new Player(this.username, null);
        newGame.addNewPlayerToGame(firstPlayer);

        // And, finally, create the actual in-game full-duplex TCP channel
        Thread clientUplinkChannelThread = new Thread(new TCPIngameChannelUplink(connection, newGame, firstPlayer));
        Thread clientDownlinkChannelThread = new Thread(new TCPIngameChannelDownlink(connection, newGame, firstPlayer));
        clientUplinkChannelThread.start();
        clientDownlinkChannelThread.start();

        return true;
    }
}
