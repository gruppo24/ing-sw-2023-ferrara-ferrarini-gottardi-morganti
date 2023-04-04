package it.polimi.ingsw.common.messages.responses;

import java.io.Serial;

/**
 * Following enum contains the possible server response statuses to pregame
 * requests
 *
 * @author Ferrarini Andrea
 */
public enum ResponseStatus {
    SUCCESS, // Operation performed correctly

    GAME_ID_TAKEN, // Tried to create a game, but provided gameID was already taken
    NO_SUCH_GAME_ID, // Tried to join a game using a non-existent gameID
    SELECTED_GAME_FULL, // Tried to join a game which wasn't awaiting for any more players

    USERNAME_TAKEN, // Tried to join a game with a username which was already taken by someone else

    INVALID_REQUEST, // Catchall error message
    SERVER_ERROR; // Returned when something went wrong at the server's side

    @Serial
    private final static long serialVersionUID = 1L;
}
