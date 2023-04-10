package it.polimi.ingsw.client.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
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
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    public SocketConnection(String host, int port) throws UnknownHostException, IOException {
        super();
        this.socket = new Socket(host, port);
        this.out = new ObjectOutputStream(this.socket.getOutputStream());
        this.in = new ObjectInputStream(this.socket.getInputStream());
    }

    private Object sendPacket(ContentType type, PacketContent content) {
        RequestPacket packet = new RequestPacket(type, content);
        try {
            this.out.writeObject(packet);
            this.out.flush();
            return this.in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            // TODO: handle IOException and reconnection
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, int[]> getAvailableGames() {
        final GamesList response = (GamesList) this.sendPacket(ContentType.LIST_GAMES, new ListGames());
        return response.availableGames;
    }

    @Override
    public ResponseStatus createGame(String username, int numPlayers) {
        final ResponsePacket response = (ResponsePacket) this.sendPacket(ContentType.CREATE_GAME,
                new CreateGame(CreateGame.generateGameID(), username, numPlayers));
        return response.status;
    }

    @Override
    public ResponseStatus connectToGame(String gameID, String username, boolean rejoining) {
        final ResponsePacket response;
        if (rejoining)
            response = (ResponsePacket) this.sendPacket(ContentType.REJOIN_GAME, new RejoinGame(gameID, username));
        else
            response = (ResponsePacket) this.sendPacket(ContentType.JOIN_GAME, new JoinGame(gameID, username));
        return response.status;
    }

    @Override
    public SharedGameState waitTurn() {
        try {
            return (SharedGameState) this.in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public SharedGameState selectColumn(int column) {
        return (SharedGameState) this.sendPacket(ContentType.SELECT_COLUMN, new ColumnSelection(column));
    }

    @Override
    public SharedGameState pickTile(int x, int y) {
        return (SharedGameState) this.sendPacket(ContentType.PICK_TILE, new TilePick(x, y));
    }

    @Override
    public SharedGameState reorder(int first, int second, int third) {
        return (SharedGameState) this.sendPacket(ContentType.REORDER, new Reorder(first, second, third));
    }
}
