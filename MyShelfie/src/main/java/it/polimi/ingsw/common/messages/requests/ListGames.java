package it.polimi.ingsw.common.messages.requests;

import it.polimi.ingsw.common.messages.responses.GamesList;
import it.polimi.ingsw.common.messages.responses.ResponseStatus;
import it.polimi.ingsw.server.controller.Contextable;
import it.polimi.ingsw.server.model.GameState;

import java.io.IOException;
import java.io.Serial;
import java.util.HashMap;

import static it.polimi.ingsw.server.Server.GAMES;

/**
 * This class is in charge requesting all currently available games
 *
 * @author Ferrarini Andrea
 */
public class ListGames extends PacketContent {
    @Serial
    private final static long serialVersionUID = 1L;

    @Override
    public boolean performRequestedAction(Contextable context) {
        // Constructing response object
        GamesList responsePacket = new GamesList();
        responsePacket.availableGames = new HashMap<>();
        for (GameState game : GAMES) {
            int[] status = game.getPlayerStatus();
            // Only returning games which still have space for new players
            if (status[0] < status[1]) responsePacket.availableGames.put(game.getGameID(), status);
        }
        responsePacket.status = ResponseStatus.SUCCESS;

        // Serializing response object and sending it back to the client
        try {
            context.getOutput().writeObject(responsePacket);
            context.getOutput().flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }
}