package it.polimi.ingsw.common.stubs;

import it.polimi.ingsw.common.messages.responses.SharedGameState;

import java.rmi.RemoteException;

/**
 * GameActionStub for JRMI
 *
 * @author Ferrara Silvia
 */
public interface GameActionStub {

    SharedGameState waitTurn() throws RemoteException;

    SharedGameState selectColumn(int column) throws RemoteException;

    SharedGameState pickTile(int x, int y) throws RemoteException;

    SharedGameState reorder(int first, int second, int third) throws RemoteException;

}
