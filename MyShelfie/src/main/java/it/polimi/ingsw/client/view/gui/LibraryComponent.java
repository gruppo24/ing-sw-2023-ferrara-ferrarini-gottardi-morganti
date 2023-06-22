package it.polimi.ingsw.client.view.gui;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import it.polimi.ingsw.client.App;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


/**
 * LibraryComponent class to be rendered in the UI
 */
public class LibraryComponent extends VBox implements SGSConsumer, Initializable {

    @FXML
    Label label;
    @FXML
    GridManager library;


    /**
     * Class constructor
     */
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

            // Checking if conditions apply to send request
            if (gameState.gameOngoing && !gameState.gameSuspended &&
                    gameState.currPlayerIndex == gameState.selfPlayerIndex &&
                    (gameState.selectionBuffer == null || gameState.selectionBuffer[0] == null)) {
                System.out.println("SENDING COLUMN SELECTION REQUEST");
                try {
                    IngameController.setGameState(App.connection.selectColumn(x));
                } catch (IOException ex) {
                    System.err.println("REQUEST ERROR: " + ex);
                    Platform.runLater( () -> App.setRoot("main_menu") );
                }
            }
        });
    }

    /** @see SGSConsumer#updateSGS(SharedGameState) */
    @Override
    public void updateSGS(SharedGameState sgs) {
        if (sgs.gameSuspended)
            label.setText("Game suspended...");
        else
            label.setText("Current Player: " + sgs.players[sgs.currPlayerIndex]);

        // Updating current library's content
        library.setGridContent(sgs.libraries[sgs.currPlayerIndex]);

        // Updating column highlighting
        // After successful request, we highlight the selected column
        int column;
        for (Node node : library.getChildren()) {
            // Always remove any previous highlighting Removing
            node.getStyleClass().remove("cell-highlight");

            // Fetching the node's coordinates, and checking whether cell should be highlighted
            column = GridPane.getColumnIndex(node);
            if (sgs.currPlayerIndex == sgs.selfPlayerIndex &&
                    column == sgs.selectedColumn &&
                    sgs.selectionBuffer != null)
                node.getStyleClass().add("cell-highlight");
        }
    }

    /** @see Initializable#initialize(URL, ResourceBundle) */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText("Current Player: N/A");
    }
}
