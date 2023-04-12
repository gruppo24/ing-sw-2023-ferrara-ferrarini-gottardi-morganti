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
            case BOOK -> "Bk";
            case CAT -> "Ca";
            case FRAME -> "Fr";
            case PLANT -> "Pl";
            case TOY -> "To";
            case TROPHY -> "Ty";
            default -> "  ";
        };
    }
}
