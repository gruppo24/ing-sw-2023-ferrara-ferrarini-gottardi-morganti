package it.polimi.ingsw.server.controller.jrmi;

import it.polimi.ingsw.common.messages.responses.ResponseStatus;
import it.polimi.ingsw.common.stubs.GameActionStub;
import it.polimi.ingsw.common.stubs.PreGameStub;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

import static it.polimi.ingsw.server.Server.GAMES;
import static it.polimi.ingsw.server.controller.socket.TCPPregameChannel.sendEmptyMessage;

public class PreGameStubImpl extends UnicastRemoteObject implements PreGameStub {

    // We only want ONE registry, so this attribute is static...
    private static Registry reg;

    // Following map will store a list, for each active game, with all the player's remote objects
    private static HashMap<String, LinkedList<GameActionStubImpl>> remotePlayers = new HashMap<>();


    /**
     * Class constructor
     * @param reg jRMI registry to be used
     * @throws RemoteException if jRMI exception
     */
    public PreGameStubImpl (Registry reg) throws RemoteException{
        super();
        PreGameStubImpl.reg = reg;
    }

    /** @see PreGameStub#getAvailableGames() */
    @Override
    public HashMap<String, int[]> getAvailableGames() throws RemoteException{
        HashMap<String, int[]> availableGames = new HashMap<>();
        for (GameState game : GAMES) {
            int[] status = game.getPlayerStatus();
            // Only returning games which still have space for new players
            if (status[0] < status[1]) availableGames.put(game.getGameID(), status);
        }
        return availableGames;
    }

    /** @see PreGameStub#createGame(String, int, String) */ 
    @Override
    public ResponseStatus createGame(String gameID, int numPlayers, String username) throws RemoteException{
        // Checking if a game with the requested gameID already exists
        Optional<GameState> maybeGame = GAMES.stream().filter((game) -> game.getGameID().equals(gameID))
                .findFirst();

        // Checking if the optional returned something or not...
        if (maybeGame.isPresent()) {
            // If it did contain something, we return an error message
            return ResponseStatus.GAME_ID_TAKEN;
        }

        // Otherwise, we actually create a new game
        GameState newGame = new GameState(gameID, numPlayers);
        GAMES.add(newGame);

        // We create a new user for the client
        Player firstPlayer = new Player(username);
        newGame.addNewPlayerToGame(firstPlayer);

        //creating new instance of GameActionStubImpl
        GameActionStubImpl remoteGame = new GameActionStubImpl(newGame, firstPlayer);
        addRemotePlayer(gameID, username, remoteGame);

        // Send a success response message to the client
        return ResponseStatus.SUCCESS;
    }

    /** @see PreGameStub#joinGame */
    @Override
    public ResponseStatus joinGame(String gameID, String username) throws RemoteException{
        // Checking the requested game exists:
        Optional<GameState> maybeGame = GAMES.stream().filter((game) -> game.getGameID().equals(gameID))
                .findFirst();

        // Checking if the optional returned something or not...
        if (maybeGame.isEmpty()) {
            return ResponseStatus.NO_SUCH_GAME_ID;
        }
        GameState game = maybeGame.get();

        // Checking the game has space for a new player
        int[] playerSituation = game.getPlayerStatus();
        if (playerSituation[0] == playerSituation[1]) {
            return ResponseStatus.SELECTED_GAME_FULL;
        }

        // Finally, checking the username requested is unique
        Optional<Player> maybePlayer = game.getUserByUsername(username);
        if (maybePlayer.isPresent()) {
            return ResponseStatus.USERNAME_TAKEN;
        }

        // If we reach this point, we create a new player for the selected game
        Player newPlayer = new Player(username);
        game.addNewPlayerToGame(newPlayer);

        //creating new instance of GameActionStubImpl
        GameActionStubImpl remoteGame = new GameActionStubImpl(game, newPlayer);
        addRemotePlayer(gameID, username, remoteGame);

        // Send a success response message to the client
        return ResponseStatus.SUCCESS;
    }

    /**
     * This method add a new game to the map of remote games
     * @param gameID unique game ID associated to the game we want to add to the map
     */
    public static void addRemoteGame(String gameID) {
        remotePlayers.put(gameID, new LinkedList<>());
    }

    /**
     * This method binds a new remote object for a player and adds this remote instance
     * to the list of remote players of a game
     * @param gameID id of the game to which we are adding a new remote player
     * @param username username of the player we are adding
     * @param remotePlayer remote player which we are adding
     * @throws RemoteException if jRMI exception
     */
    public static void addRemotePlayer(String gameID, String username, GameActionStubImpl remotePlayer) throws RemoteException {
        reg.rebind(gameID + "/" + username, remotePlayer);
        remotePlayers.get(gameID).add(remotePlayer);
    }
}
