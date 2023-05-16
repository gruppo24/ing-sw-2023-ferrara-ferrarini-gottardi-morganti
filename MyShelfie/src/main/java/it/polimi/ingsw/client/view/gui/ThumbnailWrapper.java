package it.polimi.ingsw.client.view.gui;


import it.polimi.ingsw.common.messages.responses.SharedGameState;
import javafx.scene.layout.HBox;


/**
 * Class representing an FXML ThumbnailWrapper object. This fxml element will
 * hold within itself all player's libraries
 *
 * @author Ferrarini Andrea
 */
public class ThumbnailWrapper extends HBox implements SGSConsumer {


    /**
     * Class constructor
     */
    public ThumbnailWrapper() {
        super();
        // Adding this instance to the consumer's list
        IngameController.appendConsumer(this);
        this.setSpacing(10);
    }


    /** @see SGSConsumer#updateSGS(SharedGameState)  */
    @Override
    public void updateSGS(SharedGameState sgs) {
        if (sgs.gameOngoing) {
            // Clearing all previous bottom libraries
            this.getChildren().removeAll();

            // Re-rendering all bottom libraries
            for (int i = 0; i < sgs.libraries.length; i++) {
                this.getChildren().add(new LibraryThumbnails(sgs, i));
            }
        }
    }

}
