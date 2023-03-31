package it.polimi.ingsw.common.messages;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class is in charge of returning a response status
 * to a client who performed one of the following pre-game
 * requests:
 * 1. JOIN_GAME
 * 2. REJOIN_GAME
 * 3. CREATE_GAME
 *
 * @author Ferrarini Andrea
 */
public class ResponsePacket implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;

    // This attribute will contain response status value. By analysing
    // this attribute one is informed if the performed request resulted
    // in failure (and why) or success
    public ResponseStatus status;
}
