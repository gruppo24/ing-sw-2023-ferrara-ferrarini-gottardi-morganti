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

    @Override
    public boolean performRequestedAction(Contextable context) {
        // Checking the requested game exists:
        Optional<GameState> maybeGame = GAMES.stream().
                filter((game) -> game.getGameID().equals(this.gameID)).findFirst();

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
        if (game.usernameAlreadyUsed(this.username)) {
            sendEmptyMessage(context.getOutput(), ResponseStatus.USERNAME_TAKEN);
            return false;
        }

        // If we reach this point, we create a new player for the selected game
        Player newPlayer = new Player(this.username, null);
        game.addNewPlayerToGame(newPlayer);

        // Send a success response message to the client
        sendEmptyMessage(context.getOutput(), ResponseStatus.SUCCESS);

        // And, finally, create the actual in-game full-duplex TCP channel
        Thread clientUplinkChannelThread = new Thread(
                new TCPIngameChannelUplink(context.getInput(), game, newPlayer)
        );
        Thread clientDownlinkChannelThread = new Thread(
                new TCPIngameChannelDownlink(context.getOutput(), game, newPlayer)
        );
        clientUplinkChannelThread.start();
        clientDownlinkChannelThread.start();

        return true;
    }
}
