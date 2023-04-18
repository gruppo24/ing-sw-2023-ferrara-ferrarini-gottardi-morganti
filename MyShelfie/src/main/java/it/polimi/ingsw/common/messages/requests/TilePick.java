package it.polimi.ingsw.common.messages.requests;

import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.controller.Contextable;
import it.polimi.ingsw.server.controller.Middleware;
import it.polimi.ingsw.server.model.Player;

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

    public TilePick(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean performRequestedAction(Contextable context) {
        Middleware.doPickTile(context.getGame(), context.getPlayer(), this.x, this.y);
        return false;
    }
}
