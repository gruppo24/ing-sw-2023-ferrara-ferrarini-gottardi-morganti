package it.polimi.ingsw.common.messages.requests;

import it.polimi.ingsw.server.controller.socket.Contextable;
import it.polimi.ingsw.server.exceptions.AlreadyUsedIndex;
import it.polimi.ingsw.server.exceptions.InvalidReorderingIndices;

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

    public Reorder(int firstIndex, int secondIndex, int thirdIndex) {
        this.firstIndex = firstIndex;
        this.secondIndex = secondIndex;
        this.thirdIndex = thirdIndex;
    }

    @Override
    public boolean performRequestedAction(Contextable context) {
        try {
            context.getPlayer().reorderSelectionBuffer(this.firstIndex, this.secondIndex, this.thirdIndex);
            context.getPlayer().flushBufferIntoLibrary();
            context.getGame().turnIsOver();
            // No need to notify the gameLock: GameState::turnIsOver will already do o for us
        } catch (AlreadyUsedIndex | InvalidReorderingIndices | IndexOutOfBoundsException ex) {
            System.out.println("---> Error during reordering for " + context.getPlayer().nickname + ": " + ex);
            // We wake up the threads here, since turnIsOver hasn't done so for us in case of an exception
            synchronized (context.getGame().gameLock) { context.getGame().gameLock.notifyAll(); }
        }

        return false;
    }
}
