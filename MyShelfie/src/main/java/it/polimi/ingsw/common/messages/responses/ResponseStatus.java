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
    USERNAME_NOT_IN_GAME, // Tried to rejoin a game, but the provided username doesn't exist

    INVALID_REQUEST, // Catchall error message
    SERVER_ERROR; // Returned when something went wrong at the server's side

    @Serial
    private final static long serialVersionUID = 1L;

    public String toString() {
        return switch (this) {
            case SUCCESS                -> "Operation successful!";
            case GAME_ID_TAKEN          -> "An already used gameID was chosen...";
            case NO_SUCH_GAME_ID        -> "The selected game doesn't exist...";
            case SELECTED_GAME_FULL     -> "The selected game is already full...";
            case USERNAME_TAKEN         -> "This username has already been taken in this game...";
            case USERNAME_NOT_IN_GAME   -> "Can't rejoin with this username...";
            case INVALID_REQUEST        -> "Invalid request";
            case SERVER_ERROR           -> "Server error. Retry later";
            default -> "  ";
        };
    }
}
