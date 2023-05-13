package it.polimi.ingsw.client.view.gui;


import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.model.Board;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * InGame controller class
 */
public class IngameController implements Initializable {

    @FXML HBox upperContainer;
    @FXML HBox bottomContainer;


    /** @see Initializable#initialize(URL, ResourceBundle)  */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*
         * ================================= *
         * ===== EXAMPLE GRID CREATION ===== *
         * ================================= *
         */
        Board b = new Board();
        b.refillBoard(2);
        b.definePickable();

        // Creating new grid
        TileType[][] content = b.getBoardContent();
        GridManager board = new GridManager(content.length, content[0].length, 40 /* Just a value... */);

        // Populating grid
        board.setGridContent(b.getBoardContent());
        board.setGridItemDecorators(b.getBoardState());

        // Being a board, we manually add the appropriate class to it so it gets styled correctly
        board.getStyleClass().add("board");

        AnchorPane.setBottomAnchor(board, 0.0);
        upperContainer.getChildren().add(board);
    }

}
