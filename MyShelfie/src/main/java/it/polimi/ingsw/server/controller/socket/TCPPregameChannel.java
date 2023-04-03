package it.polimi.ingsw.server.controller.socket;

import it.polimi.ingsw.common.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class runs a separate thread for request-response handling
 * from client to server during pregame stage.
 *
 * @author Ferrarini Andrea
 */
public class TCPPregameChannel implements Contextable, Runnable {

    // Reference to the actual connection with the client
    private final Socket connection;

    /**
     * Class constructor
     * @param incomingConnection connection established beforehand established with the client
     */
    public TCPPregameChannel(Socket incomingConnection) {
        this.connection = incomingConnection;
    }

    @Override
    public Socket getConnection() {
        return this.connection;
    }

    @Override
    public void run() {
        // This boolean flag will allow us to break out of the request-response loop
        boolean transitionToGame = false;

        try (ObjectInputStream request = new ObjectInputStream(connection.getInputStream())) {
            while (!transitionToGame) {
                try {
                    // We always leave the initiative to the client
                    RequestPacket requestPacket = (RequestPacket) request.readObject();
                    transitionToGame = requestPacket.content.performRequestedAction(this);
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // This thread can die now...
    }

    /**
     * Method in charge of sending an error response message to the client
     * @param connection connection object to be used to send the packet
     * @param status status value indicating cause of error
     */
    public static void sendEmptyMessage(Socket connection, ResponseStatus status) {
        // Constructing an error response packet
        ResponsePacket responsePacket = new ResponsePacket();
        responsePacket.status = status;
        try (ObjectOutputStream response = new ObjectOutputStream(connection.getOutputStream())) {
            response.writeObject(responsePacket);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
