package it.polimi.ingsw.common.stubs;

import it.polimi.ingsw.common.messages.responses.SharedGameState;

/**
 * GameActionStub for JRMI
 *
 * @author Ferrara Silvia
 */
public interface GameActionStub {

    SharedGameState waitTurn();

    SharedGameState selectColumn(int column);

    SharedGameState pickTile(int x, int y);

    SharedGameState reorder(int first, int second, int third);

}
