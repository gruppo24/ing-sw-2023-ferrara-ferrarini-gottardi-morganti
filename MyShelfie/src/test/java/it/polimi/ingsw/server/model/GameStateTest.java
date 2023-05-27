package it.polimi.ingsw.server.model;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.polimi.ingsw.client.controller.Connection;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.controller.jrmi.PreGameStubImpl;
import it.polimi.ingsw.server.exceptions.GameAlreadyFullException;

public class GameStateTest {
    private static Registry registry;

    @BeforeClass
    public static void setUpClass() throws RemoteException {
        // setup PreGameStubImpl
        registry = LocateRegistry.createRegistry(Server.JRMI_PORT);
        PreGameStubImpl preGame = new PreGameStubImpl(registry);
        registry.rebind("remotePreGame", preGame);
    }

    @Before
    public void setUp() throws RemoteException {
        // create common and private cards in server
        Server.createCommonCards();
        Server.createPrivateCards();
        Server.GAMES.clear();
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
        Assert.assertTrue(sharedGameState1.gameOngoing);
        SharedGameState sharedGameState2 = gameState2.getSharedGameState(player2);
        Assert.assertTrue(sharedGameState2.gameOngoing);
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
        Assert.assertTrue(board.shouldBeRefilled());
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
        Assert.assertTrue(gameState.isGameOver());
        SharedGameState sharedGameState = gameState.getSharedGameState(player);
        Assert.assertTrue(sharedGameState.gameOver);
    }

    @Test
    public void actuallyIsPlayersTurn() {
        GameState gameState = new GameState(Connection.generateGameID(), 2);
        Player player1 = new Player("testPlayer1");
        Player player2 = new Player("testPlayer2");
        gameState.addNewPlayerToGame(player1);
        gameState.addNewPlayerToGame(player2);
        Assert.assertTrue(gameState.actuallyIsPlayersTurn(player1) || gameState.actuallyIsPlayersTurn(player2));
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
        Assert.assertTrue(playerOptional.isPresent());
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
        Assert.assertTrue(sharedGameState1.isFinalRound);
    }

}
