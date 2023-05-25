package it.polimi.ingsw.client.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.util.Map;

import it.polimi.ingsw.common.messages.requests.*;
import it.polimi.ingsw.common.messages.responses.GamesList;
import it.polimi.ingsw.common.messages.responses.RequestPacket;
import it.polimi.ingsw.common.messages.responses.ResponsePacket;
import it.polimi.ingsw.common.messages.responses.ResponseStatus;
import it.polimi.ingsw.common.messages.responses.SharedGameState;

/**
 * Class that represents the connection via TCP sockets, see {@link Connection}
 * 
 * @author Morganti Tommaso
 */
public class SocketConnection extends Connection {
    private final String host;
    private final int port;

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    /**
     * Class constructor
     *
     * @param host server address
     * @param port socket port
     */
    public SocketConnection(String host, int port) {
        super();
        this.host = host;
        this.port = port;
    }

    @Override
    public void establishConnection() throws IOException {
        // If a socket already exists, we close it first
        if (this.socket != null) {
            try { out.close(); } catch (IOException ex) { System.out.println("Error closing ObjectOutputStream"); }
            try { in.close(); } catch (IOException ex) { System.out.println("Error closing ObjectInputStream"); }
            try { this.socket.close(); } catch(IOException ex) { System.out.println("Error closing socket"); }
        }

        this.socket = new Socket(host, port);
        this.out = new ObjectOutputStream(this.socket.getOutputStream());
        this.in = new ObjectInputStream(this.socket.getInputStream());

        // Start new thread for keep-alive periodic pings
        new Thread(new AsyncKeepAliveEcho(this.out, this)).start();
    }

    /**
     * Method in charge of sending packet of requested type and returning the server's response to it
     *
     * @param type packet type
     * @param content content of the packet
     * @return server response
     * @throws IOException in case of exceptions during packet sending
     */
    private Object sendPacket(ContentType type, PacketContent content) throws IOException {
        // Try sending requested packet
        RequestPacket packet = new RequestPacket(type, content);
        try {
            this.out.writeObject(packet);
            this.out.flush();
        } catch (IOException e) {
            System.out.println("\nServer-side disconnection...\n");
        }

        // Even if packet sending fails, check whether some (perhaps error) message
        // has been received
        try {
            return this.in.readObject();
        } catch (ClassNotFoundException e) {
            // In case of class conversion problems throw and report...
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public Map<String, int[]> getAvailableGames() throws IOException {
        final GamesList response = (GamesList) this.sendPacket(ContentType.LIST_GAMES, new ListGames());
        return response.availableGames;
    }

    @Override
    public ResponseStatus createGame(String gameID, String username, int numPlayers) throws IOException {
        final ResponsePacket response = (ResponsePacket) this.sendPacket(ContentType.CREATE_GAME,
                new CreateGame(gameID, username, numPlayers));
        return response.status;
    }

    @Override
    public ResponseStatus connectToGame(String gameID, String username, boolean rejoining) throws IOException {
        final ResponsePacket response;
        if (rejoining)
            response = (ResponsePacket) this.sendPacket(ContentType.REJOIN_GAME, new RejoinGame(gameID, username));
        else
            response = (ResponsePacket) this.sendPacket(ContentType.JOIN_GAME, new JoinGame(gameID, username));
        return response.status;
    }

    @Override
    public SharedGameState waitTurn() throws IOException {
        try {
            return (SharedGameState) this.in.readObject();
        } catch (ClassNotFoundException e) {
            // In case of class conversion problems throw and report...
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public SharedGameState selectColumn(int column) throws IOException {
        return (SharedGameState) this.sendPacket(ContentType.SELECT_COLUMN, new ColumnSelection(column));
    }

    @Override
    public SharedGameState pickTile(int x, int y) throws IOException {
        return (SharedGameState) this.sendPacket(ContentType.PICK_TILE, new TilePick(x, y));
    }

    @Override
    public SharedGameState reorder(int first, int second, int third) throws IOException {
        return (SharedGameState) this.sendPacket(ContentType.REORDER, new Reorder(first, second, third));
    }
}
