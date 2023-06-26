package it.polimi.ingsw.server.model;


import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Optional;

import it.polimi.ingsw.server.controller.jrmi.PreGameStubImpl;
import org.junit.*;

import it.polimi.ingsw.client.controller.Connection;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.exceptions.GameAlreadyFullException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;


public class GameStateTest {

    private static Registry registry;

    @BeforeClass
    public static void setUpClass() {
        // setup server, if not already online
        try {
            // setup PreGameStubImpl
            GameStateTest.registry = LocateRegistry.createRegistry(Server.JRMI_PORT);
            PreGameStubImpl preGame = new PreGameStubImpl(GameStateTest.registry);
            GameStateTest.registry.rebind("remotePreGame", preGame);
        } catch (RemoteException ex) {}
    }

    @Before
    public void setUp() {
        // create common and private cards in server
        Server.createCommonCards();
        Server.createPrivateCards();
        Server.GAMES.clear();
    }

    @AfterClass
    public static void tearDownClass() {
//        if (GameStateTest.registry != null) {
//            try {
//                GameStateTest.registry.unbind("remotePreGame");
//            } catch (RemoteException | NotBoundException ex) {}
//        }
    }

    @Test
    public void setAllPlayersOffline_allPlayersJoined_noOnlinePlayers() {
        GameState gameState = new GameState(Connection.generateGameID(), 2);

        // Adding two players
        gameState.addNewPlayerToGame(new Player("testPlayer1"));
        gameState.addNewPlayerToGame(new Player("testPlayer2"));

        assertEquals(gameState.remainingOnline(), 2);
        gameState.setAllPlayersOffline();
        assertEquals(gameState.remainingOnline(), 0);
    }

    @Test
    public void setAllPlayersOffline_allPlayersJoined_gameIsSuspended() {
        GameState gameState = new GameState(Connection.generateGameID(), 2);

        // Adding two players
        gameState.addNewPlayerToGame(new Player("testPlayer1"));
        gameState.addNewPlayerToGame(new Player("testPlayer2"));

        assertEquals(gameState.remainingOnline(), 2);
        gameState.setAllPlayersOffline();
        assertEquals(gameState.remainingOnline(), 0);
        assertTrue(gameState.isSuspended());
    }

    @Test
    public void setAllPlayersOffline_notAllPlayersJoined_oneOnlinePlayer() {
        GameState gameState = new GameState(Connection.generateGameID(), 3);

        // Adding two players
        gameState.addNewPlayerToGame(new Player("testPlayer1"));
        gameState.addNewPlayerToGame(new Player("testPlayer2"));

        assertEquals(gameState.remainingOnline(), 3);
        gameState.setAllPlayersOffline();
        assertEquals(gameState.remainingOnline(), 1);
    }

    @Test
    public void restoreRemotePlayers_twoPlayersInGame_twoPlayersRestored() {
        GameState gameState = new GameState(Connection.generateGameID(), 2);

        // Adding two players
        gameState.addNewPlayerToGame(new Player("testPlayer1"));
        gameState.addNewPlayerToGame(new Player("testPlayer2"));

        gameState.restoreRemotePlayers();
    }

    @Test
    public void addNewPlayerToGame_addTwoPlayers_gameOngoing() {
        GameState gameState = new GameState(Connection.generateGameID(), 2);
        Player player = new Player("testPlayer");
        gameState.addNewPlayerToGame(player);
        SharedGameState sharedGameState = gameState.getSharedGameState(player);
        Assert.assertFalse(sharedGameState.gameOngoing);

        GameState gameState2 = new GameState(Connection.generateGameID(), 2);
        Player player1 = new Player("testPlayer1");
        Player player2 = new Player("testPlayer2");
        gameState2.addNewPlayerToGame(player1);
        gameState2.addNewPlayerToGame(player2);
        SharedGameState sharedGameState1 = gameState2.getSharedGameState(player1);
        assertTrue(sharedGameState1.gameOngoing);
        SharedGameState sharedGameState2 = gameState2.getSharedGameState(player2);
        assertTrue(sharedGameState2.gameOngoing);
    }

    @Test
    public void turnIsOver_boardEmpty_shouldBeRefilled() {
        GameState gameState = new GameState(Connection.generateGameID(), 2);
        Player player = new Player("testPlayer");
        gameState.addNewPlayerToGame(player);
        Player player1 = new Player("testPlayer1");
        gameState.addNewPlayerToGame(player1);

        Board board = gameState.getBoard();
        // empty board
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board.getBoardContent()[i][j] != null) {
                    board.definePickable();
                    board.pick(i, j, 1);
                }
            }
        }
        assertTrue(board.shouldBeRefilled());
        gameState.turnIsOver();
        Assert.assertFalse(board.shouldBeRefilled());
    }

    @Test
    public void addNewPlayerToGame_addTooManyPlayers_shouldThrow() {
        GameState gameState = new GameState(Connection.generateGameID(), 2);
        Player player = new Player("testPlayer");
        gameState.addNewPlayerToGame(player);
        Player player1 = new Player("testPlayer1");
        gameState.addNewPlayerToGame(player1);
        Player player2 = new Player("testPlayer2");
        Assert.assertThrows(GameAlreadyFullException.class, () -> gameState.addNewPlayerToGame(player2));
    }

    @Test
    public void getGameID_shouldBeSamePassed() {
        String gameID = Connection.generateGameID();
        GameState gameState = new GameState(gameID, 2);
        Assert.assertEquals(gameState.getGameID(), gameID);
    }

    @Test
    public void isGameOver_newGame_shouldBeFalse() {
        GameState gameState = new GameState(Connection.generateGameID(), 2);
        Assert.assertFalse(gameState.isGameOver());
    }

    @Test
    public void isGameOver_gameTerminated_shouldBeTrue() {
        GameState gameState = new GameState(Connection.generateGameID(), 2);
        Player player = new Player("testPlayer");
        gameState.addNewPlayerToGame(player);
        gameState.addNewPlayerToGame(new Player("testPlayer2"));
        gameState.terminate();
        assertTrue(gameState.isGameOver());
        SharedGameState sharedGameState = gameState.getSharedGameState(player);
        assertTrue(sharedGameState.gameOver);
    }

    @Test
    public void actuallyIsPlayersTurn() {
        GameState gameState = new GameState(Connection.generateGameID(), 2);
        Player player1 = new Player("testPlayer1");
        Player player2 = new Player("testPlayer2");
        gameState.addNewPlayerToGame(player1);
        gameState.addNewPlayerToGame(player2);
        assertTrue(gameState.actuallyIsPlayersTurn(player1) || gameState.actuallyIsPlayersTurn(player2));
        Assert.assertFalse(gameState.actuallyIsPlayersTurn(player1) && gameState.actuallyIsPlayersTurn(player2));
    }

    @Test
    public void getBoard_newGame_shouldBeNotNull() {
        GameState gameState = new GameState(Connection.generateGameID(), 2);
        Assert.assertNotNull(gameState.getBoard());
    }

    @Test
    public void getUserByUsername() {
        GameState gameState = new GameState(Connection.generateGameID(), 2);
        Player player = new Player("testPlayer");
        gameState.addNewPlayerToGame(player);
        Optional<Player> playerOptional = gameState.getUserByUsername("testPlayer");
        assertTrue(playerOptional.isPresent());
        Assert.assertEquals(playerOptional.get(), player);
    }

    @Test
    public void finalRount() {
        GameState gameState = new GameState(Connection.generateGameID(), 2);
        Player player = new Player("testPlayer");
        gameState.addNewPlayerToGame(player);
        Player player1 = new Player("testPlayer1");
        gameState.addNewPlayerToGame(player1);
        SharedGameState sharedGameState = gameState.getSharedGameState(player);

        Player armChairPlayer = sharedGameState.armchairIndex == 0 ? player : player1;
        for (int column = 0; column < 5; column++) {
            for (int counter = 0; counter < 2; counter++) {
                armChairPlayer.selectColumn(column);
                armChairPlayer.pushTileToSelectionBuffer(TileType.CAT);
                armChairPlayer.pushTileToSelectionBuffer(TileType.TOY);
                armChairPlayer.pushTileToSelectionBuffer(TileType.FRAME);
                armChairPlayer.flushBufferIntoLibrary();
            }
        }
        gameState.turnIsOver();
        SharedGameState sharedGameState1 = gameState.getSharedGameState(player1);
        assertTrue(sharedGameState1.isFinalRound);
    }

}
