package it.polimi.ingsw.client;

import java.io.IOException;
import java.net.UnknownHostException;

import it.polimi.ingsw.client.view.cli.CLI;

public class Client {

    // Connection global (readonly) variables
    public static final String SERVER_ADDR = "127.0.0.1";
    public static final int SOCKET_PORT = 5050;
    public static final int JRMI_PORT = 1059;

    public static void main(String[] args) throws UnknownHostException, IOException {
        CLI cli = new CLI();
        cli.menu();
    }
}
