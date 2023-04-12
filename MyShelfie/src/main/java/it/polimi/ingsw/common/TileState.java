package it.polimi.ingsw.common;

import java.io.Serializable;

/**
 * Enum with the state of each cell on the board, where:
 * - NOT_PICKABLE: cell cannot be picked
 * - PICKABLE: cell can be picked immediately
 * - PICKABLE_NEXT: cell could be picked on subsequent picks
 * 
 * @author Morganti Tommaso
 */
public enum TileState implements Serializable {
    /**
     * Cell cannot be picked
     */
    NOT_PICKABLE,
    /**
     * Cell can be picked immediately
     */
    PICKABLE,
    /**
     * Cell could be picked on subsequent picks
     */
    PICKABLE_NEXT;

    @Override
    public String toString() {
        return switch (this) {
            case NOT_PICKABLE -> "0";
            case PICKABLE_NEXT -> "1";
            case PICKABLE -> "2";
        };
    }
}
