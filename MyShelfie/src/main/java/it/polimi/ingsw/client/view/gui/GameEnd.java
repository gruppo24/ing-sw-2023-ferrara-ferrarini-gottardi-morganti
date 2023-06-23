package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.App;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.util.ResourceBundle;


/**
 * EndGame controller class
 *
 * @author Ferrarini Andrea
 */
public class GameEnd implements Initializable {

    // Interface nodes
    @FXML Label message;
    @FXML Label leaderboard;


    /**
     * Method in charge of re-establishing a connection with the server and
     * switching to the pregame view
     */
    public void backToPregame() {
        try {
            // Re-establishing connection
            App.connection.establishConnection();

            // If re-establishment is successful, we switch to pregame view
            Platform.runLater( () -> App.setRoot("pregame") );
        } catch (IOException | NotBoundException ex) {
            // If re-establishment is unsuccessful, we completely reset the connection...
            System.out.println("ERROR: failed to establish connection with socket server: " + ex);
            App.connection = null;

            // ...and go back to main-menu view
            Platform.runLater( () -> App.setRoot("main_menu") );
        }
    }

    /** @see Initializable#initialize(URL, ResourceBundle) */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("IN INITIALIZE");

        SharedGameState lastState = IngameController.getLastState();

        // Setting the main title text
        message.setText(lastState.gameTerminated ? "Game aborted" : "Game finished!");

        StringBuilder leaderboardString = new StringBuilder();
        if (lastState.leaderboard != null) {
            for (String player : lastState.leaderboard.keySet())
                leaderboardString.append(player)
                        .append(": ")
                        .append(lastState.leaderboard.get(player))
                        .append("\n");
        }
        leaderboard.setText(leaderboardString.toString());
    }
}
