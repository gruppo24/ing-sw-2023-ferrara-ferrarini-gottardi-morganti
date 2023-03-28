package it.polimi.ingsw.server.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Ferrarini Andrea
 * Class representing a player in a game
 */
public class Player implements Serializable {

    // Attribute required for serialization
    @Serial
    private static final long serialVersionUID = 1L;

    // Game interaction attributes
    public final String nickname;
    private TileType[][] library = new TileType[5][6];
    private PrivateCard privateCard;

    // Point related attribute: we compute them and store them here for faster retrieval
    private int privatePoints = 0;
    private int clusterPoints = 0;
    private int[] commonsOrder = {0, 0};
    private boolean firstFilled = false;

    // Game action attributes
    private int selectedColumn;
    private TileType[] selectionBuffer;


    /**
     * Class constructor
     * @param nickname username to be associated to this user
     */
    public Player(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Method in charge of updating the privatePoints attribute by checking how many private
     * objectives the player has completed and mapping them to an amount of points
     */
    public void updatePrivatePoints() {

    }

    /**
     * Method in charge of updating the clusterPoints attribute by analysing the player's
     * library, finding all clusters and mapping them to an amount of points
     */
    public void updateClusterPoints() {

    }

}
