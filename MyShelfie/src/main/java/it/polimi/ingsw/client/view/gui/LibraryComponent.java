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
    }

    @Override
    public void updateSGS(SharedGameState sgs) {
        if (sgs != null) {
            label.setText("Current Player: " + sgs.players[sgs.currPlayerIndex]);
            library.setGridContent(sgs.libraries[sgs.currPlayerIndex]);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText("Current Player: N/A");
    }
}
