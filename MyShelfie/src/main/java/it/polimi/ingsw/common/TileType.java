package it.polimi.ingsw.common;

/**
 * @author Ferrara Silvia
 *         Enum containing the different types of tiles
 */
public enum TileType {
    BOOK, CAT, FRAME, PLANT, TOY, TROPHY;

    private static final long serialVersionUID = 1L;

    public String toString() {
        return switch (this) {
            case BOOK -> "B";
            case CAT -> "C";
            case FRAME -> "F";
            case PLANT -> "P";
            case TOY -> "T";
            case TROPHY -> "Y";
            default -> " ";
        };
    }
}
