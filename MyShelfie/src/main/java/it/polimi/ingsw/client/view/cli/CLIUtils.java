package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.common.TileType;

public class CLIUtils {
    // ANSI strings
    private static final String ANSI_RESET = "\u001B[0m";

    private static final String ANSI_BOLD = "\u001B[1m";
    private static final String ANSI_FAINT = "\u001B[2m";
    private static final String ANSI_UNDERLINE = "\u001B[4m";

    private static final String ANSI_CLEAR_SCREEN = "\033[H\033[2J";

    public static String makeBold(String s) {
        return ANSI_BOLD + s + ANSI_RESET;
    }

    public static String makeFaint(String s) {
        return ANSI_FAINT + s + ANSI_RESET;
    }

    public static String makeUnderlined(String s) {
        return ANSI_UNDERLINE + s + ANSI_RESET;
    }

    public static String color(String s, CLIColor color) {
        return color + s + ANSI_RESET;
    }

    public static String tilePickableNext(TileType tileType) {
        return makeBold(tilePickableNext(tileType));
    }

    public static String tileNotPickable(TileType tileType) {
        return switch (tileType) {
            case BOOK -> color(tileType.toString(), CLIColor.ANSI_WHITE);
            case CAT -> color(tileType.toString(), CLIColor.ANSI_GREEN);
            case FRAME -> color(tileType.toString(), CLIColor.ANSI_BLUE);
            case PLANT -> color(tileType.toString(), CLIColor.ANSI_PURPLE);
            case TOY -> color(tileType.toString(), CLIColor.ANSI_YELLOW);
            case TROPHY -> color(tileType.toString(), CLIColor.ANSI_CYAN);
            default -> "";
        };
    }

    public static String tilePickable(TileType tileType) {
        String t = makeBold(tileType.toString());

        return switch (tileType) {
            case BOOK -> color(t, CLIColor.ANSI_BACKGROUND_WHITE);
            case CAT -> color(t, CLIColor.ANSI_BACKGROUND_GREEN);
            case FRAME -> color(t, CLIColor.ANSI_BACKGROUND_BLUE);
            case PLANT -> color(t, CLIColor.ANSI_BACKGROUND_PURPLE);
            case TOY -> color(t, CLIColor.ANSI_BACKGROUND_YELLOW);
            case TROPHY -> color(t, CLIColor.ANSI_BACKGROUND_CYAN);
            default -> "";
        };
    }

    public static void clearScreen() {
        System.out.print(ANSI_CLEAR_SCREEN);
    }
}
