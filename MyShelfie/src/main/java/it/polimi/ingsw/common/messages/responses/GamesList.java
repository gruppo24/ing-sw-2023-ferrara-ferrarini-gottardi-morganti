package it.polimi.ingsw.common.messages.responses;

import java.io.Serial;
import java.util.Map;

/**
 * This class is in charge of returning to a client the list
 * of all currently available games
 *
 * @author Ferrarini Andrea
 */
public class GamesList extends ResponsePacket {
    @Serial
    private final static long serialVersionUID = 1L;

    // When a client requests the list of all available games,
    // the following map is returned. The map will have as keys
    // the gameIDs of each online game, and two integers as value:
    // the number of players who have already joined the game and
    // the total number of players for that game
    public Map<String, int[]> availableGames;
}
