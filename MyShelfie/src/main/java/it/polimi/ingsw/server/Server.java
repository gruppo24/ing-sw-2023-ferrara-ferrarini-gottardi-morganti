package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.jrmi.PreGameStubImpl;
import it.polimi.ingsw.server.controller.ReconnectionTimer;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.controller.socket.SockServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;
import java.util.List;

public class Server implements Serializable {

    // Global variables, accessible from anywhere
    public static final List<GameState> GAMES = new LinkedList<>();
    public static final int SOCKET_PORT = 5050;
    public static final int JRMI_PORT = 1059;

    public static final int TIMEOUT_MS = 10_000;

    // Array containing the instantiation of PrivateCards
    public static final PrivateCard[] privateCards = new PrivateCard[12];

    // Array containing the instantiation of CommonCards
    public static final CommonCard[] commonCards = new CommonCard[12];

    private static Registry registry;

    /**
     * Method that populates commonCards array
     * public for testing purposes
     */
    public static void createCommonCards() {
        for (int i = 0; i < commonCards.length; i++) {
            commonCards[i] = CommonCardFactory.cardBuilder(i);
        }
    }

    /**
     * This class fills a PrivateCard array that contains PrivateCard instances
     * The information needed to PrivateCard constructor are read from csv files
     * with the CSVReader
     */
    public static void createPrivateCards() {
        for (int i = 0; i < 12; i++) {
            CSVReader reader = new CSVReader();
            privateCards[i] = reader.getRow(i + 1);
            reader.close();
        }
    }

    public static void main(String[] args) {
        System.out.println("=======================");
        System.out.println("== SETTING UP SERVER ==");
        System.out.println("=======================\n");

        // Set up private card instances
        System.out.print("[main] >>> Creating private card instances");
        createPrivateCards();
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
        System.out.print("[main] >>> Starting jRMI server ---> ");
        try {
            registry = LocateRegistry.createRegistry(JRMI_PORT);
            System.out.println("JRMI server listening on port " + JRMI_PORT);
            PreGameStubImpl preGame = new PreGameStubImpl(registry);
            registry.rebind("remotePreGame", preGame);
        } catch (RemoteException e) {
            System.out.println("COULD NOT START JRMI SERVER");
        }

        System.out.println("[main] >>> Reading backuped games from disk");
        // Get all files in the backup folder
        File folder = new File("backups");
        File[] listOfFiles = folder.listFiles();
        // For each file, try to deserialize it and add it to the list of games
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    try {
                        FileInputStream fileIn = new FileInputStream(file);
                        ObjectInputStream in = new ObjectInputStream(fileIn);
                        GameState game = (GameState) in.readObject();
                        in.close();
                        // GameState game = GameState.deserialize(file.getName());
                        if (game != null) {
                            GAMES.add(game);
                            // Restoring remote players and marking all players as offline
                            game.restoreRemotePlayers();
                            game.setAllPlayersOffline();

                            // Game will automatically be deleted if players don't rejoin within 5 minutes
                            game.reconnectionTimer = new Thread(new ReconnectionTimer(game, 5 * 60 * 1_000));
                            game.reconnectionTimer.start();

                            System.out.println("[main] >>> Game " + game.getGameID() + " restored from disk");
                        }
                    } catch (Exception e) {
                        // for any exception, just consider it not restorable and
                        // delete the file
                        e.printStackTrace();
                        file.delete();
                    }
                }
            }
        } else {
            System.out.println("[main] >>> Folder 'backups' not found");
        }
        System.out.println("[main] >>> Backuped games restored");
    }
}
