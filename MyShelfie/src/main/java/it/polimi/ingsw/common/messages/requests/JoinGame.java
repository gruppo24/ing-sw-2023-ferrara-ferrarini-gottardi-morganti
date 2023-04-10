package it.polimi.ingsw.common.messages.requests;

import it.polimi.ingsw.common.messages.responses.ResponseStatus;
import it.polimi.ingsw.server.controller.socket.Contextable;
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
 * parameters to join an existing game
 *
 * @author Ferrarini Andrea
 */
public class JoinGame extends PacketContent {
    @Serial
    private final static long serialVersionUID = 1L;

    // The following attributes are used as arguments to a request
    public String gameID;
    public String username;

    /**
     * Class constructor
     * 
     * @param gameID   the ID of the game to join
     * @param username the username to use in the game
     */
    public JoinGame(String gameID, String username) {
        this.gameID = gameID;
        this.username = username;
    }

    @Override
    public boolean performRequestedAction(Contextable context) {
        // Checking the requested game exists:
        Optional<GameState> maybeGame = GAMES.stream().filter((game) -> game.getGameID().equals(this.gameID))
                .findFirst();

        // Checking if the optional returned something or not...
        if (maybeGame.isEmpty()) {
            sendEmptyMessage(context.getOutput(), ResponseStatus.NO_SUCH_GAME_ID);
            return false;
        }
        GameState game = maybeGame.get();

        // Checking the game has space for a new player
        int[] playerSituation = game.getPlayerStatus();
        if (playerSituation[0] == playerSituation[1]) {
            sendEmptyMessage(context.getOutput(), ResponseStatus.SELECTED_GAME_FULL);
            return false;
        }

        // Finally, checking the username requested is unique
        Optional<Player> maybePlayer = game.getUserByUsername(this.username);
        if (maybePlayer.isPresent()) {
            sendEmptyMessage(context.getOutput(), ResponseStatus.INVALID_REQUEST);
            return false;
        }

        // If we reach this point, we create a new player for the selected game
        Player newPlayer = new Player(this.username);
        game.addNewPlayerToGame(newPlayer);

        // Send a success response message to the client
        sendEmptyMessage(context.getOutput(), ResponseStatus.SUCCESS);

        // And, finally, create the actual in-game full-duplex TCP channel
        TCPIngameChannelUplink uplink = new TCPIngameChannelUplink(context.getInput(), game, newPlayer);
        Thread clientUplinkChannelThread = new Thread(uplink);
        clientUplinkChannelThread.start();

        TCPIngameChannelDownlink downlink = new TCPIngameChannelDownlink(
                context.getInput(), context.getOutput(), clientUplinkChannelThread, game, newPlayer
        );
        Thread clientDownlinkChannelThread = new Thread(downlink);
        clientDownlinkChannelThread.start();

        return true;
    }
}
