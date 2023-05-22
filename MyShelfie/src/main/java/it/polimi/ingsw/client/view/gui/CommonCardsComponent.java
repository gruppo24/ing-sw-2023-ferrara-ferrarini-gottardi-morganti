package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.App;
import it.polimi.ingsw.common.messages.responses.SharedGameState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class CommonCardsComponent extends VBox implements SGSConsumer, Initializable{
    @FXML
    ImageView card1;
    @FXML
    ImageView card2;

    @FXML
    ImageView order1;

    @FXML
    ImageView order2;

    public CommonCardsComponent(){
        setSpacing(10);
        FXMLLoader loader = new FXMLLoader(App.class.getResource("CommonCards.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            System.err.println("Couldn't load CommonCards FXML");
            throw new RuntimeException(e);
        }

        IngameController.appendConsumer(this);
        setupLayout();
    }
    @Override
    public void updateSGS(SharedGameState sgs) {
        card1.setImage(loadAsset("common goal cards", sgs.commonsId[0], 130, 80));
        card2.setImage(loadAsset("common goal cards", sgs.commonsId[1], 130,80));
        for(int i=0; i< sgs.commonsId.length; i++){
            for(int j=0; j<sgs.players.length;j++){
                if(sgs.commonsAchievers[i][j] == null){
                    if(sgs.players.length == 2){
                        switch (j+1) {
                            case 1 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_8.jpg",40,40));
                            case 2 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_4.jpg",40,40));
                        }
                        break;
                    }else if(sgs.players.length == 3){
                        switch (j+1) {
                            case 1 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_8.jpg",40,40));
                            case 2 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_6.jpg",40,40));
                            case 3 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_4.jpg",40,40));

                        }
                        break;
                    }else if(sgs.players.length == 4){
                        switch (j+1) {
                            case 1 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_8.jpg",40,40));
                            case 2 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_6.jpg",40,40));
                            case 3 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_4.jpg",40,40));
                            case 4 -> chooseCommon(i).setImage(loadAsset("scoring tokens", "scoring_2.jpg",40,40));
                        }
                        break;
                    }
                }
            }
        }

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        card1.setImage(loadAsset("common goal cards", "back.jpg",130,80));
        card2.setImage(loadAsset("common goal cards", "back.jpg",130,80));
        order1.setImage(loadAsset("scoring tokens", "scoring_back_EMPTY.jpg",40,40));
        order2.setImage(loadAsset("scoring tokens", "scoring_back_EMPTY.jpg",40,40));
    }
    private static Image loadAsset(String assetDirectory, String assetName, int width, int height) {
        Path pathToAsset = Paths.get(App.ASSETS_BASE_PATH, assetDirectory, assetName);
        try {
            return new Image(new FileInputStream(pathToAsset.toString()), width, height, false, false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    private ImageView chooseCommon(int i){
        if(i == 0)
            return order1;
        else
            return order2;
    }
    private void setupLayout() {
        HBox hbox1 = new HBox();
        hbox1.getChildren().addAll(card1, order1);
        hbox1.setAlignment(Pos.CENTER);
        hbox1.setSpacing(10);

        HBox hbox2 = new HBox();
        hbox2.getChildren().addAll(card2, order2);
        hbox2.setAlignment(Pos.CENTER);
        hbox2.setSpacing(10);

        getChildren().addAll(hbox1, hbox2);
        setSpacing(10);
        setAlignment(Pos.CENTER);
    }

}
