package it.polimi.ingsw.common.messages.responses;

import it.polimi.ingsw.common.TileState;
import it.polimi.ingsw.common.TileType;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

/**
 * This class holds a representation of the current state of the game
 * relative to a particular player. This class is used as a virtual-view
 * for all players inside a game
 *
 * @author Ferrarini Andrea
 */
public class SharedGameState implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;

    // List of all players connected to the current game
    public String[] players;

    // index of the player that the shared game state is relative to
    public int selfPlayerIndex;

    // Board related attributes
    public TileType[][] boardContent;
    public TileState[][] boardState;

    // Turn related attributes
    public int currPlayerIndex;
    public int armchairIndex;
    public boolean isFinalRound;
    public String firstCompleter;

    // Common objectives related attributes
    public String[] commonsId;
    public String[] commonsDesc;

    // Private objective attributes
    public String privateId;
    public String privateDesc;

    // Libraries
    public TileType[][][] libraries;

    // Achieved points attributes
    public int commonPts; // Actual points, not achievement 'order'
    public int privatePts;
    public int clusterPts;
    public boolean firstFilled;

    // Game dynamics attributes
    public int selectedColumn;
    public TileType[] selectionBuffer;

    // Size of this matrix: #commmonCards * #players.
    // Structure is: player with username == commonsAchievers[i][j] ==> common
    // objective i completed by said player with j as order of completion
    public String[][] commonsAchievers;

    // Game dynamics attributes
    public boolean gameOngoing; // Tells the client whether the game has started or not
    public boolean gameOver; // Tells the client whether the game has ended or not
    public boolean gameTerminated; // Tells the client that the game has been prematurely terminated

    // Game ending information (ONLY AVAILABLE AFTER GAME HAS ENDED)
    public HashMap<String, Integer> leaderboard;
}
