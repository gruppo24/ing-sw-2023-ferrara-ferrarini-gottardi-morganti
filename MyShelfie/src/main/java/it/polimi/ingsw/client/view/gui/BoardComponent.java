package it.polimi.ingsw.client.view.gui;


import it.polimi.ingsw.client.App;
import it.polimi.ingsw.common.messages.responses.SharedGameState;


/**
 * Board component class, renders the game board in the GUI
 */
public class BoardComponent extends GridManager implements SGSConsumer {


    /**
     * Class constructor
     */
    public BoardComponent() {
        super(9, 9, 40);
        this.getStyleClass().add("board");
        IngameController.appendConsumer(this);

        // Attaching server request
        this.setActionHandler((x, y) -> {
            SharedGameState gameState = IngameController.getLastState();
            System.out.println("Picked tile " + x + ", " + y);
            if (gameState.selectionBuffer != null &&
                    gameState.selectionBuffer[gameState.selectionBuffer.length-1] == null) {
                System.out.println("SENDING PICK REQUEST");
                IngameController.setGameState(App.connection.pickTile(x, y));
                IngameController.setGameState(App.connection.reorder(0, 1, 2));
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
    
}
