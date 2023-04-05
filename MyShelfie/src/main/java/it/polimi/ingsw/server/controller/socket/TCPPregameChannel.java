package it.polimi.ingsw.server.controller.socket;

import it.polimi.ingsw.common.messages.responses.RequestPacket;
import it.polimi.ingsw.common.messages.responses.ResponsePacket;
import it.polimi.ingsw.common.messages.responses.ResponseStatus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class runs a separate thread for request-response handling
 * from client to server during pregame stage.
 *
 * @author Ferrarini Andrea
 */
public class TCPPregameChannel implements Contextable, Runnable {

    // Reference to the actual output and input channels with the client
    private final ObjectInputStream input;
    private final ObjectOutputStream output;

    /**
     * Class constructor
     * 
     * @param input  ObjectOutputStream associated ton the current downlink channel
     * @param output ObjectInputStream associated to the current uplink channel
     */
    public TCPPregameChannel(ObjectInputStream input, ObjectOutputStream output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public ObjectInputStream getInput() {
        return this.input;
    }

    @Override
    public ObjectOutputStream getOutput() {
        return this.output;
    }

    @Override
    public void run() {
        // This boolean flag will allow us to break out of the request-response loop
        boolean transitionToGame = false;

        while (!transitionToGame) {
            try {
                // We always leave the initiative to the client
                RequestPacket requestPacket = (RequestPacket) this.input.readObject();
                transitionToGame = requestPacket.content.performRequestedAction(this);
            } catch (ClassNotFoundException | IOException ex) {
                System.out.println("[SocketServer] DISCONNECTION IN PREGAME");
                break;
            }
        }
        // This thread can die now...
    }

    /**
     * Method in charge of sending an error response message to the client
     * 
     * @param output the ObjectOutputStream associated to a socket channel
     * @param status status value indicating cause of error
     */
    public static void sendEmptyMessage(ObjectOutputStream output, ResponseStatus status) {
        // Constructing an error response packet
        ResponsePacket responsePacket = new ResponsePacket();
        responsePacket.status = status;
        try {
            output.writeObject(responsePacket);
            output.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
