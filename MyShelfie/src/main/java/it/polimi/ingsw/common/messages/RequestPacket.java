package it.polimi.ingsw.common.messages;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents a packet sent from client to server (when using socket).
 * All packets from client to server are instances of this class which are
 * serialized and then deserialized upon receiving.
 *
 * @author Ferrarini Andrea
 */
public class RequestPacket implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;

    public ContentType contentType;
    public PacketContent content;
}
