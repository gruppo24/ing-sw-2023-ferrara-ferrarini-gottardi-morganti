package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.common.messages.responses.SharedGameState;

public class BoardComponent extends GridManager implements SGSConsumer {

    public BoardComponent() {
        super(9, 9, 40);
        this.getStyleClass().add("board");
        IngameController.appendConsumer(this);
    }

    @Override
    public void update(SharedGameState sgs) {
        this.setGridContent(sgs.boardContent);
        this.setGridItemDecorators(sgs.boardState);
    }
}
