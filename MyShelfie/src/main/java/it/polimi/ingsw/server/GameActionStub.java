package it.polimi.ingsw.server;

import it.polimi.ingsw.common.messages.responses.SharedGameState;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface gameActionStub extends Remote {

    public SharedGameState waitTurn() throws RemoteException;

    public SharedGameState selectColumn(int column) throws RemoteException;

    public SharedGameState pickTile(int x, int y) throws RemoteException;

    public SharedGameState reorder(int first, int second, int third) throws RemoteException;

}
