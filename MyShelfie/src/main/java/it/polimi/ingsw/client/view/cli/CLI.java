package it.polimi.ingsw.client.view.cli;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import it.polimi.ingsw.client.ReconnectionHandler;
import it.polimi.ingsw.client.controller.Connection;
import it.polimi.ingsw.client.controller.JRMIConnection;
import it.polimi.ingsw.client.controller.SocketConnection;
import it.polimi.ingsw.common.TileState;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.common.messages.responses.ResponseStatus;
import it.polimi.ingsw.common.messages.responses.SharedGameState;

import static it.polimi.ingsw.client.Client.*;


/**
 * CLI class, implements the command line interface for the client
 * It's an all encomassing class for the view via terminal
 * 
 * @author Morganti Tommaso
 * @author Ferrarini Andrea
 */
public class CLI {

    private String myUsername;

    // The connection to the server & scanner to read user input from the terminal
    private Connection connection;
    private final Scanner in = new Scanner(System.in);


    /**
     * Class constructor
     */
    public CLI() {
        // Establish a server connection
        doConnect();
    }

    /**
     * Method in charge of actually connecting to server
     */
    private void doConnect() {
        System.out.println("=== Selected server address: " + SERVER_ADDR + " (0 - change) ===");
        System.out.println("Select server type (1 - Socket, 2 - jRMI):");

        boolean valid = false;
        while (!valid) {
            valid = true;
            char choice = in.next().charAt(0);
            if (choice == '0') {
                System.out.print("New address >>> ");
                SERVER_ADDR = in.next();
                System.out.println("Now select serve type...");
                valid = false;
            } else if (choice == '1') {
                this.connection = new SocketConnection(SERVER_ADDR, SOCKET_PORT);
            } else if (choice == '2') {
                this.connection = new JRMIConnection("localhost", 1059);
            } else {
                System.out.println("Invalid choice...");
                valid = false;
            }

            // If a valid choice has been selected, actually try connecting...
            if (valid) {
                try {
                    // After having selected the type of connection, we actually open a
                    // communication channel with the server
                    this.connection.establishConnection();
                } catch (IOException | NotBoundException ex) {
                    System.out.println("Couldn't reach server... retry later");
                    System.out.println("=== Selected server address: " + SERVER_ADDR + " (0 - change) ===");
                    System.out.println("Select server (1 - Socket, 2 - jRMI):");
                    // After connection failure, enter loop again
                    valid = false;
                }
            }
        }
    }

    /**
     * Pre-game menu, allows the user to create a new game or connect to an existing one
     *
     * @return whether the user has requested the game to terminate or not
     */
    public boolean menu() {
        // Displaying options
        System.out.println("\n\033[1mMyShelfie - Gruppo 24\033[0m");
        System.out.println("1. Create a new game");
        System.out.println("2. List existing games");
        System.out.println("3. Join a game");
        System.out.println("4. Automatic game rejoin");
        System.out.println("5. Manual game rejoin");
        System.out.println("6. Exit");

        char choice = in.next().charAt(0);
        switch (choice) {
            case '1' -> createNewGame();
            case '2' -> listGames();
            case '3' -> joinGame(false);
            case '4' -> joinGame(true);
            case '5' -> manualRejoin();
            case '6' -> {
                System.out.println("Exiting...");
                return true;
            }

            default -> System.out.println("Invalid choice");
        }

        // Unless '6' has been selected, we automatically return false
        return false;
    }

    /**
     * Prints out all the current games on the server and allows the user to join
     * one
     */
    private void listGames() {
        System.out.println("\n\033[1mExisting games:\033[0m");
        Map<String, int[]> games;
        try {
            games = this.connection.getAvailableGames();
        } catch (IOException ex) {
            // In case of server disconnection, retry connecting and return to lobby...
            System.out.println("Couldn't reach server... retry later");
            doConnect();
            return;
        }

        if (games.keySet().size() == 0)
            System.out.println("No games available");
        else
            for (String gameID : games.keySet())
                System.out.println(" - " + gameID + " (" + games.get(gameID)[0] + "/" + games.get(gameID)[1] +")");
    }

    /**
     * Create a new game, asking the num of players and the username
     */
    private void createNewGame() {
        System.out.println("Enter the number of players: ");
        int numPlayers = in.nextInt();
        System.out.println("Enter your username: ");
        String username = in.next();

        String gameID = Connection.generateGameID();
        System.out.println("Creating game with " + numPlayers + " players...");

        ResponseStatus res;
        try {
            res = this.connection.createGame(gameID, username, numPlayers);
        } catch (IOException | NotBoundException ex) {
            // In case of server disconnection, retry connecting and return to lobby...
            System.out.println("Couldn't reach server... retry later");
            doConnect();
            return;
        }

        if (res == ResponseStatus.SUCCESS) {
            this.myUsername = username;

            // After successful game creation, we store our current game session information
            // for possible future game reconnections
            ReconnectionHandler rh = new ReconnectionHandler();
            rh.setParameters(gameID, username);

            this.game();
        } else {
            System.out.println(res);
        }
    }

    /**
     * Joins a game or rejoins an existing game session after a disconnection
     */
    private void joinGame(boolean rejoining) {
        String gameID, username;
        ReconnectionHandler rh = new ReconnectionHandler();

        // Checking if we are joining a game for the first time or rejoining a session
        if (rejoining) {
            // Trying to recover session information here:
            try {
                String[] sessionInformation = rh.getParameters();
                gameID = sessionInformation[0];
                username = sessionInformation[1];
            } catch (FileNotFoundException ex) {
                System.out.println("ERROR: couldn't find recovery file (moved or deleted)");
                return;
            } catch (IOException ex) {
                System.out.println("ERROR: couldn't read recovery file for reconnection...");
                return;
            } catch (ClassNotFoundException ex) {
                System.out.println("ERROR: couldn't decode data stored in recovery file...");
                return;
            }
        } else {
            System.out.println("Enter game ID: ");
            gameID = in.next();
            System.out.println("Enter your username: ");
            username = in.next();
        }

        // If previous operations were successful, (re)join a game
        ResponseStatus res;
        try {
            res = this.connection.connectToGame(gameID, username, rejoining);
        } catch (IOException | NotBoundException ex) {
            // In case of server disconnection, retry connecting and return to lobby...
            System.out.println("Couldn't reach server... retry later");
            doConnect();
            return;
        }

        if (res == ResponseStatus.SUCCESS) {
            this.myUsername = username;

            // After successful game (re)joining, we store our current game session information
            // for possible future game reconnections
            rh.setParameters(gameID, username);

            this.game();
        } else {
            System.out.println(res);
        }

    }

    /**
     * Rejoins a game manually (asks for gameID and username) ==> to be used only for debugging
     */
    private void manualRejoin() {
        System.out.println("=== MANUAL REJOIN (debugging only) ===\nEnter game ID and username: ");
        String gameID = in.next();
        String username = in.next();

        ResponseStatus res;
        try {
            res = this.connection.connectToGame(gameID, username, true);
        } catch (IOException | NotBoundException ex) {
            // In case of server disconnection, retry connecting and return to lobby...
            System.out.println("Couldn't reach server... retry later");
            doConnect();
            return;
        }

        if (res == ResponseStatus.SUCCESS) {
            this.myUsername = username;

            // After successful game (re)joining, we store our current game session information
            // for possible future game reconnections
            ReconnectionHandler rh = new ReconnectionHandler();
            rh.setParameters(gameID, username);

            this.game();
        } else {
            System.out.println(res);
        }

    }

    /**
     * Main game loop, waits for the server to send the game state and prints it out
     */
    private void game() {
        // As soon as we enter the game loop, we check whether it is our turn
        SharedGameState gameState;
        try {
            gameState = this.connection.waitTurn();
        } catch (IOException ex) {
            // In case of server disconnection, retry connecting and return to lobby...
            System.out.println("Couldn't reach server... retry later");
            doConnect();
            return;
        }

        int myIndex;
        while (!gameState.gameOver) {
            // We clear the terminal before printing anything
            CLIUtils.clearScreen();

            // We print the game state ONLY if the game has started
            if (gameState.gameOngoing) this.printSharedGameState(gameState);
            else System.out.println("Game not started... waiting for players");

            // Checking if the game has started OR if our turn hasn't come yet
            myIndex = Arrays.asList(gameState.players).indexOf(this.myUsername);
            try {
                gameState = (!gameState.gameOngoing || gameState.gameSuspended || myIndex != gameState.currPlayerIndex) ?
                        this.connection.waitTurn() :
                        handleTurn(gameState);
            } catch (IOException ex) {
                // In case of server disconnection, retry connecting and return to lobby...
                System.out.println("Couldn't reach server... retry later");
                doConnect();
                return;
            }
        }

        if (gameState.gameTerminated)
            System.out.println("=== GAME TERMINATED (one or more players disconnected...) ===\nCurrent points were:");
        else
            System.out.println("=== GAME IS OVER ===\nFinal leaderboard:");

        for (String player : gameState.leaderboard.keySet())
            System.out.println(" ".repeat(4) + "- " + player + ": " + gameState.leaderboard.get(player) + "pts");
        System.out.println("\nPress any key to go back to menu...");
        in.next();
        CLIUtils.clearScreen();

        // TCP/jRMI communication channel will be closed when the game is terminated.
        // For this reason, we reopen a new connection
        try {
            // Initially, simply try reconnecting to same server automatically...
            this.connection.establishConnection();
        } catch (IOException | NotBoundException ex) {
            // In case of failure, call doConnect and request a potential server change
            System.out.println("Couldn't reach server... retry later");
            doConnect();
        }
    }

    /**
     * Method in charge of rendering a SharedGameState for the CLI interface
     * @param game SharedGameState instance to render
     */
    private void printSharedGameState(SharedGameState game) {
        int myIndex = 0;
        for (String player : game.players)
            if (player.equals(this.myUsername))
                break;
            else
                myIndex++;

        // Printing who is the first player of the game
        if (myIndex == game.armchairIndex) System.out.println(CLIUtils.makeItalic("You have the armchair!"));
        else System.out.println(CLIUtils.makeItalic(game.players[game.armchairIndex] + " has the armchair"));

        // Printing whether this is the final round of the game
        if (game.isFinalRound)
            System.out.println(CLIUtils.makeBold(CLIUtils.color(
                    CLIUtils.color("FINAL ROUND OF THE GAME", CLIColor.ANSI_BACKGROUND_RED)
                    , CLIColor.ANSI_BLACK))
            );
        else
            System.out.println();

        // At first, we print our own library along with
        // our selectionBuffer and selectedColumn, if they are
        // present
        System.out.println();   // New line
        if (game.selectionBuffer != null) {
            System.out.println(" ".repeat(4) + " ".repeat(2 * game.selectedColumn) + "*");
        } else {
            System.out.println();  // Empty line
        }
        for (int row = game.libraries[myIndex][0].length - 1; row >= 0; row--) {
            System.out.print(row + " | ");
            for (int column = 0; column < game.libraries[myIndex].length; column++) {
                TileType tile = game.libraries[myIndex][column][row];
                if (tile == null)
                    System.out.print("  ");
                else
                    System.out.print(CLIUtils.tilePickable(tile));
            }
            // Depending on the row, we print further information for the user
            if (row == 3 && game.selectionBuffer != null) {
                // If we have reached the third row, we also print our selection buffer
                System.out.print(" ".repeat(8) + "Tiles picked: [");
                for (TileType tile : game.selectionBuffer)
                    if (tile == null)
                        System.out.print("  ");
                    else
                        System.out.print(tile + " ");
                System.out.print("]");
            } else if (row == 2) {
                // If we have reached the fourth row, we also print our cluster points
                System.out.print(" ".repeat(8) + CLIUtils.makeBold(game.clusterPts + " cluster points obtained"));
            } else if (row == 1) {
                // If we have reached the fifth row, we also print our common points
                System.out.print(" ".repeat(8) + CLIUtils.makeBold(game.commonPts + " common points obtained"));
            }
            System.out.println();   // New line
        }
        System.out.print(" ".repeat(4));
        for (int column = 0; column < game.libraries[myIndex].length; column++) System.out.print("--");
        System.out.print("\n    ");
        for (int column = 0; column < game.libraries[myIndex].length; column++) System.out.print(column + " ");
        System.out.println("\n");

        // Now printing all libraries of the other players
        for (int row = game.libraries[myIndex][0].length - 1; row >= 0; row--) {
            System.out.print(row + " | ");
            for (int libraryIndex = 0; libraryIndex < game.libraries.length; libraryIndex++) {
                if (libraryIndex != myIndex) {
                    for (int column = 0; column < game.libraries[libraryIndex].length; column++) {
                        TileType tile = game.libraries[libraryIndex][column][row];
                        if (tile == null)
                            System.out.print("  ");
                        else
                            System.out.print(CLIUtils.tilePickable(tile));
                    }
                    System.out.print(" ".repeat(8));
                }
            }
            System.out.println();
        }

        // Building bottom edge
        System.out.print(" ".repeat(4));
        for (int playerIndex = 0; playerIndex < game.players.length - 1; playerIndex++) {
            for (int column = 0; column < game.libraries[playerIndex].length; column++) {
                System.out.print("--");
            }
            System.out.print(" ".repeat(8));
        }
        System.out.print("\n    ");
        for (int playerIndex = 0; playerIndex < game.players.length - 1; playerIndex++) {
            for (int column = 0; column < game.libraries[playerIndex].length; column++) {
                System.out.print(column + " ");
            }
            System.out.print(" ".repeat(8));
        }
        System.out.println();

        // Printing other players' usernames and connection status
        System.out.print(" ".repeat(4));
        for (int playerIndex=0; playerIndex < game.players.length; playerIndex++) {
            if (playerIndex != myIndex) {
                String player = game.players[playerIndex];

                if (player.length() > 2 * game.libraries[myIndex].length)   // Trimming player username if necessary
                    player = player.substring(0, 2 * game.libraries[myIndex].length) + "...";
                int padding = 2*game.libraries[myIndex].length - player.length();

                if (playerIndex == game.currPlayerIndex)    // If it's the players turn, we make their username bold
                    player = CLIUtils.makeUnderlined(CLIUtils.makeBold(player));

                System.out.print(player);
                System.out.print(game.playerStatus[playerIndex] ?
                    CLIUtils.color("*", CLIColor.ANSI_GREEN) : CLIUtils.color("*", CLIColor.ANSI_RED)
                );
                System.out.print(" ".repeat(padding + 7));
            }
        }
        System.out.println("\n");   // New line

        // Finally, printing the board
        printBoard(game.boardContent, game.boardState);

        // Before returning, we print the current common and private objectives
        System.out.println();
        for (int commonIndex=0; commonIndex < game.commonsDesc.length; commonIndex++) {
            System.out.println(CLIUtils.makeBold("Common objective: ") + game.commonsDesc[commonIndex]);
            System.out.print(" ".repeat(8) + "Obtained by: ");
            for (String commonAchiever : game.commonsAchievers[commonIndex])
                if (commonAchiever != null)
                    System.out.print(CLIUtils.makeBold(commonAchiever) + " ");
            System.out.println();
        }
        System.out.print(CLIUtils.makeBold("Your private objective: ") + game.privateDesc);
        System.out.println(CLIUtils.makeBold(CLIUtils.makeItalic(" (" + game.privatePts + "pts obtained so far)")));
    }

    /**
     * Prints out a game board with a readable grid format
     *
     * @param boardContent board content
     * @param boardState current board state (which tiles are PICKABLE,
     *                   PICKABLE_NEXT, NOT_PICKABLE)
     */
    private void printBoard(TileType[][] boardContent, TileState[][] boardState) {
        for (int i = boardContent[0].length-1; i >= 0; i--) {
            System.out.print(i + " | ");
            for (int j = 0; j < boardContent.length; j++) {
                if (boardContent[j][i] != null) {
                    if (boardState[j][i] == TileState.PICKABLE)
                        System.out.print(CLIUtils.tilePickable(boardContent[j][i]));
                    else if (boardState[j][i] == TileState.PICKABLE_NEXT)
                        System.out.print(CLIUtils.tilePickableNext(boardContent[j][i]));
                    else
                        System.out.print(CLIUtils.tileNotPickable(boardContent[j][i]));
                } else
                    System.out.print("  ");
            }
            System.out.println();
        }
        System.out.print(" ".repeat(4));
        for (int i = 0; i < boardContent[0].length; i++) System.out.print("--");
        System.out.print("\n    ");
        for (int i = 0; i < boardContent[0].length; i++) System.out.print(i + " ");
        System.out.println();
    }

    /**
     * Method in charge of handling user interactions whenever it is their turn
     * @param game latest SharedGameState instance
     * @return instance of SharedGameState after user interaction
     * @throws IOException in case of connection issues
     */
    private SharedGameState handleTurn(SharedGameState game) throws IOException {
        // If it actually is the player's turn, we, at first,
        // check they have selected a column
        if (game.selectionBuffer == null) {
            System.out.print("Select a column > ");
            return connection.selectColumn(CLIUtils.safeNextInt(in));
        }

        // Checking if it is possible to pick tiles
        if (this.areThereTilesPickable(game.boardState) && game.selectionBuffer[game.selectionBuffer.length-1] == null) {
            System.out.print("Pick a tile - tile x coordinate (-1 if done): ");
            int x = CLIUtils.safeNextInt(in);
            System.out.print("Pick a tile - tile y coordinate (also -1 if done): ");
            int y = CLIUtils.safeNextInt(in);

            // Checking if the player has actually picked a tile
            if (x != -1 || y != -1) return this.connection.pickTile(x, y);
        }

        // Checking if some tiles have actually been selected...
        if (game.selectionBuffer[0] != null) {
            int[] r = {0, 1, 2};
            for (int index = 0; index < Arrays.stream(game.selectionBuffer).filter(Objects::nonNull).count(); index++) {
                System.out.println("Index of tile number " + index + " to insert in library");
                r[index] = CLIUtils.safeNextInt(in);
            }
            return connection.reorder(r[0], r[1], r[2]);
        }

        // If no action has been performed, we simply return the same SharedGameState instance
        return game;
    }

    /**
     * This method is in charge of checking whether any tile is pickable withing a given board
     * @param boardState board to check
     * @return whether any tile is in state TileState.PICKABLE
     */
    private boolean areThereTilesPickable(TileState[][] boardState) {
        for (TileState[] column : boardState)
            for (TileState tile : column)
                if (tile == TileState.PICKABLE)
                    return true;
        return false;
    }

}
