package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.common.messages.responses.SharedGameState;

public interface SGSConsumer {

    /**
     * Method that updates the GUI with the current state of the game
     * 
     * This method should be decleratively overriden with the update logic for
     * each component dependent on the game state
     *
     * @param sgs the current state of the game
     */
    void updateSGS(SharedGameState sgs);
}
