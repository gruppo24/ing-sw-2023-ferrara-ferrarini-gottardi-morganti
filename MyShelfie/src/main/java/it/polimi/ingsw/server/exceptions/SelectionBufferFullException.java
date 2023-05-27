package it.polimi.ingsw.server.exceptions;

/**
 * This exception class is used to report whenever a user tries
 * to insert tiles in an already full column
 * @author Ferrarini Andrea
 */
public class SelectionBufferFullException extends RuntimeException {
    /**
     * Class constructor
     * @param column the selected column which happened to be full
     */
    public SelectionBufferFullException(int column) {
        super("ERROR: selection buffer for " + column + " is already full!");
    }

}
