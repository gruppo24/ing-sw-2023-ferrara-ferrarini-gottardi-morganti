package it.polimi.ingsw.server.controller.socket;

import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

import java.net.Socket;

/**
 * Following interface is in charge of exposing getter methods
 * which provide context information for request handling
 *
 * @author Ferrarini Andrea
 */
public interface Contextable {

    /**
     * Method in charge of returning the connection object
     * associated with the current request
     * @return connetcion associated to the current request
     */
    default Socket getConnection() { return null; }

    /**
     * Method in charge of returning the game associated to
     * the current request
     * @return game associated with the request
     */
    default GameState getGame() { return null; }

    /**
     * Method in charge of returning the player associated to
     * the current request
     * @return player associated with the request
     */
    default Player getPlayer() { return null; }
}
