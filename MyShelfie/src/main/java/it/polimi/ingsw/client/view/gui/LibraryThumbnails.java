package it.polimi.ingsw.client.view.gui;


import it.polimi.ingsw.client.App;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;


/**
 * Class in charge of constructing and rendering a bottom library object with its icons
 *
 * @author Ferrarini Andrea
 */
public class LibraryThumbnails extends VBox {

    @FXML
    GridManager bottomLibrary;

    @FXML
    ImageView hasArmchair;

    @FXML
    ImageView common1Obtained;

    @FXML
    ImageView common2Obtained;

    @FXML
    ImageView firstFinished;

    @FXML
    Label username;


    /**
     * Class constructor
     *
     * @param sgs SharedGameState instance to render
     * @param index thumbnail's index
     */
    public LibraryThumbnails(SharedGameState sgs, int index) {
        // Setting up FXML, root and controller
        FXMLLoader loader = new FXMLLoader(App.class.getResource("libraryThumbnails.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        // Rendering player's username
        username.setText(sgs.players[index]);

        // Rendering bottom library element and icons
        bottomLibrary.setGridContent(sgs.libraries[index]);

        // Rendering icons
        if (sgs.armchairIndex == index) {
            hasArmchair.setImage(GUIUtils.loadAsset("misc", "firstplayertoken.png"));
        }

        for (int j=0; j < sgs.commonsAchievers[0].length; j++) {
            if (sgs.players[index].equals(sgs.commonsAchievers[0][j])) {
                String imgName = "scoring_" + GUIUtils.mapCommonPoints(sgs.commonsAchievers[0].length, j) + ".jpg";
                this.common1Obtained.setImage(GUIUtils.loadAsset("scoring tokens", imgName));
            }
        }

        for (int j=0; j < sgs.commonsAchievers[1].length; j++) {
            if (sgs.players[index].equals(sgs.commonsAchievers[1][j])) {
                String imgName = "scoring_" + GUIUtils.mapCommonPoints(sgs.commonsAchievers[0].length, j) + ".jpg";
                this.common2Obtained.setImage(GUIUtils.loadAsset("scoring tokens", imgName));
            }
        }

        if (sgs.players[index].equals(sgs.firstCompleter)) {
            firstFinished.setImage(GUIUtils.loadAsset("scoring tokens", "end game.png"));
        }
    }

}
