package it.polimi.ingsw.common.messages.requests;

import it.polimi.ingsw.server.controller.socket.Contextable;

import java.io.Serial;

/**
 * This class is in charge of requesting the selection of a column
 * for a player whose turn in the game has come
 *
 * @author Ferrarini Andrea
 */
public class ColumnSelection extends PacketContent {
    @Serial
    private final static long serialVersionUID = 1L;

    // Selected column index
    public int column;

    public ColumnSelection(int column) {
        this.column = column;
    }

    @Override
    public boolean performRequestedAction(Contextable context) {
        context.getPlayer().selectColumn(column);
        return false;
    }
}
