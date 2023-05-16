package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.common.messages.responses.SharedGameState;

/**
 * Board component class, renders the game board in the GUI
 */
public class BoardComponent extends GridManager implements SGSConsumer {

    public BoardComponent() {
        super(9, 9, 40);
        this.getStyleClass().add("board");
        IngameController.appendConsumer(this);
    }

    @Override
    public void updateSGS(SharedGameState sgs) {
        System.out.println("Updating board");
        System.out.println(sgs.boardContent);
        System.out.println(sgs.boardContent[4]);
        System.out.println(sgs.boardContent[4][4]);

        // set the correct tiles and tile decorators
        this.setGridContent(sgs.boardContent);
        this.setGridItemDecorators(sgs.boardState);
    }
}
