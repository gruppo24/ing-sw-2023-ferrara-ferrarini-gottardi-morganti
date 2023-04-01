package it.polimi.ingsw.common.messages;

import java.io.Serial;

/**
 * This class is in charge of forwarding a selectionBuffer reorder
 * request to the server. If the operation is successful, the following
 * request will act as a "DONE" message, that it, it will reorder the
 * player's selection buffer, then move the selected tiles into the
 * player's library. At that point, the player's turn ends
 *
 * @author Ferrarini Andrea
 */
public class Reorder extends PacketContent {
    @Serial
    private final static long serialVersionUID = 1L;

    // The following attributes describe the order which has
    // to be used when moving the selected tiles into the library
    public int firstIndex;
    public int secondIndex;
    public int thirdIndex;
}
