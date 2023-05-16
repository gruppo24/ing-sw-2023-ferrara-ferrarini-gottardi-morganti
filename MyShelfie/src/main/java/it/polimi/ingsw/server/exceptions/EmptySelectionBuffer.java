package it.polimi.ingsw.server.exceptions;


/**
 * This exception class is used whenever a user tries to
 * reorder a selection buffer that hasn't ever been filled.
 * @author Ferrarini Andrea
 */
public class EmptySelectionBuffer extends RuntimeException {

    /**
     * Class constructor
     */
    public EmptySelectionBuffer() {
        // Default error message
        super("ERROR: tried to reorder empty selection buffer");
    }

}
