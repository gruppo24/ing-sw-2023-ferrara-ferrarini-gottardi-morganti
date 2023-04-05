package it.polimi.ingsw.client;

import java.io.IOException;
import java.net.UnknownHostException;

import it.polimi.ingsw.client.view.CLI;

public class Client {
    public static void main(String[] args) throws UnknownHostException, IOException {
        CLI cli = new CLI();
        cli.menu();
    }
}
