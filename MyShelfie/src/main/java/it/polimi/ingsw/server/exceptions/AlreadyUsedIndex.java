package it.polimi.ingsw.server.exceptions;

/**
 * This exception class is used whenever a user tries to
 * reorder a selection buffer and provides the same index
 * multiple times.
 * @author Ferrarini Andrea
 */
public class AlreadyUsedIndex extends RuntimeException {

    /**
     * Class constructor
     * @param index the index which triggered the exception
     */
    public AlreadyUsedIndex(int index) {
        super("ERROR: index " + index + " already used in reordering");
    }
}
