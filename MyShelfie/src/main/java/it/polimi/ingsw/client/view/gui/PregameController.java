package it.polimi.ingsw.client.view.gui;


import it.polimi.ingsw.client.App;
import it.polimi.ingsw.client.ReconnectionHandler;
import it.polimi.ingsw.client.controller.Connection;
import it.polimi.ingsw.client.view.gui.gameslist.ListViewCell;
import it.polimi.ingsw.common.messages.responses.ResponseStatus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import static it.polimi.ingsw.client.App.requestLock;


/**
 * Pregame menu controller class
 */
public class PregameController implements Initializable {

    // Interface nodes
    @FXML TextField username;
    @FXML Label errorMessage;
    @FXML Spinner<Integer> numOfPlayers;
    @FXML ListView<Entry<String, int[]>> gameList;

    // Reference to the scene
    private static PregameController self;


    /** @see Initializable#initialize(URL, ResourceBundle)  */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // As soon as the scene is set, we request all available games
        this.listGames();

        // Creating a reference to the class instance
        PregameController.self = this;
    }

    /**
     * Method in charge of joining a game
     *
     * @param gameId gameId of the game we want to join
     */
    public static void joinGame(String gameId) {
        String _username = self.username.getText();

        if (!_username.isBlank()) {
            // Executing request on separate thread
            new Thread(() -> {
                synchronized (requestLock) {
                    ResponseStatus response = App.connection.connectToGame(gameId, _username, false);

                    // Switching view
                    if (response == ResponseStatus.SUCCESS) {
                        // After successful game (re)joining, we store our current game session information
                        // for possible future game reconnections
                        ReconnectionHandler rh = new ReconnectionHandler();
                        rh.setParameters(gameId, _username);

                        Platform.runLater(() -> App.setRoot("ingame"));
                    } else {
                        Platform.runLater( () -> self.errorMessage.setText(response.toString()) );
                        System.out.println("COULDN'T JOIN GAME: " + response);
                    }

                }
            }).start();
        }
    }


    /** Method in charge of requesting a list of all available games and displaying them */
    @FXML
    private void listGames() {
        // Executing request on separate thread
        new Thread(() -> {
            synchronized (requestLock) {
                ObservableList<Entry<String, int[]>> observableList = FXCollections.observableArrayList();
                observableList.setAll(App.connection.getAvailableGames().entrySet());
                Platform.runLater(() -> {
                    gameList.setItems(FXCollections.observableArrayList());
                    gameList.setCellFactory(l -> new ListViewCell());
                    gameList.setItems(observableList);
                    gameList.setCellFactory(l -> new ListViewCell());
                });
            }
        }).start();
    }

    /** Method in charge of rejoining lastly disconnected game */
    @FXML
    private void rejoinGame() {
        // Executing request on separate thread
        new Thread(() -> {
            synchronized (requestLock) {
                try {
                    String[] parameters = new ReconnectionHandler().getParameters();
                    System.out.println(parameters[0] + ", " + parameters[1]);
                    ResponseStatus response = App.connection.connectToGame(parameters[0], parameters[1], true);

                    // Switching view
                    if (response == ResponseStatus.SUCCESS) {
                        Platform.runLater( () -> App.setRoot("ingame") );
                    } else {
                        Platform.runLater( () -> errorMessage.setText(response.toString()) );
                        System.out.println("COULDN'T REJOIN GAME: " + response);
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    System.out.println("ERROR: Couldn't rejoin game: " + ex);
                }
            }
        }).start();
    }

    /** Method in charge of creating a new game */
    @FXML
    private void createGame() {
        String _username = self.username.getText();

        if (!_username.isBlank()) {
            new Thread(() -> {
                synchronized (requestLock) {
                    String gameId = Connection.generateGameID();
                    ResponseStatus response = App.connection.createGame(
                            gameId,
                            username.getText(),
                            numOfPlayers.getValue()
                    );

                    // Switching view
                    if (response == ResponseStatus.SUCCESS) {
                        // After successful game (re)joining, we store our current game session information
                        // for possible future game reconnections
                        ReconnectionHandler rh = new ReconnectionHandler();
                        rh.setParameters(gameId, _username);

                        Platform.runLater(() -> App.setRoot("ingame"));
                    } else {
                        Platform.runLater( () -> errorMessage.setText(response.toString()) );
                        System.out.println("COULDN'T CREATE GAME: " + response);
                    }
                }
            }).start();
        }
    }

}
