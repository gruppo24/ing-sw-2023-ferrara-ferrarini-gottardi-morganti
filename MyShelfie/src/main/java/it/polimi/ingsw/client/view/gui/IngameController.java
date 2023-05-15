package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.App;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
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

    private static SharedGameState gameState;
    private static List<SGSConsumer> consumers;

    private static void resetConsumers() {
        consumers = new LinkedList<>();
    }

    public static void appendConsumer(SGSConsumer consumer) {
        consumers.add(consumer);
    }

    public static void setGameState(SharedGameState gameState) {
        System.out.println("GameState recieved, updating " + consumers.size() + " consumers");
        IngameController.gameState = gameState;

        // dispatching update to all shared game state consumers
        for (SGSConsumer consumer : consumers) {
            consumer.update(IngameController.gameState);
        }

        // if sgs is null, or it is not the player's turn, wait for turn
        if (gameState == null || gameState.currPlayerIndex != gameState.selfPlayerIndex)
            setGameState(App.connection.waitTurn());
    }

    @FXML
    HBox upperContainer;
    @FXML
    HBox bottomContainer;

    /** @see Initializable#initialize(URL, ResourceBundle) */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final Thread t = new Thread(() -> {
            setGameState(null);
        });
        t.start();
        resetConsumers();
        BoardComponent board = new BoardComponent();
        upperContainer.getChildren().add(board);
    }

}
