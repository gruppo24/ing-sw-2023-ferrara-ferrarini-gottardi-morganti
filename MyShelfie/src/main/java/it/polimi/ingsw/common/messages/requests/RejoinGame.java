package it.polimi.ingsw.common.messages.requests;

import it.polimi.ingsw.common.messages.responses.ResponseStatus;
import it.polimi.ingsw.server.controller.Contextable;
import it.polimi.ingsw.server.controller.socket.TCPIngameChannelDownlink;
import it.polimi.ingsw.server.controller.socket.TCPIngameChannelUplink;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

import java.io.Serial;
import java.util.Optional;

import static it.polimi.ingsw.server.Server.GAMES;
import static it.polimi.ingsw.server.controller.socket.TCPPregameChannel.sendEmptyMessage;

/**
 * This class is in charge of attaching the required pregame request
 * parameters to re-join an existing game after client disconnection
 *
 * @author Ferrarini Andrea
 */
public class RejoinGame extends PacketContent {
    @Serial
    private final static long serialVersionUID = 1L;

    // The following attributes are used as arguments to a request
    public String gameID;
    public String username;

    /**
     * Class constructor
     * @param gameID game to which we want to reconnect
     * @param username username of the user who we want to reconnect to
     */
    public RejoinGame(String gameID, String username) {
        this.gameID = gameID;
        this.username = username;
    }

    @Override
    public boolean performRequestedAction(Contextable context) {
        // At first, we check that the requested game exists
        // Checking the requested game exists:
        Optional<GameState> maybeGame = GAMES.stream().filter((game) -> game.getGameID().equals(this.gameID))
                .findFirst();

        // Checking if the optional returned something or not...
        if (maybeGame.isEmpty()) {
            sendEmptyMessage(context.getOutput(), ResponseStatus.NO_SUCH_GAME_ID);
            return false;
        }
        GameState game = maybeGame.get();

        // Secondly, we check that the requested username exists within the scope
        // of the selected game
        Optional<Player> maybePlayer = game.getUserByUsername(this.username);
        if (maybePlayer.isEmpty()) {
            sendEmptyMessage(context.getOutput(), ResponseStatus.USERNAME_NOT_IN_GAME);
            return false;
        }
        Player player = maybePlayer.get();

        // Send a success response message to the client
        sendEmptyMessage(context.getOutput(), ResponseStatus.SUCCESS);

        // Finally, we re-open the TCPIngameChannels for the client
        TCPIngameChannelUplink uplink = new TCPIngameChannelUplink(context.getInput(), game, player);
        TCPIngameChannelDownlink downlink = new TCPIngameChannelDownlink(context.getInput(), context.getOutput(), game, player);

        Thread clientUplinkChannelThread = new Thread(uplink);
        Thread clientDownlinkChannelThread = new Thread(downlink);

        clientUplinkChannelThread.start();
        clientDownlinkChannelThread.start();

        return true;
    }
}
