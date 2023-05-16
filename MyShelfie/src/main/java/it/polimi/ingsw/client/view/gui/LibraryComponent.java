package it.polimi.ingsw.client.view.gui;


import java.net.URL;
import java.util.ResourceBundle;

import it.polimi.ingsw.client.App;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;


public class LibraryComponent extends VBox implements SGSConsumer, Initializable {
    @FXML
    Label label;
    @FXML
    GridManager library;

    public LibraryComponent() {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("library.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            System.err.println("Couldn't load Library FXML");
            throw new RuntimeException(e);
        }

        IngameController.appendConsumer(this);

        // Attaching event to tile click
        library.setActionHandler((x, y) -> {
            SharedGameState gameState = IngameController.getLastState();
            System.out.println("Selected column: " + x);
            if (gameState.gameOngoing && gameState.currPlayerIndex == gameState.selfPlayerIndex) {
                System.out.println("SENDING COLUMN SELECTION REQUEST");
                IngameController.setGameState(App.connection.selectColumn(x));
            }
        });
    }

    /** @see SGSConsumer#updateSGS(SharedGameState) */
    @Override
    public void updateSGS(SharedGameState sgs) {
        label.setText("Current Player: " + sgs.players[sgs.currPlayerIndex]);
        library.setGridContent(sgs.libraries[sgs.currPlayerIndex]);
    }

    /** @see Initializable#initialize(URL, ResourceBundle) */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText("Current Player: N/A");
    }
}
