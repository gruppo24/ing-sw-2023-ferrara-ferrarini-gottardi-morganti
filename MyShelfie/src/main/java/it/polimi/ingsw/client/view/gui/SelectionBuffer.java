package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.App;
import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

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

                System.out.print("Current buffer state: ");
                for (int index : this.reorderedBuffer)
                    System.out.print(index + ", ");
                System.out.println();

                // Checking if the buffer has been completely reordered
                if (this.reorderedBuffer.size() == gameState.selectionBuffer.length) {
                    // In that case, we send reorder request to the server. To do this,
                    // we prefetch the indices and then call the function. Otherwise
                    // we can't clear the list properly...

                    int first = this.reorderedBuffer.get(0);
                    int second = this.reorderedBuffer.size() > 1 ? this.reorderedBuffer.get(1) : 1;
                    int third = this.reorderedBuffer.size() > 2 ? this.reorderedBuffer.get(2) : 2;

                    this.reorderedBuffer = new LinkedList<>();
                    IngameController.setGameState(App.connection.reorder(first, second, third));

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
    }

}
