package it.polimi.ingsw.common.messages;

import java.io.Serial;

/**
 * This class is in charge of attaching the required pregame request
 * parameters to a request ==> (Re)joinGame and CreateGame require
 * a game identifier and a username
 *
 * @author Ferrarini Andrea
 */
public class PregameArguments extends PacketContent {
    @Serial
    private final static long serialVersionUID = 1L;

    // The following attributes are used as arguments to a request
    public String gameID;
    public String username;
}
