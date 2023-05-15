package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.common.messages.responses.SharedGameState;

public interface SGSConsumer {

    /**
     * Method that updates the GUI with the current state of the game
     *
     * @param sgs the current state of the game
     */
    void update(SharedGameState sgs);
}
