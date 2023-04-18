package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Following interface is in charge of exposing getter methods
 * which provide context information for request handling
 *
 * @author Ferrarini Andrea
 */
public interface Contextable {

    /**
     * Method in charge of returning an object input stream associated to the current socket connection
     * @return the ObjectInputStream associated to the current channel
     */
    ObjectInputStream getInput();

    /**
     * Method in charge of returning an object output stream associated to the current socket connection
     * @return the ObjectOutputStream associated to the current channel
     */
    ObjectOutputStream getOutput();

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
