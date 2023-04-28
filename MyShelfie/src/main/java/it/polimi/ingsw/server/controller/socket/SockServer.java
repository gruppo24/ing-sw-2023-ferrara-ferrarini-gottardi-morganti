package it.polimi.ingsw.server.controller.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class will act as a dispatcher for incoming connections on the socket server.
 * All it does is accept all incoming connection requests, instance a new thread
 * for them, and continue looping.
 *
 * @author Ferrarini Andrea
 */
public class SockServer implements Runnable {

    private final int port;

    /**
     * Class constructor
     * @param port port to bind when starting the server
     */
    public SockServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        // As soon as we enter the parallel thread, we start
        // our socket server
        try (ServerSocket dispatcher = new ServerSocket(this.port)) {
            System.out.println("[SockServer] socket server bound to port " + this.port);
            System.out.println("[SockServer] entering connection-accepting loop...");
            while (true) {
                Socket incomingConnection = dispatcher.accept();

                // Setting a 10 seconds timeout (should be enough...)
                incomingConnection.setSoTimeout(10_000);

                // As soon as a new connection has been received,
                // we send the client in pregame-state
                Thread clientChannelThread = new Thread(new TCPPregameChannel(
                        new ObjectInputStream(incomingConnection.getInputStream()),
                        new ObjectOutputStream(incomingConnection.getOutputStream())
                ));
                clientChannelThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
