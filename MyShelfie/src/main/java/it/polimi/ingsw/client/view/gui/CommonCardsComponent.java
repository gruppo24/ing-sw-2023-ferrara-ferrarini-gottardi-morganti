package it.polimi.ingsw.client.view.gui;


import it.polimi.ingsw.client.App;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;


/**
 * Class in charge of constructing and rendering a common card and its points icon
 */
public class CommonCardsComponent extends HBox implements SGSConsumer {

    @FXML
    ImageView card;

    @FXML
    ImageView order;

    @FXML
    Label description;

    // Common card number
    private final int index;


    /**
     * Class constructor
     *
     * @param index index of the common card
     */
    public CommonCardsComponent(@NamedArg("index") int index) {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("CommonCards.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            System.err.println("Couldn't load CommonCards FXML");
            throw new RuntimeException(e);
        }

        this.index = index;
        IngameController.appendConsumer(this);
    }

    /** @see SGSConsumer#updateSGS(SharedGameState) */
    @Override
    public void updateSGS(SharedGameState sgs) {
        // Loading static images
        if (sgs.gameOngoing) {
            card.setImage(GUIUtils.loadAsset("common goal cards", sgs.commonsId[index], 130, 80));

            int numOfPlayers = sgs.players.length;
            int nextOrder = -1;
            for (int i = 0; i < sgs.commonsAchievers[index].length; i++)
                if (sgs.commonsAchievers[index][i] == null && nextOrder == -1)
                    nextOrder = i;

            if (nextOrder + 1 <= numOfPlayers && nextOrder != -1) {
                String imgName = "scoring_" + GUIUtils.mapCommonPoints(numOfPlayers, nextOrder+1) + ".jpg";
                order.setImage(GUIUtils.loadAsset("scoring tokens", imgName, 40, 40));
            } else {
                order.setImage(null);
            }

            // Setting common objective description
            description.setText(sgs.commonsDesc[index]);

        } else {
            // If game hasn't started, we load all cards and icons on their backs
            card.setImage(GUIUtils.loadAsset("common goal cards", "back.jpg", 130, 80));
            order.setImage(GUIUtils.loadAsset("scoring tokens", "scoring_back_EMPTY.jpg", 40, 40));
        }
    }

}
