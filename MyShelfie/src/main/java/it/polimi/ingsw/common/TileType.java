package it.polimi.ingsw.common;

/**
 * @author Ferrara Silvia
 *         Enum containing the different types of tiles
 */
public enum TileType {
    BOOK, CAT, FRAME, PLANT, TOY, TROPHY;

    private static final long serialVersionUID = 1L;

    public String getAssetName() {
        return switch (this) {
            case BOOK -> "Libri1.1.png";
            case CAT -> "Gatti1.1.png";
            case FRAME -> "Cornici1.1.png";
            case PLANT -> "Piante1.1.png";
            case TOY -> "Giochi1.1.png";
            case TROPHY -> "Trofei1.1.png";
        };
    }

    public String toString() {
        return switch (this) {
            case BOOK -> "Bk";
            case CAT -> "Ca";
            case FRAME -> "Fr";
            case PLANT -> "Pl";
            case TOY -> "To";
            case TROPHY -> "Ty";
        };
    }
}
