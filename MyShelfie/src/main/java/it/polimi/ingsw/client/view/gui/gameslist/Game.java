package it.polimi.ingsw.client.view.gui.gameslist;


import it.polimi.ingsw.client.App;
import it.polimi.ingsw.client.view.gui.PregameController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Map.Entry;


/**
 * Class in charge of creating
 */
public class Game {

    // Interface nodes
    @FXML private BorderPane content;
    @FXML private Label gameId;
    @FXML private Label playerStatus;

    // Interface nodes' content attributes
    private final String _gameId;
    private final int _currentPlayers;
    private final int _maximumPlayers;


    /**
     * Class constructor
     *
     * @param _game interface content data
     */
    public Game(Entry<String, int[]> _game) {
        this._gameId = _game.getKey();
        this._currentPlayers = _game.getValue()[0];
        this._maximumPlayers = _game.getValue()[1];

        // Loading content FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("GameListItem.fxml"));
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Method in charge of populating interface with provided content */
    public void setInfo() {
        this.gameId.setText(this._gameId);
        this.playerStatus.setText("(" + this._currentPlayers + "/" + this._maximumPlayers + ")");

        // Attaching click action to main BorderPane container
        this.content.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                PregameController.joinGame(this._gameId);
            }
        });
    }

    /**
     * Method in charge of returning the created container to caller so that
     * it can be rendered elsewhere
     *
     * @return BorderPane holding the provided content
     */
    public BorderPane getContent() {
        return this.content;
    }

}
