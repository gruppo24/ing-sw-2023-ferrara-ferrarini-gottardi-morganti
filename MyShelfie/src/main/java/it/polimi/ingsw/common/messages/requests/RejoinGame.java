package it.polimi.ingsw.common.messages.requests;

import it.polimi.ingsw.server.controller.socket.Contextable;

import java.io.Serial;

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

    @Override
    public boolean performRequestedAction(Contextable context) {
        return false;
    }
}
