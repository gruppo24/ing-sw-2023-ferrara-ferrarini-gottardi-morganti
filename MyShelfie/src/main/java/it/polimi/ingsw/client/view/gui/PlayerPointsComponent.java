package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.App;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * PlayerPoints component class, renders the points of the player in the GUI
 */
public class PlayerPointsComponent extends VBox implements SGSConsumer, Initializable {

    @FXML
    Label player;
    @FXML
    Label privatePoints;

    @FXML
    Label commonPoints;

    @FXML
    Label clusterPoints;

    @FXML
    Label firstLibraryFilledPoint;

    public PlayerPointsComponent(){
        FXMLLoader loader = new FXMLLoader(App.class.getResource("playerPoints.fxml"));
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
        player.setText("Player " + sgs.players[sgs.selfPlayerIndex]);
        privatePoints.setText("Private points: " + sgs.privatePts);
        commonPoints.setText("Common points: " + sgs.commonPts);
        clusterPoints.setText("Cluster points: " + sgs.clusterPts);
        if (sgs.firstFilled)
            firstLibraryFilledPoint.setText("First library filled point: 1");
        else
            firstLibraryFilledPoint.setText("First library filled point: 0");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        player.setText("Player: N/A");
        privatePoints.setText("Private points: 0");
        commonPoints.setText("Common points: 0");
        clusterPoints.setText("Cluster points: 0");
        firstLibraryFilledPoint.setText("First library filled point: 0");
    }


}
