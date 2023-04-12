package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.common.TileType;

public class CLIUtils {
    // ANSI strings
    private static final String ANSI_RESET = "\u001B[0m";

    private static final String ANSI_BOLD = "\u001B[1m";
    private static final String ANSI_UNDERLINE = "\u001B[4m";

    private static final String ANSI_CLEAR_SCREEN = "\033[H\033[2J";

    public static String makeBold(String s) {
        return ANSI_BOLD + s + ANSI_RESET;
    }

    public static String makeUnderlined(String s) {
        return ANSI_UNDERLINE + s + ANSI_RESET;
    }

    public static String color(String s, CLIColor color) {
        return color + s + ANSI_RESET;
    }

    public static void printTileType(TileType tileType) {
        switch (tileType) {
            case BOOK:
                System.out.print(color(tileType.toString(), CLIColor.ANSI_BACKGROUND_WHITE));
                break;
            case CAT:
                System.out.print(color(tileType.toString(), CLIColor.ANSI_BACKGROUND_GREEN));
                break;
            case FRAME:
                System.out.print(color(tileType.toString(), CLIColor.ANSI_BACKGROUND_BLUE));
                break;
            case PLANT:
                System.out.print(color(tileType.toString(), CLIColor.ANSI_BACKGROUND_PURPLE));
                break;
            case TOY:
                System.out.print(color(tileType.toString(), CLIColor.ANSI_BACKGROUND_YELLOW));
                break;
            case TROPHY:
                System.out.print(color(tileType.toString(), CLIColor.ANSI_BACKGROUND_CYAN));
                break;
            default:
                break;
        }
    }

    public static void clearScreen() {
        System.out.print(ANSI_CLEAR_SCREEN);
    }
}
