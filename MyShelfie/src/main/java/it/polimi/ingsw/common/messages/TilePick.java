package it.polimi.ingsw.common.messages;

import java.io.Serial;

/**
 * This class is in charge of forwarding a tile pick from
 * the game board
 *
 * @author Ferrarini Andrea
 */
public class TilePick extends PacketContent {
    @Serial
    private final static long serialVersionUID = 1L;

    // Coordinates of the picked tile
    public int x;
    public int y;
}
