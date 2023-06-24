package it.polimi.ingsw.client.controller;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.Map;

import it.polimi.ingsw.common.messages.responses.ResponseStatus;
import it.polimi.ingsw.common.messages.responses.SharedGameState;

/**
 * Connection abstract class, implemented with both RMI and Socket.
 * This class exposes a bunch of methods that the Client will call to dispatch
 * actions to the server, both in the pre-game phase and in the game phase.
 * 
 * @author Morganti Tommaso
 */
public abstract class Connection {
    /**
     * A static method to generate game IDs
     */
    public static String generateGameID() {
        return "game-" + System.currentTimeMillis();
    }

    // ==== PREGAME PHASE ====

    /**
     * Get a list of all available games
     * 
     * @return a Map, the key is the game id and the value is an array of two
     *         integers, the first is the num of players connected, the second is
     *         the num of players required
     * @throws IOException in case of socket connection unavailable (specifically, its
     *          subclass RemoteException if RMI connection is unavailable)
     */
    public abstract Map<String, int[]> getAvailableGames() throws IOException;

    /**
     * Method in charge of actually performing the connection to a server
     *
     * @throws IOException in case of socket connection unavailable (specifically, its
     *          subclass RemoteException if RMI connection is unavailable)
     * @throws NotBoundException in case of remote object unavailable
     */
    public abstract void establishConnection() throws IOException, NotBoundException;

    /**
     * Method in charge of sending periodic pings to the server during idle periods.
     * This will prevent channel closure, confirming the client is still online
     *
     * @param echoMillisDelay delay between each echo
     */
    public abstract void asyncKeepAliveEcho(int echoMillisDelay);

    /**
     * Default implementation of {@link Connection#asyncKeepAliveEcho(int)}
     * with 5000 millisecond delays between each ping
     */
    public void asyncKeepAliveEcho() {
        asyncKeepAliveEcho(5_000);
    }

    /**
     * Creates a new game and connects to it
     *
     * @param gameID unique gameID to be associated to the game which we create
     * @param username the username for the player with which the client will connect
     * @param numPlayers the number of players for the game
     * @return the status of the request, can be SUCCESS, GAME_ID_TAKEN if the gameID is already taken
     *
     * @throws IOException in case of socket connection unavailable (specifically, its
     *          subclass RemoteException if RMI connection is unavailable)
     * @throws NotBoundException in case of remote object unavailable
     */
    public abstract ResponseStatus createGame(String gameID, String username, int numPlayers) throws IOException, NotBoundException;

    /**
     * Connects to an existing game with the given gameID and username
     * 
     * @param gameID   the ID of the game to connect to
     * @param username the username for the player with which the client will connect
     * @param rejoining whether we are attempting to rejoin an existing game session or
     *                  we are joining for the first time
     * @return the status of the request, can be SUCCESS, NO_SUCH_GAME_ID if the
     *         gameID doesn't exist, SELECTED_GAME_FULL if the game is already full
     *         or USERNAME_TAKEN if the username is already taken. When rejoining a
     *         game, also USERNAME_NOT_IN_GAME is a possible response status message,
     *         informing us that we are trying to rejoin with a username which doesn't
     *         exist.
     *
     * @throws IOException in case of socket connection unavailable (specifically, its
     *          subclass RemoteException if RMI connection is unavailable)
     * @throws NotBoundException in case of remote object unavailable
     */
    public abstract ResponseStatus connectToGame(String gameID, String username, boolean rejoining) throws IOException, NotBoundException;

    // ==== GAME PHASE ====

    /**
     * This method awaits for the game state to be updated by the server.
     * This should be called over and over again in a loop, until the returned
     * SharedGameState indicates that it's the player's turn.
     *
     * @throws IOException in case of socket connection unavailable (specifically, its
     *          subclass RemoteException if RMI connection is unavailable)
     */
    public abstract SharedGameState waitTurn() throws IOException;

    /**
     * Selects which column the player wants to fill
     *
     * @throws IOException in case of socket connection unavailable (specifically, its
     *          subclass RemoteException if RMI connection is unavailable)
     */
    public abstract SharedGameState selectColumn(int column) throws IOException;

    /**
     * Picks a tile from the board
     *
     * @throws IOException in case of socket connection unavailable (specifically, its
     *          subclass RemoteException if RMI connection is unavailable)
     */
    public abstract SharedGameState pickTile(int x, int y) throws IOException;

    /**
     * Reorders the tiles in the player's selection buffer, to fill the column
     * in the player's library in the desired order
     *
     * @throws IOException in case of socket connection unavailable (specifically, its
     *          subclass RemoteException if RMI connection is unavailable)
     */
    public abstract SharedGameState reorder(int first, int second, int third) throws IOException;
}
