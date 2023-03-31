package it.polimi.ingsw.common.messages;

import java.io.Serial;
import java.io.Serializable;


/**
 * This class serves as a placeholder for different content types
 * sent from client to server. Each subclass of this class will
 * contain the appropriate attributes for each request. Explicit
 * cast from PacketContent to the appropriate class is required.
 *
 * @author Ferrarini Andrea
 */
public class PacketContent implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;
}
