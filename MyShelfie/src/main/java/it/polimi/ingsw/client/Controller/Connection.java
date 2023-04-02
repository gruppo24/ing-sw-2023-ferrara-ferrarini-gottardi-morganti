package it.polimi.ingsw.client.Controller;

import java.util.List;

import it.polimi.ingsw.common.messages.SharedGameState;

/**
 * Connection abstract class, implemented with both RMI and Socket.
 * This class exposes a bunch of methods that the Client will call to dispatch
 * actions to the server, both in the pre-game phase and in the game phase.
 * 
 * @author Morganti Tommaso
 */
public abstract class Connection {
    /**
     * Get all available games
     * 
     * @return
     */
    public abstract List<String> getAvailableGames();

    /**
     * Creates a new game and connects to it
     * 
     * @param username the username for the player with wich the client will connect
     */
    public abstract void createGame(String username);

    /**
     * Connects to an existing game
     * 
     * @param gameID   the ID of the game to connect to
     * @param username the username for the player with wich the client will connect
     */
    public abstract void connectToGame(String gameID, String username);

    /**
     * This method awaits for the game state to be updated by the server.
     * This should be called over and over again in a loop, until the returned
     * SharedGameState indicates that it's the player's turn.
     */
    public abstract SharedGameState waitTurn();

    /**
     * Selects which column the player wants to fill
     */
    public abstract SharedGameState selectColumn(int column);

    /**
     * Picks a tile from the board
     */
    public abstract SharedGameState pickTile(int x, int y);

    /**
     * Reorders the tiles in the player's selection buffer, to fill the column
     * in the player's library in the desired order
     */
    public abstract SharedGameState reorder(int first, int second, int third);
}
