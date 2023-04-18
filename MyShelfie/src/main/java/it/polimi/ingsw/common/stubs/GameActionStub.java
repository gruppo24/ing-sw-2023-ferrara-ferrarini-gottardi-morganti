package it.polimi.ingsw.common.stubs;

import it.polimi.ingsw.common.messages.responses.SharedGameState;

public interface GameActionStub {

    public SharedGameState waitTurn();

    public SharedGameState selectColumn(int column);

    public SharedGameState pickTile(int x, int y);

    public SharedGameState reorder(int first, int second, int third);

}
