package it.polimi.ingsw.common.messages.responses;

import java.io.Serial;
import java.io.Serializable;

import it.polimi.ingsw.common.messages.requests.ContentType;
import it.polimi.ingsw.common.messages.requests.PacketContent;

/**
 * This class represents a packet sent from client to server (when using
 * socket).
 * All packets from client to server are instances of this class which are
 * serialized and then deserialized upon receiving.
 *
 * @author Ferrarini Andrea
 */
public class RequestPacket implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;

    public RequestPacket(ContentType contentType, PacketContent content) {
        this.contentType = contentType;
        this.content = content;
    }

    public ContentType contentType;
    public PacketContent content;
}
