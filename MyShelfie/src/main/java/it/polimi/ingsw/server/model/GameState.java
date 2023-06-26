package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import it.polimi.ingsw.server.RandomGenerator;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.controller.jrmi.GameActionStubImpl;
import it.polimi.ingsw.server.exceptions.GameAlreadyFullException;

import java.io.*;
import java.rmi.RemoteException;
import java.util.*;

import static it.polimi.ingsw.server.Server.*;
import static it.polimi.ingsw.server.controller.jrmi.PreGameStubImpl.addRemoteGame;
import static it.polimi.ingsw.server.controller.jrmi.PreGameStubImpl.addRemotePlayer;

/**
 * Class that describes the state of a specific game
 *
 * @author Ferrara Silvia
 * @author Ferrarini Andrea
 */
public class GameState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String gameUniqueCode;

    // attributes related to turn management
    private int armchair;
    private int currPlayerIndex;
    private boolean finalRound = false;
    private Player firstFilledPlayer;
    private final Player[] players;

    private final Board board;

    // attributes related to common objectives
    private final int[] nextCommons = { 1, 1 };
    private final CommonCard[] commonCards;

    // Game dynamics attributes
    transient public Object gameLock = new Object();
    private boolean gameOver = false;
    private boolean gameOngoing = false;
    private boolean suspended = false;
    private boolean gameTerminated = false;

    // Disconnection handling
    public transient Thread reconnectionTimer;


    /**
     * Class constructor
     * 
     * @param GameID       unique game identifier
     * @param numOfPlayers number of players for the current game
     */
    public GameState(String GameID, int numOfPlayers) {
        this.gameUniqueCode = GameID;
        this.players = new Player[numOfPlayers];
        this.board = new Board();
        this.commonCards = new CommonCard[2];

        // we populate the CommonCards array with random CommonCards
        int[] numOfCard = RandomGenerator.random(2);
        for (int i = 0; i < numOfCard.length; i++) {
            this.commonCards[i] = Server.commonCards[numOfCard[i]];
        }

        // Finally, we add the game to the map of remote jRMI games
        addRemoteGame(this.gameUniqueCode);
    }

    /**
     * Method in charge of restoring all remote objects for each player
     */
    public void restoreRemotePlayers() {
        // Re-adding remote game
        addRemoteGame(this.gameUniqueCode);

        // Restoring a remote object for each player
        for (Player player : this.players) {
            try {
                GameActionStubImpl remotePlayer = new GameActionStubImpl(this, player);
                addRemotePlayer(this.gameUniqueCode, player.nickname, remotePlayer);
            } catch (RemoteException ex) {
                System.out.println("Error in restoring remote player: " + ex);
            }
        }
    }

    /**
     * Upon game restoring, all players should be marked as offline initially...
     * This method is in charge of forcing all player connection states to "disconnected"
     */
    public void setAllPlayersOffline() {
        for (Player player: this.players)
            if (player != null)
                player.hasDisconnected();

        // As a consequence, the game HAS to be in the "suspended" state
        this.suspended = true;
    }

    /**
     * Method in charge of returning number of currently online players
     * @return number of online players
     */
    public int remainingOnline() {
        int remaining = 0;
        for (Player player : this.players)
            if (player == null)  // If a player hasn't joined yet, we consider them online in the count...
                remaining++;
            else if (player.isConnected())
                remaining++;
        return remaining;
    }

    /**
     * Getter method for the this.suspended attribute
     * @return this.suspended
     */
    public boolean isSuspended() {
        return this.suspended;
    }

    /**
     * Method to be called after the turn of a player has ended. The
     * method is in charge of performing all the required end-of-turn
     * operations
     */
    public void turnIsOver() {
        // We update the points of the current player
        if (this.players[this.currPlayerIndex] != null) {
            this.players[this.currPlayerIndex].updatePrivatePoints();
            this.players[this.currPlayerIndex].updateClusterPoints();
            this.obtainedCommons();
        }

        // If it already isn't the final round, checking if the current player has
        // filled their library entirely
        if (!this.finalRound) {
            this.finalRound = this.players[this.currPlayerIndex].checkIfFilled();

            // If the player has now triggered the final round, they obtain an additional
            // point
            if (this.finalRound)
                firstFilledPlayer = this.players[this.currPlayerIndex];
        }

        // We update the player-turn index and check if the game has ended
        int online = this.remainingOnline();
        if (online > 1 || online == 1 && !this.players[this.currPlayerIndex].isConnected()) {
            try {
                do {
                    this.currPlayerIndex = (this.currPlayerIndex + 1) % this.players.length;
                    this.gameOver = (this.currPlayerIndex == this.armchair && this.finalRound) || this.gameTerminated;
                } while (!this.players[this.currPlayerIndex].isConnected() && !this.gameOver);
            } catch (NullPointerException ex) {
                return;
            }
            this.suspended = false;
        } else {
            this.suspended = true;
        }

        // Only if the game hasn't ended, update the current board-state
        if (!this.gameOver && !this.gameTerminated) {
            if (this.board.shouldBeRefilled())
                this.board.refillBoard(this.players.length);
            this.board.definePickable();
        } else {
            // Forcing gameOver to true (in case game has been terminated)
            this.gameOver = true;
            // Otherwise, we remove this game from the games data structure
            GAMES.remove(this);

            // Deleting game backup file here
            File backupFile = new File("backups/" + gameUniqueCode + ".back");
            if (backupFile.exists() && !backupFile.delete())
                System.out.println("ERROR: something went wrong deleting the '" + gameUniqueCode + "' backup file...");
        }

        // Finally, we awake all waiting threads. This will trigger a
        // broadcast of SharedGameState objects to all clients
        synchronized (this.gameLock) {
            this.gameLock.notifyAll();
        }
    }

    /**
     * When called, this method will immediately terminate the game.
     * This could be needed in case of long player disconnections...
     */
    public void terminate() {
        this.gameTerminated = true;
        this.turnIsOver();
    }

    /**
     * getter function for gameOver attribute
     * 
     * @return the gameOver attribute
     */
    public boolean isGameOver() {
        return this.gameOver;
    }

    /**
     * getter method for gameUniqueCode
     * 
     * @return the unique game ID of the game
     */
    public String getGameID() {
        return this.gameUniqueCode;
    }

    /**
     * Method in charge of returning the current player status of the game, that is,
     * the number of players this game takes and the number of players which have
     * actually already joined the game
     * 
     * @return an array of two ints: players who have already joined, total players
     *         of the game
     */
    public int[] getPlayerStatus() {
        int numOfPlayers = this.players.length;

        // Finding how many players have actually already joined the game
        int currPlayers = (int) Arrays.stream(this.players).filter(Objects::nonNull).count();

        return new int[] { currPlayers, numOfPlayers };
    }

    /**
     * Method in charge of looking for, and returning, a Player of the current game
     * given a username
     *
     * @param username username to look for
     * @return an optional of Player
     */
    public Optional<Player> getUserByUsername(String username) {
        return Arrays.stream(players).filter(Objects::nonNull).filter((player) -> player.nickname.equals(username))
                .findFirst();
    }

    /**
     * Method in charge of adding a new player to the current game. Some
     * preconditions are that the player has a unique username and that the
     * game can still accept players
     * 
     * @param newPlayer the player to be added
     * @throws GameAlreadyFullException whenever it is attempted to add a new
     *                                  player, but the game is already full
     */
    public void addNewPlayerToGame(Player newPlayer) throws GameAlreadyFullException {
        int newPlayerIndex = this.getPlayerStatus()[0];

        // If the game is already full, throw an exception
        if (newPlayerIndex >= this.players.length)
            throw new GameAlreadyFullException(this.getPlayerStatus()[1]);

        // Otherwise, add another player
        this.players[newPlayerIndex] = newPlayer;

        // Add a new remote object for this player
        try {
            addRemotePlayer(this.gameUniqueCode, newPlayer.nickname, new GameActionStubImpl(this, newPlayer));
        } catch (RemoteException ex) {
            System.out.println("Error: couldn't add a remote player: " + ex);
        }

        // Checking if the game is now full and it can start
        if (newPlayerIndex == this.players.length - 1) {
            this.gameOngoing = true;

            // Assign each player a private card calling random of RandomGenerator
            // (in order to have a different card for each player)
            int[] random = RandomGenerator.random(players.length);
            for (int i = 0; i < players.length; i++)
                players[i].setPrivateCard(privateCards[random[i]]);

            // Choose randomly a player who will be the first
            this.armchair = (new Random()).nextInt(this.players.length);
            this.currPlayerIndex = this.armchair;

            // At this point, we set up a game board
            this.board.refillBoard(this.players.length);
            this.board.definePickable();

            // Finally, we awake the downlink for client notification
            synchronized (this.gameLock) {
                this.gameLock.notifyAll();
            }
        }
    }

    /**
     * Function in charge of telling the caller if it is a certain player's turn
     * 
     * @param player player of whom we want to know if it is the turn
     * @return whether it is the passed player's turn
     */
    public synchronized boolean actuallyIsPlayersTurn(Player player) {
        return player.equals(this.players[this.currPlayerIndex]);
    }

    /**
     * getter method for board attribute
     * 
     * @return the board associated to this game
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Method in charge of generating a shared version of the current GameState
     *
     * @param player player for whom we want to generate a shared game state
     * @return shared version of the current game state for the requested player
     */
    public SharedGameState getSharedGameState(Player player) {
        SharedGameState sgs = new SharedGameState();

        // Current player information
        sgs.players = new String[this.players.length];
        for (int index = 0; index < this.players.length; index++)
            if (this.players[index] != null)
                sgs.players[index] = this.players[index].nickname;
        sgs.selfPlayerIndex = Arrays.asList(this.players).indexOf(player);
        if (this.gameOngoing)
            sgs.playerStatus = Arrays.stream(this.players).map(Player::isConnected).toArray(Boolean[]::new);

        // Current board information
        sgs.boardContent = this.board.getBoardContent();
        sgs.boardState = this.board.getBoardState();

        // Turn management information
        sgs.currPlayerIndex = this.currPlayerIndex;
        sgs.armchairIndex = this.armchair;
        sgs.isFinalRound = this.finalRound;
        if (this.firstFilledPlayer != null)
            sgs.firstCompleter =  this.firstFilledPlayer.nickname;

        // Common cards information
        sgs.commonsId = new String[this.commonCards.length];
        sgs.commonsDesc = new String[this.commonCards.length];
        for (int index = 0; index < this.commonCards.length; index++) {
            sgs.commonsId[index] = this.commonCards[index].identifier;
            sgs.commonsDesc[index] = this.commonCards[index].description;
        }

        // Private card information (ONLY AVAILABLE IF THE GAME HAS STARTED)
        if (this.gameOngoing) {
            sgs.privateId = player.getPrivateCard().identifier;
            sgs.privateDesc = player.getPrivateCard().description;
            sgs.privateObjectives = player.getPrivateCard().objectives;
        }

        // Library information
        sgs.libraries = new TileType[this.players.length][][];
        for (int index = 0; index < this.players.length; index++)
            if (this.players[index] != null)
                sgs.libraries[index] = this.players[index].getLibrary();

        // Current player's points information
        sgs.commonPts = 0;
        for (int commonOrder : player.commonsOrder)
            if (commonOrder > 0)
                sgs.commonPts += CommonCard.mapCommonPoints(this.players.length, commonOrder);

        sgs.privatePts = player.getPrivatePoints();
        sgs.clusterPts = player.getClusterPoints();
        sgs.firstFilled = player.checkIfFilled();

        // [ONLY VALID DATA IF IT'S THE PLAYER'S TURN] Turn information
        sgs.selectedColumn = player.getSelectedColumn();
        sgs.selectionBuffer = player.getSelectionBufferCopy();

        // Information regarding who achieved common points so far
        sgs.commonsAchievers = new String[this.commonCards.length][this.players.length];
        for (int index = 0; index < this.commonCards.length; index++)
            for (Player p : this.players)
                if (p != null && p.commonsOrder[index] != 0)
                    sgs.commonsAchievers[index][p.commonsOrder[index] - 1] = p.nickname;

        // Lastly, we set the game dynamics attributes
        sgs.gameOngoing = this.gameOngoing;
        sgs.gameSuspended = this.suspended;
        sgs.gameOver = this.gameOver;
        sgs.gameTerminated = this.gameTerminated;

        // If the game is over, we also build a leaderboard
        if (sgs.gameOver) {
            sgs.leaderboard = new HashMap<>();
            for (Player value : this.players) {
                // Player's private, cluster and first-filled-points
                int playerPoints = value.getPrivatePoints() +
                        value.getClusterPoints() +
                        (value.equals(this.firstFilledPlayer) ? 1 : 0);

                // Common objective points
                for (int commonOrder : value.commonsOrder)
                    if (commonOrder > 0)
                        playerPoints += CommonCard.mapCommonPoints(this.players.length, commonOrder);

                sgs.leaderboard.put(value.nickname, playerPoints);
            }
        }

        return sgs;
    }

    /**
     * method that checks if a Player has achieved or not any Common Objectives
     * 
     * @param currPlayerIndex index of player to check for common objectives
     */
    public void obtainedCommons(int currPlayerIndex) {
        // We iterate through all common cards of the current player
        for (int i = 0; i < commonCards.length; i++) {
            // If the player hasn't already obtained the common objective...
            if (players[currPlayerIndex].commonsOrder[i] == 0) {
                // ...We check whether they have completed it now...
                if (commonCards[i].checkObjective(players[currPlayerIndex].getLibrary())) {
                    // ...in which case we assign the current commonOrder value and then increment
                    // it
                    players[currPlayerIndex].commonsOrder[i] = nextCommons[i];
                    nextCommons[i]++;
                }
            }
        }
    }

    /**
     * Implementation of readObject method to restore the transient gameLock
     * attribute
     */
    @Serial
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.gameLock = new Object();
    }

    /**
     * Shorthand version of the obtainedCommons(int) method which calls
     * the obtainedCommons(int) passing this.currPlayerIndex as argument
     */
    public void obtainedCommons() {
        this.obtainedCommons(this.currPlayerIndex);
    }

}
