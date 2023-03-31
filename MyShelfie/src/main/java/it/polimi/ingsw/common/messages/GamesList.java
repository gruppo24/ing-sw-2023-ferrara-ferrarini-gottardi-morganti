package it.polimi.ingsw.common.messages;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * This class is in charge of forwarding server responses to
 * a client while the they still are in pregame (game selection,
 * game creation, game rejoin after crash)
 *
 * @author Ferrarini Andrea
 */
public class GamesList implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;

    // When a client requests the list of all available games,
    // the following map is returned. The map will have as keys
    // the gameIDs of each online game, and two integers as value:
    // the number of players who have already joined the game and
    // the total number of players for that game
    public Map<String, int[]> availableGames;

    // This attribute will contain response status value. By analysing
    // this attribute one is informed if the performed request resulted
    // in failure (and why) or success
    public ResponseStatus status;
}
