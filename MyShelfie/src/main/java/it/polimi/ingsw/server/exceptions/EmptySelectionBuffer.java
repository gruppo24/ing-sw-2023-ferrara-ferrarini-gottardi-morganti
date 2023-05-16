package it.polimi.ingsw.server.exceptions;


/**
 *
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
