package it.polimi.ingsw.server.controller.JRMI;

import it.polimi.ingsw.common.messages.responses.SharedGameState;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface gameActionStub extends Remote {

    SharedGameState waitTurn() throws RemoteException;

    SharedGameState selectColumn(int column) throws RemoteException;

    SharedGameState pickTile(int x, int y) throws RemoteException;

    SharedGameState reorder(int first, int second, int third) throws RemoteException;

}
