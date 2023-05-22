package it.polimi.ingsw.client.view.gui;


import it.polimi.ingsw.client.App;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Class in charge of constructing and rendering a bottom library object with its icons
 *
 * @author Ferrarini Andrea
 */
public class LibraryThumbnails extends VBox implements SGSConsumer{

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
        FXMLLoader loader = new FXMLLoader(App.class.getResource("libraryThumbnails.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        // Rendering bottom library element
        bottomLibrary.setGridContent(sgs.libraries[index]);

        // Rendering icons
        if (sgs.armchairIndex == index) {
            hasArmchair.setImage(loadAsset("misc", "firstplayertoken.png"));
        }

        // Rendering player's username
        username.setText(sgs.players[index]);
    }
    @Override
    public void updateSGS(SharedGameState sgs) {
        if(sgs.firstFilled) {
            firstFinished.setImage(loadAsset("scoring tokens", "end game.png"));
        }
        for(int i=0; i< sgs.commonsId.length; i++){
            for(int j=0; j<sgs.players.length;j++){
                if(sgs.players[sgs.currPlayerIndex].equals(sgs.commonsAchievers[i][j])){
                    if(sgs.players.length == 2){
                        switch (j+1) {
                            case 1 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_8.jpg"));
                            case 2 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_4.jpg"));
                        }
                    }else if(sgs.players.length == 3){
                        switch (j+1) {
                            case 1 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_8.jpg"));
                            case 2 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_6.jpg"));
                            case 3 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_4.jpg"));
                        }

                    }else if(sgs.players.length == 4){
                        switch (j+1) {
                            case 1 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_8.jpg"));
                            case 2 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_6.jpg"));
                            case 3 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_4.jpg"));
                            case 4 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_2.jpg"));
                        }
                    }
                }
            }
        }
    }

    /**
     * Helper method in charge of opening a FileInputStream for asset loading
     *
     * @param assetDirectory asset directory under ASSET_BASE_PATH
     * @param assetName asset file name under ASSET_BASE_PATH/assetDirectory
     * @return new Image instance with opened asset
     */
    private static Image loadAsset(String assetDirectory, String assetName) {
        Path pathToAsset = Paths.get(App.ASSETS_BASE_PATH, assetDirectory, assetName);
        try {
            return new Image(new FileInputStream(pathToAsset.toString()), 20, 20, false, false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    private ImageView chooseCommon(int i){
        if(i == 0)
            return common1Obtained;
        else
            return  common2Obtained;
    }


}
