package it.polimi.ingsw.common.stubs;

import it.polimi.ingsw.common.messages.responses.SharedGameState;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * GameActionStub for jRMI
 *
 * @author Ferrara Silvia
 */
public interface GameActionStub extends Remote {

    /**
     * Method in charge of requesting a shared game state without waiting next
     * gameLock wakeup
     *
     * @return current shared game state
     * @throws RemoteException if exception in jRMI
     */
    SharedGameState getSharedGameStateImmediately() throws RemoteException;

    /**
     * Method in charge of terminating a disconnection-timer thread after reconnection
     *
     * @throws RemoteException if exception in jRMI
     */
    void resetDisconnectionTimer() throws RemoteException;

    /**
     * Method in charge of queuing a shared game state request. SGS will be sent
     * at next gameLock wakeup
     *
     * @return shared game state object
     * @throws RemoteException if exception in jRMI
     */
    SharedGameState waitTurn() throws RemoteException;

    /**
     * Method in charge of selecting a column during player's turn
     *
     * @param column picked column
     * @return updated shared game state
     * @throws RemoteException if exception in jRMI
     */
    SharedGameState selectColumn(int column) throws RemoteException;

    /**
     * Method in charge of picking a tile from the board during player's turn
     *
     * @param x x coordinate of the tile
     * @param y y coordinate of the tile
     * @return updated shared game state
     * @throws RemoteException  if exception in jRMI
     */
    SharedGameState pickTile(int x, int y) throws RemoteException;

    /**
     * Method in charge of reording selection buffer before inserting tiles in
     * player's grid during their turn
     *
     * @param first first tile index
     * @param second second tile index
     * @param third third tile index
     * @return updated shared game state
     * @throws RemoteException if exception in jRMI
     */
    SharedGameState reorder(int first, int second, int third) throws RemoteException;

}
