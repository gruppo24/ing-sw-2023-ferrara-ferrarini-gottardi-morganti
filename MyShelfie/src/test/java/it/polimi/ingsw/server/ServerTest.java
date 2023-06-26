package it.polimi.ingsw.server;


import it.polimi.ingsw.client.controller.Connection;
import it.polimi.ingsw.client.controller.JRMIConnection;
import it.polimi.ingsw.client.controller.SocketConnection;
import it.polimi.ingsw.common.TileState;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.common.messages.responses.ResponseStatus;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import it.polimi.ingsw.server.model.GameState;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;


public class ServerTest {

    private static Connection socketClient;
    private static Connection jRMIClient;

    @BeforeClass
    public static void setUpClass() {
        Server.main(new String[]{});
    }

    @Before
    public void setUp() {
        // Clearing all games in the server
        Server.GAMES.clear();

        // Creating two clients
        socketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        jRMIClient = new JRMIConnection("localhost", Server.JRMI_PORT);
    }

    @After
    public void tearDown() {
        // Remove any potentially created games
        Server.GAMES.clear();
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

    private void checkGamesMatch(Map<String, int[]> games) {
        // Making sure games provided and actually available match
        for (String gameId : games.keySet())
            assertTrue(Server.GAMES.stream().map(GameState::getGameID).toList().contains(gameId));
        for (String gameId : Server.GAMES.stream().map(GameState::getGameID).toList())
            assertTrue(games.containsKey(gameId));
    }

    /*============================ Socket ========================== */

    @Test
    public void socketClient_getAvailableGames_returnsAvailableGames() throws InterruptedException, IOException, NotBoundException {
        // Establishing connection
        socketClient.establishConnection();

        // Request list of available games
        Thread.sleep(500);
        Map<String, int[]> games = socketClient.getAvailableGames();

        // Checking result is right
        checkGamesMatch(games);
    }

    @Test
    public void socketClient_createNewGame_joinGame_reJoinGame() throws InterruptedException, IOException, NotBoundException {
        ResponseStatus response;

        // Establishing connection
        socketClient.establishConnection();

        // Creating new Game
        String gameId = Connection.generateGameID();
        Thread.sleep(500);
        response = socketClient.createGame(gameId, "test1", 2);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Join the same game
        SocketConnection secondSocketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        secondSocketClient.establishConnection();
        Thread.sleep(500);

        // Checking the created game exists
        Map<String, int[]> games = secondSocketClient.getAvailableGames();
        Thread.sleep(500);
        checkGamesMatch(games);

        // Finally, connecting to said game
        response = secondSocketClient.connectToGame(gameId, "test2", false);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Rejoin the same game
        SocketConnection thirdSocketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        thirdSocketClient.establishConnection();
        Thread.sleep(500);
        response = thirdSocketClient.connectToGame(gameId, "test2", true);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Checking the game is actually full
        GameState game = null;
        for (GameState eachGame : Server.GAMES) {
            if (eachGame.getGameID().equals(gameId)) {
                game = eachGame;
                break;
            }
        }
        assertNotNull(game);
        assertEquals(game.getPlayerStatus()[0], game.getPlayerStatus()[1]);
    }

    @Test
    public void socketClient_createGameWithUsedName_doesntCreateGame() throws InterruptedException, IOException, NotBoundException {
        ResponseStatus response;

        // Establishing connection
        socketClient.establishConnection();

        // Creating new Game
        String gameId = Connection.generateGameID();
        Thread.sleep(500);
        response = socketClient.createGame(gameId, "test1", 2);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Create new client
        SocketConnection secondSocketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        secondSocketClient.establishConnection();
        Thread.sleep(500);

        // Fetching current games
        Map<String, int[]> games = secondSocketClient.getAvailableGames();
        Thread.sleep(500);

        // Trying to create a game with same ID
        response = secondSocketClient.createGame(gameId, "test2", 2);
        Thread.sleep(500);
        assertEquals(response, ResponseStatus.GAME_ID_TAKEN);

        // Checking, finally, that the current list of games is equal to the previous one
        checkGamesMatch(games);
    }

    @Test
    public void socketClient_joinWithUsedUsername_doesntJoin() throws InterruptedException, IOException, NotBoundException {
        ResponseStatus response;

        // Establishing connection
        socketClient.establishConnection();

        // Creating new Game
        Thread.sleep(500);
        String gameId = Connection.generateGameID();
        response = socketClient.createGame(gameId, "test1", 2);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Create new client
        SocketConnection secondSocketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        Thread.sleep(500);
        secondSocketClient.establishConnection();

        // Trying to join the game with same username
        Thread.sleep(500);
        response = secondSocketClient.connectToGame(gameId, "test1", false);
        assertEquals(response, ResponseStatus.USERNAME_TAKEN);

        // Checking the user hasn't actually joined the game
        // Checking the game is actually full
        GameState game = null;
        for (GameState eachGame : Server.GAMES) {
            if (eachGame.getGameID().equals(gameId)) {
                game = eachGame;
                break;
            }
        }
        assertNotNull(game);
        assertEquals(game.getPlayerStatus()[0] + 1, game.getPlayerStatus()[1]);
    }

    @Test
    public void socketClient_joinGame_noGameId() throws InterruptedException, IOException, NotBoundException {
        ResponseStatus response;

        // Establishing connection
        socketClient.establishConnection();

        // Creating new Game
        Thread.sleep(500);
        String gameId = Connection.generateGameID();
        response = socketClient.createGame(gameId, "test1", 2);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Create new client
        SocketConnection secondSocketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        Thread.sleep(500);
        secondSocketClient.establishConnection();

        // Trying to join the game with no gameId
        Thread.sleep(500);
        response = secondSocketClient.connectToGame("", "test2", false);
        assertEquals(response, ResponseStatus.NO_SUCH_GAME_ID);

        // Checking the user hasn't actually joined the game
        // Checking the game is actually full
        GameState game = null;
        for (GameState eachGame : Server.GAMES) {
            if (eachGame.getGameID().equals(gameId)) {
                game = eachGame;
                break;
            }
        }
        assertNotNull(game);
        assertEquals(game.getPlayerStatus()[0] + 1, game.getPlayerStatus()[1]);
    }

    @Test
    public void socketClient_joinAlreadyFullGame_doesntJoin() throws InterruptedException, IOException, NotBoundException {
        ResponseStatus response;

        // Establishing connection
        socketClient.establishConnection();

        // Creating new Game
        Thread.sleep(500);
        String gameId = Connection.generateGameID();
        response = socketClient.createGame(gameId, "test1", 2);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Create new client
        SocketConnection secondSocketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        Thread.sleep(500);
        secondSocketClient.establishConnection();

        // Joining the game again
        Thread.sleep(500);
        response = secondSocketClient.connectToGame(gameId, "test2", false);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Trying to join the game a third time
        SocketConnection thirdSocketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        Thread.sleep(500);
        thirdSocketClient.establishConnection();
        Thread.sleep(500);
        response = thirdSocketClient.connectToGame(gameId, "test3", false);
        assertEquals(response, ResponseStatus.SELECTED_GAME_FULL);
    }

    @Test
    public void socketClient_rejoinWithWrongUsername_doesntRejoin() throws InterruptedException, IOException, NotBoundException {
        ResponseStatus response;

        // Establishing connection
        socketClient.establishConnection();

        // Creating new Game
        Thread.sleep(500);
        String gameId = Connection.generateGameID();
        response = socketClient.createGame(gameId, "test1", 2);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Create new client
        SocketConnection secondSocketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        Thread.sleep(500);
        secondSocketClient.establishConnection();

        // Joining the game with the second client
        Thread.sleep(500);
        response = secondSocketClient.connectToGame(gameId, "test2", false);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Rejoin the same game with wrong name
        SocketConnection thirdSocketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        thirdSocketClient.establishConnection();
        Thread.sleep(500);
        response = thirdSocketClient.connectToGame(gameId, "test3", true);
        assertEquals(response, ResponseStatus.USERNAME_NOT_IN_GAME);
    }

    @Test
    public void socketClient_rejoinWithNoGameId_doesntRejoin() throws InterruptedException, IOException, NotBoundException {
        ResponseStatus response;

        // Establishing connection
        socketClient.establishConnection();

        // Creating new Game
        Thread.sleep(500);
        String gameId = Connection.generateGameID();
        response = socketClient.createGame(gameId, "test1", 2);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Create new client
        SocketConnection secondSocketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        Thread.sleep(500);
        secondSocketClient.establishConnection();

        // Joining the game with the second client
        Thread.sleep(500);
        response = secondSocketClient.connectToGame(gameId, "test2", false);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Rejoin the same game with no gameId
        SocketConnection thirdSocketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        thirdSocketClient.establishConnection();
        Thread.sleep(500);
        response = thirdSocketClient.connectToGame("", "test2", true);
        assertEquals(response, ResponseStatus.NO_SUCH_GAME_ID);
    }

    @Test
    public void socketClient_gameActionSimulation() throws InterruptedException, IOException, NotBoundException {
        ResponseStatus response;

        // Establishing connection
        socketClient.establishConnection();

        // Creating new Game
        String gameId = Connection.generateGameID();
        Thread.sleep(500);
        response = socketClient.createGame(gameId, "test1", 2);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Join the same game
        SocketConnection secondSocketClient = new SocketConnection("localhost", Server.SOCKET_PORT);
        secondSocketClient.establishConnection();
        Thread.sleep(500);
        response = secondSocketClient.connectToGame(gameId, "test2", false);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Discarding first SGS
        socketClient.waitTurn();
        secondSocketClient.waitTurn();

        // Check whose turn it is
        Thread.sleep(500);
        SharedGameState sgs = socketClient.waitTurn();

        int x = 4, y = 1, column = 1;
        TileType tileToPick = sgs.boardContent[x][y];
        Connection s = sgs.currPlayerIndex == sgs.selfPlayerIndex ? socketClient : secondSocketClient;
        Connection other_s = sgs.currPlayerIndex != sgs.selfPlayerIndex ? socketClient : secondSocketClient;


        // Select column
        Thread.sleep(500);
        sgs = s.selectColumn(1);
        other_s.waitTurn();  // Discarding the second players SharedGameStates
        assertEquals(sgs.selectedColumn, column);

        // Select invalid column
        Thread.sleep(500);
        sgs = s.selectColumn(10);
        other_s.waitTurn();  // Discarding the second players SharedGameStates
        assertEquals(sgs.selectedColumn, column);

        // Pick invalid tile
        Thread.sleep(500);
        sgs = s.pickTile(14, 1);
        other_s.waitTurn();  // Discarding the second players SharedGameStates
        assertEquals(sgs.boardState[x][y], TileState.PICKABLE);
        assertEquals(sgs.boardContent[x][y], tileToPick);

        // Pick tile
        Thread.sleep(500);
        sgs = s.pickTile(4, 1);
        other_s.waitTurn();  // Discarding the second players SharedGameStates
        assertEquals(sgs.boardState[x][y], TileState.NOT_PICKABLE);
        assertNull(sgs.boardContent[x][y]);
        assertEquals(sgs.selectionBuffer[0], tileToPick);
        assertNull(sgs.selectionBuffer[1]);
        assertNull(sgs.selectionBuffer[2]);

        // Invalid reordering
        Thread.sleep(500);
        sgs = s.reorder(1, 0, 2);
        other_s.waitTurn();  // Discarding the second players SharedGameStates
        assertEquals(sgs.selectionBuffer[0], tileToPick);
        assertNull(sgs.selectionBuffer[1]);
        assertNull(sgs.selectionBuffer[2]);

        // Reordering
        Thread.sleep(500);
        sgs = s.reorder(0, 1, 2);
        assertEquals(sgs.libraries[sgs.selfPlayerIndex][column][0], tileToPick);
        assertNull(sgs.selectionBuffer);

        // Finally, the second player is notified of their turn
        sgs = other_s.waitTurn();
        assertEquals(sgs.currPlayerIndex, sgs.selfPlayerIndex);
    }

    /*============================ jRMI ========================== */

    @Test
    public void jRMIClient_getAvailableGames_returnsAvailableGames() throws InterruptedException, IOException, NotBoundException {
        // Establishing connection
        jRMIClient.establishConnection();

        // Request list of available games
        Thread.sleep(500);
        Map<String, int[]> games = jRMIClient.getAvailableGames();

        // Checking result is right
        checkGamesMatch(games);
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

        // Checking the created game exists
        Map<String, int[]> games = secondJRMIClient.getAvailableGames();
        Thread.sleep(500);
        checkGamesMatch(games);

        // Finally, connecting to said game
        secondJRMIClient.connectToGame(gameId, "test2", false);

        // Rejoin the same game
        JRMIConnection thirdJRMIClient = new JRMIConnection("localhost", Server.JRMI_PORT);
        thirdJRMIClient.establishConnection();
        Thread.sleep(500);
        thirdJRMIClient.connectToGame(gameId, "test2", true);

        // Checking the game is actually full
        GameState game = null;
        for (GameState eachGame : Server.GAMES) {
            if (eachGame.getGameID().equals(gameId)) {
                game = eachGame;
                break;
            }
        }
        assertNotNull(game);
        assertEquals(game.getPlayerStatus()[0], game.getPlayerStatus()[1]);
    }

    @Test
    public void jRMIClient_createGameWithUsedName_doesntCreateGame() throws InterruptedException, IOException, NotBoundException {
        ResponseStatus response;

        // Establishing connection
        jRMIClient.establishConnection();

        // Creating new Game
        String gameId = Connection.generateGameID();
        Thread.sleep(500);
        response = jRMIClient.createGame(gameId, "test1", 2);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Create new client
        JRMIConnection secondJRMIClient = new JRMIConnection("localhost", Server.JRMI_PORT);
        secondJRMIClient.establishConnection();
        Thread.sleep(500);

        // Fetching current games
        Map<String, int[]> games = secondJRMIClient.getAvailableGames();
        Thread.sleep(500);

        // Trying to create a game with same ID
        response = secondJRMIClient.createGame(gameId, "test2", 2);
        Thread.sleep(500);
        assertEquals(response, ResponseStatus.GAME_ID_TAKEN);

        // Checking, finally, that the current list of games is equal to the previous one
        checkGamesMatch(games);
    }

    @Test
    public void jRMIClient_joinWithUsedUsername_doesntJoin() throws InterruptedException, IOException, NotBoundException {
        ResponseStatus response;

        // Establishing connection
        jRMIClient.establishConnection();

        // Creating new Game
        Thread.sleep(500);
        String gameId = Connection.generateGameID();
        response = jRMIClient.createGame(gameId, "test1", 2);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Create new client
        JRMIConnection secondJRMIClient = new JRMIConnection("localhost", Server.JRMI_PORT);
        Thread.sleep(500);
        secondJRMIClient.establishConnection();

        // Trying to join the game with same username
        Thread.sleep(500);
        response = secondJRMIClient.connectToGame(gameId, "test1", false);
        assertEquals(response, ResponseStatus.USERNAME_TAKEN);

        // Checking the user hasn't actually joined the game
        // Checking the game is actually full
        GameState game = null;
        for (GameState eachGame : Server.GAMES) {
            if (eachGame.getGameID().equals(gameId)) {
                game = eachGame;
                break;
            }
        }
        assertNotNull(game);
        assertEquals(game.getPlayerStatus()[0] + 1, game.getPlayerStatus()[1]);
    }

    @Test
    public void jRMIClient_joinGame_noGameId() throws InterruptedException, IOException, NotBoundException {
        ResponseStatus response;

        // Establishing connection
        jRMIClient.establishConnection();

        // Creating new Game
        Thread.sleep(500);
        String gameId = Connection.generateGameID();
        response = jRMIClient.createGame(gameId, "test1", 2);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Create new client
        JRMIConnection secondJRMIClient = new JRMIConnection("localhost", Server.JRMI_PORT);
        Thread.sleep(500);
        secondJRMIClient.establishConnection();

        // Trying to join the game with no gameId
        Thread.sleep(500);
        response = secondJRMIClient.connectToGame("", "test2", false);
        assertEquals(response, ResponseStatus.NO_SUCH_GAME_ID);
    }

    @Test
    public void jRMIClient_joinAlreadyFullGame_doesntJoin() throws InterruptedException, IOException, NotBoundException {
        ResponseStatus response;

        // Establishing connection
        jRMIClient.establishConnection();

        // Creating new Game
        Thread.sleep(500);
        String gameId = Connection.generateGameID();
        response = jRMIClient.createGame(gameId, "test1", 2);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Create new client
        JRMIConnection secondJRMIClient = new JRMIConnection("localhost", Server.JRMI_PORT);
        Thread.sleep(500);
        secondJRMIClient.establishConnection();

        // Joining the game again
        Thread.sleep(500);
        response = secondJRMIClient.connectToGame(gameId, "test2", false);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Trying to join the game a third time
        JRMIConnection thirdJRMIClient = new JRMIConnection("localhost", Server.JRMI_PORT);
        Thread.sleep(500);
        thirdJRMIClient.establishConnection();
        Thread.sleep(500);
        response = thirdJRMIClient.connectToGame(gameId, "test3", false);
        assertEquals(response, ResponseStatus.SELECTED_GAME_FULL);
    }

    @Test (expected = NotBoundException.class)
    public void jRMIClient_rejoinWithWrongUsername_doesntRejoin() throws InterruptedException, IOException, NotBoundException {
        ResponseStatus response;

        // Establishing connection
        jRMIClient.establishConnection();

        // Creating new Game
        Thread.sleep(500);
        String gameId = Connection.generateGameID();
        response = jRMIClient.createGame(gameId, "test1", 2);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Create new client
        JRMIConnection secondJRMIClient = new JRMIConnection("localhost", Server.JRMI_PORT);
        Thread.sleep(500);
        secondJRMIClient.establishConnection();

        // Joining the game with the second client
        Thread.sleep(500);
        response = secondJRMIClient.connectToGame(gameId, "test2", false);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Rejoin the same game with wrong name
        JRMIConnection thirdJRMIClient = new JRMIConnection("localhost", Server.JRMI_PORT);
        thirdJRMIClient.establishConnection();
        Thread.sleep(500);
        thirdJRMIClient.connectToGame(gameId, "test3", true);
    }

    @Test (expected = NotBoundException.class)
    public void jRMIClient_rejoinWithNoGameId_doesntRejoin() throws InterruptedException, IOException, NotBoundException {
        ResponseStatus response;

        // Establishing connection
        jRMIClient.establishConnection();

        // Creating new Game
        Thread.sleep(500);
        String gameId = Connection.generateGameID();
        response = jRMIClient.createGame(gameId, "test1", 2);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Create new client
        JRMIConnection secondJRMIClient = new JRMIConnection("localhost", Server.JRMI_PORT);
        Thread.sleep(500);
        secondJRMIClient.establishConnection();

        // Joining the game with the second client
        Thread.sleep(500);
        response = secondJRMIClient.connectToGame(gameId, "test2", false);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Rejoin the same game with no gameId
        JRMIConnection thirdJRMIClient = new JRMIConnection("localhost", Server.JRMI_PORT);
        thirdJRMIClient.establishConnection();
        Thread.sleep(500);
        thirdJRMIClient.connectToGame("", "test2", true);
    }

    @Test
    public void jRMIClient_gameActionSimulation() throws InterruptedException, IOException, NotBoundException {
        ResponseStatus response;

        // Establishing connection
        jRMIClient.establishConnection();

        // Creating new Game
        String gameId = Connection.generateGameID();
        Thread.sleep(500);
        response = jRMIClient.createGame(gameId, "test1", 2);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Join the same game
        JRMIConnection secondJRMIClient = new JRMIConnection("localhost", Server.JRMI_PORT);
        secondJRMIClient.establishConnection();
        Thread.sleep(500);
        response = secondJRMIClient.connectToGame(gameId, "test2", false);
        assertEquals(response, ResponseStatus.SUCCESS);

        // Check whose turn it is
        Thread.sleep(500);
        SharedGameState sgs = secondJRMIClient.waitTurn();

        int x = 4, y = 1, column = 1;
        TileType tileToPick = sgs.boardContent[x][y];
        Connection s = sgs.currPlayerIndex != sgs.selfPlayerIndex ? jRMIClient : secondJRMIClient;
        Connection other_s = sgs.currPlayerIndex == sgs.selfPlayerIndex ? jRMIClient : secondJRMIClient;

        // Select column
        Thread.sleep(500);
        sgs = s.selectColumn(1);
        assertEquals(sgs.selectedColumn, column);

        // Select invalid column
        Thread.sleep(500);
        sgs = s.selectColumn(10);
        assertEquals(sgs.selectedColumn, column);

        // Pick invalid tile
        Thread.sleep(500);
        sgs = s.pickTile(14, 1);
        assertEquals(sgs.boardState[x][y], TileState.PICKABLE);
        assertEquals(sgs.boardContent[x][y], tileToPick);

        // Pick tile
        Thread.sleep(500);
        sgs = s.pickTile(4, 1);
        assertEquals(sgs.boardState[x][y], TileState.NOT_PICKABLE);
        assertNull(sgs.boardContent[x][y]);
        assertEquals(sgs.selectionBuffer[0], tileToPick);
        assertNull(sgs.selectionBuffer[1]);
        assertNull(sgs.selectionBuffer[2]);

        // Invalid reordering
        Thread.sleep(500);
        sgs = s.reorder(1, 0, 2);
        assertEquals(sgs.selectionBuffer[0], tileToPick);
        assertNull(sgs.selectionBuffer[1]);
        assertNull(sgs.selectionBuffer[2]);

        // Reordering
        Thread.sleep(500);
        sgs = s.reorder(0, 1, 2);
        assertEquals(sgs.libraries[sgs.selfPlayerIndex][column][0], tileToPick);
        assertNull(sgs.selectionBuffer);

        new Thread(() -> {
            // Find the game
            GameState game = null;
            for (GameState eachGame : Server.GAMES) {
                if (eachGame.getGameID().equals(gameId)) {
                    game = eachGame;
                    break;
                }
            }

            // Game MUST exist
            assertNotNull(game);

            // Force the lock to wake-up all threads
            while (true) {
                synchronized (game.gameLock) {
                    game.gameLock.notifyAll();
                }
            }
        }).start();

        // Finally, the second player is notified of their turn
        sgs = other_s.waitTurn();
        assertEquals(sgs.currPlayerIndex, sgs.selfPlayerIndex);
    }
}
