package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.App;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;


/**
 * SelectionBuffer class to be rendered in the UI
 */
public class SelectionBuffer extends Pane implements SGSConsumer {

    @FXML
    GridManager selectionBuffer;

    // This List will contain the reordered buffer
    LinkedList<Integer> reorderedBuffer = new LinkedList<>();


    /**
     * Class constructor
     */
    public SelectionBuffer() {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("SelectionBuffer.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            System.err.println("Couldn't load SelectionBuffer FXML");
            throw new RuntimeException(e);
        }

        IngameController.appendConsumer(this);

        // Reordering actions on tile click
        selectionBuffer.setActionHandler((x, y) -> {
            SharedGameState gameState = IngameController.getLastState();
            if (gameState.gameOngoing && !gameState.gameSuspended &&
                    gameState.currPlayerIndex == gameState.selfPlayerIndex &&
                    gameState.selectionBuffer != null) {

                // Forcing user chosen order
                if (this.reorderedBuffer.contains(x)) {
                    // MUST use "Integer.valueOf(...)", or else will remove at index!!!
                    this.reorderedBuffer.remove(Integer.valueOf(x));
                } else {
                    this.reorderedBuffer.add((x));
                }

//                // DEBUG ONLY
//                System.out.print("Current buffer state: ");
//                for (int index : this.reorderedBuffer)
//                    System.out.print(index + ", ");
//                System.out.println();

                // Applying highlight to selected tiles in buffer
                int column;
                for (Node node : selectionBuffer.getChildren()) {
                    // Always remove any previous highlighting Removing
                    node.getStyleClass().remove("cell-highlight");

                    // Fetching the node's coordinates, and checking whether cell should be highlighted
                    column = GridPane.getColumnIndex(node);
                    if (this.reorderedBuffer.contains(column))
                        node.getStyleClass().add("cell-highlight");
                }

                // Checking if the buffer has been completely reordered
                if (this.reorderedBuffer.size() == gameState.selectionBuffer.length) {
                    // In that case, we send reorder request to the server. To do this,
                    // we prefetch the indices and then call the function. Otherwise
                    // we can't clear the list properly...

                    int first = this.reorderedBuffer.get(0);
                    int second = this.reorderedBuffer.size() > 1 ? this.reorderedBuffer.get(1) : 1;
                    int third = this.reorderedBuffer.size() > 2 ? this.reorderedBuffer.get(2) : 2;

                    this.reorderedBuffer = new LinkedList<>();
                    try {
                        IngameController.setGameState(App.connection.reorder(first, second, third));
                    } catch (IOException ex) {
                        System.err.println("REQUEST ERROR: " + ex);
                        Platform.runLater( () -> App.setRoot("main_menu") );
                    }

                }
            }
        });
    }

    /** @see SGSConsumer#updateSGS(SharedGameState) */
    @Override
    public void updateSGS(SharedGameState sgs) {
        // Based on the current game state we decide whether to clear
        // the selection-buffer, or populate it with the currently picked
        // tile
        TileType[][] content;
        if (sgs.selectionBuffer != null) {
            Arrays.stream(sgs.selectionBuffer).forEach(System.out::println);
            content = new TileType[3][1];
            for (int tileIndex=0; tileIndex < sgs.selectionBuffer.length; tileIndex++) {
                    content[tileIndex] = new TileType[] {sgs.selectionBuffer[tileIndex]};
            }
        } else {
            content = new TileType[][]{{null}, {null}, {null}};
        }
        this.selectionBuffer.setGridContent(content);

        // By default we also always clear any highlights:
        // 1. either a reordering has been complete ==> highlight can be removed now
        // 2. an invalid reordering occurred ==> reset the reordering
        for (Node node : selectionBuffer.getChildren())
            node.getStyleClass().remove("cell-highlight");
    }

}
