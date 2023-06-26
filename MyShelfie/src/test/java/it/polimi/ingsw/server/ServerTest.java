package it.polimi.ingsw.server;


import it.polimi.ingsw.client.controller.Connection;
import it.polimi.ingsw.client.controller.JRMIConnection;
import it.polimi.ingsw.client.controller.SocketConnection;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;


public class ServerTest {

    private static Connection socketClient;
    private static Connection jRMIClient;

    @BeforeClass
    public static void setUpClass() {
        Server.main(new String[] {});
    }

    @Before
    public void setUp() {
        socketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        jRMIClient = new JRMIConnection("localhost", Server.JRMI_PORT);
    }

    @AfterClass
    public static void tearDownClass() {
        // Remove any potential game backups created
        File[] backups = (new File("backups")).listFiles();
        if (backups != null)
            for (File backup : backups)
                if (!backup.delete())
                    System.out.println("ERROR DELETING A BACKUP-FILE");
    }

    @Test
    public void socketClient_getAvailableGames() throws InterruptedException, IOException, NotBoundException {
        // Establishing connection
        socketClient.establishConnection();

        // Request list of available games
        Thread.sleep(500);
        socketClient.getAvailableGames();
    }

    @Test
    public void socketClient_createNewGame_joinGame_reJoinGame() throws InterruptedException, IOException, NotBoundException {
        // Establishing connection
        socketClient.establishConnection();

        // Creating new Game
        String gameId = Connection.generateGameID();
        Thread.sleep(500);
        socketClient.createGame(gameId, "test1", 2);

        // Join the same game
        SocketConnection secondSocketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        secondSocketClient.establishConnection();
        Thread.sleep(500);
        secondSocketClient.connectToGame(gameId, "test2", false);

        // Rejoin the same game
        SocketConnection thirdSocketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        thirdSocketClient.establishConnection();
        Thread.sleep(500);
        thirdSocketClient.connectToGame(gameId, "test2", true);
    }

    @Test
    public void socketClient_gameActionSimulation() throws InterruptedException, IOException, NotBoundException {
        // Establishing connection
        socketClient.establishConnection();

        // Creating new Game
        String gameId = Connection.generateGameID();
        Thread.sleep(500);
        socketClient.createGame(gameId, "test1", 2);

        // Join the same game
        SocketConnection secondSocketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        secondSocketClient.establishConnection();
        Thread.sleep(500);
        secondSocketClient.connectToGame(gameId, "test2", false);

        // Check whose turn it is
        Thread.sleep(1000);
        SharedGameState sgs = secondSocketClient.waitTurn();
        if (sgs.currPlayerIndex != sgs.selfPlayerIndex) {
            // If it the first socket's turn, simulate with it a turn
            Thread.sleep(500);
            socketClient.selectColumn(0);
            Thread.sleep(500);
            socketClient.pickTile(4, 1);
            Thread.sleep(500);
            socketClient.reorder(0, 1, 2);
        } else {
            // Otherwise, simulate with second socket
            Thread.sleep(500);
            secondSocketClient.selectColumn(0);
            Thread.sleep(500);
            secondSocketClient.pickTile(4, 1);
            Thread.sleep(500);
            secondSocketClient.reorder(0, 1, 2);
        }
    }

    @Test
    public void jRMIClient_getAvailableGames() throws InterruptedException, IOException, NotBoundException {
        // Establishing connection
        jRMIClient.establishConnection();

        // Request list of available games
        Thread.sleep(500);
        jRMIClient.getAvailableGames();
    }

    @Test
    public void jRMIClient_createNewGame_joinGame_reJoinGame() throws InterruptedException, IOException, NotBoundException {
        // Establishing connection
        jRMIClient.establishConnection();

        // Creating new Game
        String gameId = Connection.generateGameID();
        Thread.sleep(500);
        jRMIClient.createGame(gameId, "test1", 2);

        // Join the same game
        JRMIConnection secondJRMIClient = new JRMIConnection("localhost", Server.JRMI_PORT);
        secondJRMIClient.establishConnection();
        Thread.sleep(500);
        secondJRMIClient.connectToGame(gameId, "test2", false);

        // Rejoin the same game
        JRMIConnection thirdJRMIClient = new JRMIConnection("localhost", Server.JRMI_PORT);
        thirdJRMIClient.establishConnection();
        Thread.sleep(500);
        thirdJRMIClient.connectToGame(gameId, "test2", true);
    }

    @Test
    public void jRMIClient_gameActionSimulation() throws InterruptedException, IOException, NotBoundException {
        // Establishing connection
        jRMIClient.establishConnection();

        // Creating new Game
        String gameId = Connection.generateGameID();
        Thread.sleep(500);
        jRMIClient.createGame(gameId, "test1", 2);

        // Join the same game
        JRMIConnection secondJRMIClient = new JRMIConnection("localhost", Server.JRMI_PORT);
        secondJRMIClient.establishConnection();
        Thread.sleep(500);
        secondJRMIClient.connectToGame(gameId, "test2", false);

        // Check whose turn it is
        Thread.sleep(1000);
        SharedGameState sgs = secondJRMIClient.waitTurn();
        if (sgs.currPlayerIndex != sgs.selfPlayerIndex) {
            // If it the first socket's turn, simulate with it a turn
            Thread.sleep(500);
            jRMIClient.selectColumn(0);
            Thread.sleep(500);
            jRMIClient.pickTile(4, 1);
            Thread.sleep(500);
            jRMIClient.reorder(0, 1, 2);
        } else {
            // Otherwise, simulate with second socket
            Thread.sleep(500);
            secondJRMIClient.selectColumn(0);
            Thread.sleep(500);
            secondJRMIClient.pickTile(4, 1);
            Thread.sleep(500);
            secondJRMIClient.reorder(0, 1, 2);
        }
    }

}
