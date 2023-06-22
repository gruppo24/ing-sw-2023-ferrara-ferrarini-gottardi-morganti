package it.polimi.ingsw.client;


import it.polimi.ingsw.client.view.cli.CLI;


/**
 * Main CLI entrypoint class
 */
public class Client {

    // Connection global (readonly) variables
    public static String SERVER_ADDR = "127.0.0.1";
    public static final int SOCKET_PORT = 5050;
    public static final int JRMI_PORT = 1059;


    public static void main(String[] args) {
        // Choose and establish connection
        CLI cli = new CLI();

        // Enter game loop --> lobby will automatically redirect to game eventually
        boolean exit = false;
        while (!exit)
            exit = cli.menu();
        System.exit(0);
    }
}
