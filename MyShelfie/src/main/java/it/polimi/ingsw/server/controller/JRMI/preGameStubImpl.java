package it.polimi.ingsw.server.controller.JRMI;

import it.polimi.ingsw.common.messages.responses.GamesList;
import it.polimi.ingsw.common.messages.responses.ResponseStatus;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.server.Server.GAMES;
import static it.polimi.ingsw.server.controller.socket.TCPPregameChannel.sendEmptyMessage;

public class preGameStubImpl extends UnicastRemoteObject implements preGameStub{
    protected preGameStubImpl() throws RemoteException {
        super();
    }

    @Override
    public Map<String, int[]> getAvailableGames() throws RemoteException {
        GamesList response = new GamesList();
        response.availableGames = new HashMap<>();
        for(GameState game: GAMES){
            response.availableGames.put(game.getGameID(), game.getPlayerStatus());
        }
        response.status = ResponseStatus.SUCCESS;
        return response.availableGames;
    }
    @Override
    public ResponseStatus createGame(String username, int numPlayers) throws RemoteException{
        GameState game = new GameState("game- "+ System.currentTimeMillis(),numPlayers);
        GAMES.add(game);
        Player firstPlayer = new Player(username);
        game.addNewPlayerToGame(firstPlayer);
        return ResponseStatus.SUCCESS;
    }
    @Override
    public ResponseStatus connectToGame(String gameId, String username, boolean rejoining) throws RemoteException{
        return null;
    }
}
