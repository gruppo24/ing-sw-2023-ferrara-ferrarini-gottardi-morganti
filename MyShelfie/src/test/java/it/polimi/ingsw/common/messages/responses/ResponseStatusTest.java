package it.polimi.ingsw.common.messages.responses;


import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ResponseStatusTest {

    @Test
    public void toString_forEachResponse_returnsCorrectMessage() {
        assertEquals(ResponseStatus.SUCCESS.toString(), "Operation successful!");
        assertEquals(ResponseStatus.GAME_ID_TAKEN.toString(), "An already used gameID was chosen...");
        assertEquals(ResponseStatus.NO_SUCH_GAME_ID.toString(), "The selected game doesn't exist...");
        assertEquals(ResponseStatus.SELECTED_GAME_FULL.toString(), "The selected game is already full...");
        assertEquals(ResponseStatus.USERNAME_TAKEN.toString(), "This username has already been taken in this game...");
        assertEquals(ResponseStatus.USERNAME_NOT_IN_GAME.toString(), "Can't rejoin with this username...");
        assertEquals(ResponseStatus.INVALID_REQUEST.toString(), "Invalid request");
        assertEquals(ResponseStatus.SERVER_ERROR.toString(), "Server error. Retry later");
    }

}
