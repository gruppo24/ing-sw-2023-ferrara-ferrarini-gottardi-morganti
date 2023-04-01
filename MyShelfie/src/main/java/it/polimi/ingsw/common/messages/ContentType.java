package it.polimi.ingsw.common.messages;

import java.io.Serial;

/**
 * This enum is required to specify what content is provided
 * along with the client request message.
 *
 * @author Ferrarini Andrea
 */
public enum ContentType {
    LIST_GAMES,         // Requests the list of all currently available games
    JOIN_GAME,          // Requests to join a game
    REJOIN_GAME,        // Requests to re-join (after disconnection) a game
    CREATE_GAME,        // Requests to create a new game

    SELECT_COLUMN,      // While player's turn, selects a column for tile insertion
    PICK_TILE,          // While player's turn, picks a tile from the board
    REORDER;            // Before inserting tiles in the library, allows to specify an order

    @Serial
    private final static long serialVersionUID = 1L;
}
