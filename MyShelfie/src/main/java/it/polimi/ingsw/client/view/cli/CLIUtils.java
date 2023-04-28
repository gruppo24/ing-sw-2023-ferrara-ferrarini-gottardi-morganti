package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.common.TileType;

import java.util.Scanner;

public class CLIUtils {
    // ANSI strings
    private static final String ANSI_RESET = "\u001B[0m";

    private static final String ANSI_BOLD = "\u001B[1m";
    private static final String ANSI_FAINT = "\u001B[2m";
    private static final String ANSI_ITALIC = "\u001B[3m";
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

    public static String makeItalic(String s) {
        return ANSI_ITALIC + s + ANSI_RESET;
    }

    public static String color(String s, CLIColor color) {
        return color + s + ANSI_RESET;
    }

    private static String tileColor(TileType tileType) {
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

    public static String tilePickableNext(TileType tileType) {
        return makeUnderlined(makeBold(tileColor(tileType)));
    }

    public static String tileNotPickable(TileType tileType) {
        return makeItalic(tileColor(tileType));
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

    /**
     * Method in charge of reading from a Scanner inputs as long as the user doesn't
     * insert a valid integer
     * @param s Scanner to use for reading
     * @return the valid integer inserted by the user
     */
    public static int safeNextInt(Scanner s) {
        // Loop as long as the user doesn't insert a valid integer
        while (!s.hasNextInt()) {
            String invalid = s.next();
            System.out.println("'" + invalid + "' IS NOT A VALID INTEGER");
            System.out.print("Retry: ");
        }

        // Finally, return inserted value
        return s.nextInt();
    }
}
