package it.polimi.ingsw.client.view.cli;

public enum CLIColor {
    ANSI_BLACK("\u001B[30m"),
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_BLUE("\u001B[34m"),
    ANSI_PURPLE("\u001B[35m"),
    ANSI_CYAN("\u001B[36m"),
    ANSI_WHITE("\u001B[37m"),

    ANSI_BACKGROUND_BLACK("\u001B[40m"),
    ANSI_BACKGROUND_RED("\u001B[41m"),
    ANSI_BACKGROUND_GREEN("\u001B[42m"),
    ANSI_BACKGROUND_YELLOW("\u001B[43m"),
    ANSI_BACKGROUND_BLUE("\u001B[44m"),
    ANSI_BACKGROUND_PURPLE("\u001B[45m"),
    ANSI_BACKGROUND_CYAN("\u001B[46m"),
    ANSI_BACKGROUND_WHITE("\u001B[47m");

    private String ansiCode;

    CLIColor(final String ansiCode) {
        this.ansiCode = ansiCode;
    }

    @Override
    public String toString() {
        return ansiCode;
    }
}
