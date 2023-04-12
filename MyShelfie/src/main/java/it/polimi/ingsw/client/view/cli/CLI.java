package it.polimi.ingsw.client.view.cli;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Scanner;

import it.polimi.ingsw.client.controller.Connection;
import it.polimi.ingsw.client.controller.SocketConnection;
import it.polimi.ingsw.common.TileState;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.common.messages.responses.ResponseStatus;
import it.polimi.ingsw.common.messages.responses.SharedGameState;

import javax.swing.*;

/**
 * CLI class, implements the command line interface for the client
 * It's an all encomassing class for the view via terminal
 * 
 * @author Morganti Tommaso
 */
public class CLI {

    private String myUsername;

    /**
     * The connection to the server
     */
    Connection connection;
    /**
     * Scanner to read user input from the terminal
     */
    Scanner in = new Scanner(System.in);

    public CLI() throws UnknownHostException, IOException {
        System.out.println("Select server (1 - Socket, 2 - jRMI):");
        char choice = in.next().charAt(0);
        if (choice == '1') {
            this.connection = new SocketConnection("localhost", 5050);
        } else if (choice == '2') {
            this.connection = new SocketConnection("localhost", 5050);
            //this.connection = new JRMIConnection("localhost", 1059);
        }

    }

    /**
     * Pre-game menu, allows the user to create a new game or connect to an existing
     * one
     * 
     * @throws IOException
     */
    public void menu() throws IOException {
        while (true) {
            System.out.println("\n\033[1mMyShelfie - Gruppo 24\033[0m");
            System.out.println("1. Create a new game");
            System.out.println("2. List existing games");
            System.out.println("3. Rejoin a game");
            System.out.println("4. Exit");

            char choice = in.next().charAt(0);
            switch (choice) {
                case '1' -> createNewGame();
                case '2' -> listGames();
                case '3' -> rejoinGame();
                case '4' -> {
                    System.out.println("Exiting...");
                    System.exit(0);
                }
                default -> {
                    System.out.println("Invalid choice");
                    menu();
                }
            }
        }
    }

    /**
     * Prints out all the current games on the server and allows the user to join
     * one
     * 
     * @throws IOException
     */
    private void listGames() throws IOException {
        System.out.println("\n\033[1mExisting games:\033[0m");
        Map<String, int[]> games = this.connection.getAvailableGames();
        String[] gameIDs = games.keySet().toArray(new String[0]);
        if (gameIDs.length == 0) {
            System.out.println("No games available");
            menu();
        } else {
            for (int i = 0; i < gameIDs.length; i++) {
                System.out.println((i + 1) + ")" + gameIDs[i] + " " + games.get(gameIDs[i])[0] + "/"
                        + games.get(gameIDs[i])[1] + " players");
            }
            System.out.println("Enter the number of the game you want to join: ");
            int choice = in.nextInt();
            System.out.println("Enter your username: ");
            String username = in.next();

            System.out.println("Connecting to game " + gameIDs[choice - 1] + "...");

            ResponseStatus res = this.connection.connectToGame(gameIDs[choice - 1], username, false);
            if (res == ResponseStatus.SUCCESS) {
                this.myUsername = username;
                this.game();
            } else {
                System.out.println(res);
            }
        }
    }

    /**
     * Create a new game, asking the num of players and the username
     * 
     * @throws IOException
     */
    public void createNewGame() throws IOException {
        System.out.println("Enter the number of players: ");
        int numPlayers = in.nextInt();
        System.out.println("Enter your username: ");
        String username = in.next();

        System.out.println("Creating game with " + numPlayers + " players...");

        ResponseStatus res = this.connection.createGame(username, numPlayers);
        if (res == ResponseStatus.SUCCESS) {
            this.myUsername = username;
            this.game();
        } else {
            System.out.println(res);
        }
    }

    public void rejoinGame() throws IOException {
        System.out.println("Enter game ID: ");
        String gameID = in.next();
        System.out.println("Enter your username: ");
        String username = in.next();

        ResponseStatus res = this.connection.connectToGame(gameID, username, true);
        if (res == ResponseStatus.SUCCESS) {
            this.myUsername = username;
            this.game();
        } else {
            System.out.println(res);
        }
    }

    /**
     * Main game loop, waits for the server to send the game state and prints it out
     */
    public void game() {
        // As soon as we enter the game loop, we check whether it is our turn
        SharedGameState gameState = this.connection.waitTurn();

        while (!gameState.gameOver) {
            // We print the game state ONLY if the game has started
            CLIUtils.clearScreen();  // We clear the terminal before printing anything
            if (gameState.gameOngoing) this.printSharedGameState(gameState);
            else System.out.println("Game not started... waiting for players");
            gameState = this.connection.waitTurn();

            /*
            // Loop a long as it isn't the player's turn OR the game hasn't started
            while (!gameState.players[gameState.currPlayerIndex].equals(this.myUsername) || !gameState.gameOngoing) {
                System.out.println("=== NOT MY TURN ===");
                gameState = this.connection.waitTurn();
            }

            System.out.println("=== MY TURN ===");
            this.printBoard(gameState.boardContent);

            System.out.println("Choose column:");
            int column = this.in.nextInt();
            this.connection.selectColumn(column);

            // FIXME: LIMITING PICK TO ONE TILE AT THE TIME...
            System.out.println("Choose tile (x first, then y):");
            int x = this.in.nextInt();
            int y = this.in.nextInt();
            while (gameState.boardState[x][y] != TileState.PICKABLE) {
                System.out.println("INVALID CHOICE!!");
                x = this.in.nextInt();
                y = this.in.nextInt();
            }
            this.connection.pickTile(x, y);

            // FIXME: REORDER IS STATIC
            gameState = this.connection.reorder(0, 1, 2);
            this.printBoard(gameState.boardContent);
             */
            }

            System.out.println("GAME IS OVER");
        }

    /**
     * Method in charge of rendering a SharedGameState for the CLI interface
     * @param game SharedGameState instance to render
     */
    public void printSharedGameState(SharedGameState game) {
        int myIndex = 0;
        for (String player : game.players)
            if (player.equals(this.myUsername))
                break;
            else
                myIndex++;

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
            // If we have reached the third row, we also print our selection buffer
            if (row == 3 && game.selectionBuffer != null) {
                System.out.print(" ".repeat(8) + "[");
                for (TileType tile : game.selectionBuffer)
                    if (tile == null)
                        System.out.print("  ");
                    else
                        System.out.print(tile + " ");
                System.out.print("]");
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

        // Printing other players' usernames
        System.out.print(" ".repeat(4));
        for (int playerIndex=0; playerIndex < game.players.length; playerIndex++) {
            if (playerIndex != myIndex) {
                String player = game.players[playerIndex];

                if (player.length() > 2 * game.libraries[myIndex].length)   // Trimming player username if necessary
                    player = player.substring(0, 2 * game.libraries[myIndex].length) + "...";
                int padding = 2*game.libraries[myIndex].length - player.length();

                if (playerIndex == game.currPlayerIndex)    // If it's the players turn, we make their username bold
                    player = CLIUtils.makeBold(player);

                System.out.print(player);
                System.out.print(" ".repeat(padding + 8));
            }
        }
        System.out.println("\n");   // New line

        // Finally, printing the board
        printBoard(game.boardContent, game.boardState);
    }

    /**
     * Prints out a game board with a readable grid format
     *
     * @param boardContent board content
     * @param boardState current board state (which tiles are PICKABLE,
     *                   PICKABLE_NEXT, NOT_PICKABLE)
     */
    private void printBoard(TileType[][] boardContent, TileState[][] boardState) {
        for (int i = 0; i < boardContent[0].length; i++) {
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
}
