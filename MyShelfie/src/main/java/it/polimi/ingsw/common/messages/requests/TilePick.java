package it.polimi.ingsw.common.messages.requests;

import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.controller.socket.Contextable;
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
        Player player = context.getPlayer();
        // NOTICE: we subtract 1 from player.getSelectionBufferSize() because we are about to
        // push a new tile in the buffer
        TileType pickedTile = context.getGame().getBoard().pick(this.x, this.y, player.getSelectionBufferSize()-1);
        player.pushTileToSelectionBuffer(pickedTile);
        return false;
    }
}
