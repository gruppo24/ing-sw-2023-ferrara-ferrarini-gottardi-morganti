package it.polimi.ingsw.server.exceptions;

/**
 * This exception class is used to report whenever a selectionBuffer
 * reordering is performed, but unused cells of the buffer are also
 * reordered ==> only used cells (a.k.a. nut null cells) can't be
 * reordered
 * @author Ferrarini Andrea
 */
public class InvalidReorderingIndices extends RuntimeException {
    /**
     * Class constructor
     */
    public InvalidReorderingIndices() {
        // Default error message
        super("ERROR: Indices passed are invalid for current buffer reordering.");
    }

    /**
     * Class constructor
     * @param index buffer index which triggered the exception
     */
    public InvalidReorderingIndices(int index) {
        super("ERROR: index " + index + " can't be used in reordering!");
    }
}
