package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.App;
import it.polimi.ingsw.common.TileState;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 * Board component class, renders the game board in the GUI
 */
public class BoardComponent extends GridManager implements SGSConsumer {
    /**
     * Class constructor
     */
    public BoardComponent() {
        super(9, 9, 50);
        this.getStyleClass().add("board");
        IngameController.appendConsumer(this);

        // Attaching server request
        this.setActionHandler((x, y) -> {
            SharedGameState gameState = IngameController.getLastState();
            System.out.println("Picked tile " + x + ", " + y);
            if (gameState.gameOngoing &&
                    gameState.currPlayerIndex == gameState.selfPlayerIndex &&
                    gameState.selectionBuffer != null &&
                    gameState.selectionBuffer[gameState.selectionBuffer.length - 1] == null) {
                System.out.println("SENDING PICK REQUEST");
                IngameController.setGameState(App.connection.pickTile(x, y));
            }
        });
    }

    /** @see SGSConsumer#updateSGS(SharedGameState) */
    @Override
    public void updateSGS(SharedGameState sgs) {
        System.out.println("Updating board");

        // set the correct tiles and tile decorators
        this.setGridContent(sgs.boardContent);
        this.setGridItemDecorators(sgs.boardState);
    }

    /**
     * Method in charge of decorating each item in the grid according to a TileState
     * value
     *
     * @param states TileState of each tile in the grid. "states" is a matrix of
     *               columns * rows
     */
    public void setGridItemDecorators(TileState[][] states) {
        // Checking argument is valid
        if (states.length != this.columns || states[0].length != this.rows) {
            throw new IllegalArgumentException(
                    "ERROR: states matrix must match the grid's size." +
                            "Grid: " + columns + " * " + rows + ", " +
                            "states: " + states.length + " * " + states[0].length);
        }

        // Iterating over each node of the grid
        int column, row;
        for (Node node : this.getChildren()) {
            // clear previous borders
            node.getStyleClass().clear();

            // Fetching the node's coordinates
            column = GridPane.getColumnIndex(node);
            row = states[column].length - GridPane.getRowIndex(node) - 1;

            // Check if there is content at the current coordinates
            if (states[column][row] == TileState.PICKABLE) {
                node.getStyleClass().add("board-tile-pickable");
            } else if (states[column][row] == TileState.PICKABLE_NEXT) {
                node.getStyleClass().add("board-tile-pickable-next");
            } else {
                node.getStyleClass().add("board-tile-not-pickable");
            }
        }
    }
}
