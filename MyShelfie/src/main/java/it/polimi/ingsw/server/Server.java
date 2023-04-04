package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.*;


import it.polimi.ingsw.server.controller.socket.SockServer;
import it.polimi.ingsw.server.model.GameState;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class Server implements Serializable {

    // Global variables, accessible from anywhere
    public static final List<GameState> GAMES = new LinkedList<>();
    public static final int SOCKET_PORT = 5050;

    

    //array containing the instantiation of CommonCards
    public static final CommonCard[] commonCards = new CommonCard[12];

    /**
     * Method that populates commonCards array
     */
    private static void createCommonCards(){
        for(int i=0; i<commonCards.length; i++){
            commonCards[i] = CommonCardFactory.cardBuilder(i);
        }
    }

    public static void main(String[] args) {
        System.out.println("=======================");
        System.out.println("== SETTING UP SERVER ==");
        System.out.println("=======================\n");

        // Set up private card instances
        System.out.print("[main] >>> Creating private card instances");
        // TODO
        System.out.println(" ---> Private card instances created");

        // Set up common card instances
        System.out.print("[main] >>> Creating common card instances");
        createCommonCards();
        System.out.println(" ---> Common card instances created");

        // Start socket server
        System.out.println("[main] >>> Starting socket server - will listen on port " + SOCKET_PORT);
        Thread socketThread = new Thread(new SockServer(SOCKET_PORT));
        socketThread.start();

        // Start jRMI server
        System.out.println("[main] >>> Starting jRMI server - will listen on default port 1059");
    }
}
