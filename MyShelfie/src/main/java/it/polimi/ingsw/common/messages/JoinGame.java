package it.polimi.ingsw.common.messages;

import it.polimi.ingsw.server.controller.socket.Contextable;

import java.io.Serial;

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
    public boolean performRequestedAction(Contextable context) { return false; }
}
