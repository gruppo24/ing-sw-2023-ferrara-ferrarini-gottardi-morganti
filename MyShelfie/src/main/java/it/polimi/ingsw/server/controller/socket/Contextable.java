package it.polimi.ingsw.server.controller.socket;

import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

import java.net.Socket;

public interface Contextable {
    default Socket getConnection() { return null; }
    default GameState getGame() { return null; }
    default Player getPlayer() { return null; }
}
