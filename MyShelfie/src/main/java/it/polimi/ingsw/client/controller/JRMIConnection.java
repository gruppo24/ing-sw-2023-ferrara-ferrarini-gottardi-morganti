package it.polimi.ingsw.client.controller;

import it.polimi.ingsw.common.messages.responses.ResponseStatus;
import it.polimi.ingsw.common.messages.responses.SharedGameState;

import java.util.Map;

public class JRMIConnection extends Connection{
    public JRMIConnection(){
        super();
    }

    @Override
    public Map<String, int[]> getAvailableGames() {
        return null;
    }

    @Override
    public ResponseStatus createGame(String username, int numPlayers) {
        return null;
    }

    @Override
    public ResponseStatus connectToGame(String gameId, String username, boolean rejoining){
        return null;
    }

    @Override
    public SharedGameState waitTurn(){
        return null;
    }

    @Override
    public SharedGameState selectColumn(int column){
        return null;
    }

    @Override
    public SharedGameState pickTile(int x, int y){
        return null;
    }

    @Override
    public SharedGameState reorder(int first, int second, int third){
        return null;
    }
}
