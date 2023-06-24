package it.polimi.ingsw.client;


import it.polimi.ingsw.client.view.cli.CLI;
import java.util.Scanner;
import static it.polimi.ingsw.client.App.startGUI;


/**
 * Main CLI entrypoint class
 */
public class Client {

    // Connection global (readonly) variables
    public static String SERVER_ADDR = "127.0.0.1";
    public static final int SOCKET_PORT = 5050;
    public static final int JRMI_PORT = 1059;


    public static void main(String[] args) {
        System.out.print("=== Select a mode: CLI (c) / GUI (g) ===\n> ");

        // Asking the user to select an interface mode
        Scanner s = new Scanner(System.in);
        char value = s.next().charAt(0);

        switch (value) {
            case 'c' -> cli();
            case 'g' -> gui(args);

            default -> System.out.println("Invalid choice...");
        }
    }

    /**
     * Function in charge of starting and handling the CLI finite state machine
     */
    public static void cli() {
        // Choose and establish connection
        CLI cli = new CLI();

        // Enter game loop --> lobby will automatically redirect to game eventually
        boolean exit = false;
        while (!exit)
            exit = cli.menu();
        System.exit(0);
    }

    /**
     * Function in charge of starting the javafx GUI
     *
     * @param args command line arguments to be forwarded to javafx
     */
    public static void gui(String[] args) {
        startGUI(args); // We simply launch our GUI...
    }
}
