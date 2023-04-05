package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import it.polimi.ingsw.server.RandomGenerator;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.exceptions.GameAlreadyFullException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import static it.polimi.ingsw.server.Server.privateCards;

/**
 * Class that describes the state of a specif game
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
    private final Player[] players;

    private final Board board;

    // attributes related to common objectives
    private final int[] nextCommons = { 1, 1 };
    private final CommonCard[] commonCards;

    // Game dynamics attributes
    public final Object gameLock = new Object();
    private boolean gameOver = false;
    private boolean gameOngoing = false;

    /**
     * class constructor
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
        for(int i = 0; i < numOfCard.length; i++){
            this.commonCards[i] = Server.commonCards[numOfCard[i]];
        }
    }

    /**
     * Method to be called after the turn of a player has ended. The
     * method is in charge of performing all the required end-of-turn
     * operations
     */
    public void turnIsOver() {
        // We update the points of the current player
        this.players[this.currPlayerIndex].updatePrivatePoints();
        this.players[this.currPlayerIndex].updateClusterPoints();
        this.obtainedCommons();

        // Checking if this has to be the final round
        this.finalRound = this.players[this.currPlayerIndex].checkIfFilled();

        // We update the player-turn index and check if the game has ended
        this.currPlayerIndex = (this.currPlayerIndex + 1) % this.players.length;
        this.gameOver = this.currPlayerIndex == this.armchair && this.finalRound;

        // Only if the game hasn't ended, update the current board-state
        if (!this.gameOver) {
            if (this.board.shouldBeRefilled()) this.board.refillBoard(this.players.length);
            this.board.definePickable();
        }

        // We also store the current game state on disk (for crash recovery) TODO
        /*
        File backupFile = new File(this.gameUniqueCode + ".bu");
        boolean success = true;
        if (!backupFile.exists()) {
            try {
                success = backupFile.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (success) {
            try {
                FileOutputStream backupFileStream = new FileOutputStream(backupFile);
                ObjectOutputStream gameStateStream = new ObjectOutputStream(backupFileStream);
                gameStateStream.writeObject(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("ERROR: COULDN'T WRITE GAME '" + this.gameUniqueCode + "' TO DISK!");
        }
*/

        // Finally, we awake all waiting threads. This will trigger a
        // broadcast of SharedGameState objects to all clients
        synchronized (this.gameLock) { this.gameLock.notifyAll(); }
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
     * Method in charge of checking whether a player with a given username already
     * exists
     * within current game
     * 
     * @param testUsername username to look for
     * @return whether the provided username has already been used
     */
    public boolean usernameAlreadyUsed(String testUsername) {
        return Arrays.stream(this.players).filter(Objects::nonNull).anyMatch(p -> p.nickname.equals(testUsername));
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
        if (newPlayerIndex >= this.players.length) throw new GameAlreadyFullException(this.getPlayerStatus()[1]);

        // Otherwise, add another player
        this.players[newPlayerIndex] = newPlayer;

        // Checking if the game is now full and it can start
        if (newPlayerIndex == this.players.length - 1) {
            this.gameOngoing = true;
            // TODO: EACH PLAYER IS ASSIGNED A PRIVATE CARD
            // FIXME: FOR NOW, WE ASSIGN THE FIRST PRIVATE CARD TO EVERYONE
            for (Player player : this.players) {
                player.setPrivateCard(privateCards[0]);
            }

            // Choose randomly a player who will be the first
            this.armchair = (new Random()).nextInt(this.players.length);
            this.currPlayerIndex = this.armchair;

            // At this point, we set up a game board
            this.board.refillBoard(this.players.length);
            this.board.definePickable();

            // Finally, we awake the downlink for client notification
            synchronized (this.gameLock) { this.gameLock.notifyAll(); }
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
     * @param player player for whom we want to generate a shared game state
     * @return shared version of the current game state for the requested player
     */
    public SharedGameState getSharedGameState(Player player) {
        SharedGameState sgs = new SharedGameState();

        // Current player information
        sgs.players = new String[this.players.length];
        for (int index=0; index < this.players.length; index++)
            if (this.players[index] != null)
                sgs.players[index] = this.players[index].nickname;

        // Current board information
        sgs.boardContent = this.board.getBoardContent();
        sgs.boardState = this.board.getBoardState();

        // Turn management information
        sgs.currPlayerIndex = this.currPlayerIndex;
        sgs.armchairIndex = this.armchair;
        sgs.isFinalRound = this.finalRound;

        // Common cards information
        sgs.commonsId = new String[this.commonCards.length];
        sgs.commonsDesc = new String[this.commonCards.length];
        for (int index=0; index < this.commonCards.length; index++) {
            sgs.commonsId[index] = this.commonCards[index].identifier;
            sgs.commonsDesc[index] = this.commonCards[index].description;
        }

        // Private card information (ONLY AVAILABLE IF THE GAME HAS STARTED)
        if (this.gameOngoing) {
            sgs.privateId = player.getPrivateCard().identifier;
            sgs.privateDesc = player.getPrivateCard().description;
        }

        // Library information
        sgs.libraries = new TileType[this.players.length][][];
        for (int index=0; index < this.players.length; index++)
            if (this.players[index] != null)
                sgs.libraries[index] = this.players[index].getLibrary();

        // Current player's points information
        sgs.commonPts = 0;
        for (int commonOrder: player.commonsOrder)
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
        for (int index=0; index < this.commonCards.length; index++)
            for (Player p : this.players)
                if (p != null && p.commonsOrder[index] != 0)
                    sgs.commonsAchievers[index][p.commonsOrder[index]] = p.nickname;

        // Lastly, we set the game dynamics attributes
        sgs.gameOngoing = this.gameOngoing;
        sgs.gameOver = this.gameOver;

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
     * Shorthand version of the obtainedCommons(int) method which calls
     * the obtainedCommons(int) passing this.currPlayerIndex as argument
     */
    public void obtainedCommons() {
        this.obtainedCommons(this.currPlayerIndex);
    }

}
