package it.polimi.ingsw.client.view;

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
        // TODO: make this configurable
        this.connection = new SocketConnection("localhost", 5050);
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

            char choice = (char) in.next().charAt(0);
            switch (choice) {
                case '1':
                    createNewGame();
                    break;
                case '2':
                    listGames();
                    break;
                case '3':
                    rejoinGame();
                    break;
                case '4':
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice");
                    menu();
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
     * Prints out a game board with a (kind of) readable grid format
     * 
     * @param board
     */
    public void printBoard(TileType[][] board) {
        for (int i = 0; i < board[0].length; i++) {
            System.out.print(i + " | ");
            for (int j = 0; j < board.length; j++) {
                if (board[j][i] != null)
                    System.out.print(board[j][i] + " ");
                else
                    System.out.print("  ");
            }
            System.out.println();
        }
        System.out.print("    ");
        for (int i = 0; i < board[0].length; i++)
            System.out.print(i + " ");
        System.out.println();
    }

    /**
     * Main game loop, waits for the server to send the game state and prints it out
     */
    public void game() {
        // Non funziona affato, solo un esempio, dobbiamo ancora implementare
        // tutta la cosa del restituire veramente lo shared game state
        SharedGameState gameState = null;
        gameState = this.connection.waitTurn();

        while (!gameState.gameOver) {
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
        }

        System.out.println("GAME IS OVER");
    }
}
