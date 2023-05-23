package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.App;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * InGame controller class
 */
public class IngameController implements Initializable {
    // list of all components dependent on the shared game state
    private static List<SGSConsumer> consumers = new LinkedList<>();

    private static SharedGameState lastState;

    /**
     * Add a component to the list of components dependent on the shared game state
     * 
     * @param consumer the component to add
     */
    public static void appendConsumer(SGSConsumer consumer) {
        System.out.println("Adding consumer");
        consumers.add(consumer);
    }

    public static void setGameState(SharedGameState gameState) {
        lastState = gameState;
        System.out.println("GameState received, updating " + consumers.size() + " consumers");

        // dispatching update to all shared game state consumers
        Platform.runLater(() -> { // run later bc we are not on the javafx thread
            for (SGSConsumer consumer : consumers) {
                consumer.updateSGS(gameState);
            }
        });

        // if sgs is null, or it is not the player's turn, wait for turn
        if (gameState == null ||
                !gameState.gameOngoing ||
                gameState.gameSuspended ||
                gameState.currPlayerIndex != gameState.selfPlayerIndex)
            setGameState(App.connection.waitTurn());
    }

    /**
     * lastState getter method
     */
    public static SharedGameState getLastState() {
        return lastState;
    }

    @FXML
    HBox upperContainer;
    @FXML
    HBox bottomContainer;

    /** @see Initializable#initialize(URL, ResourceBundle) */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // run the wait turn loop on a separate thread
        new Thread(() -> {
            setGameState(App.connection.waitTurn());
        }).start();
    }
}
