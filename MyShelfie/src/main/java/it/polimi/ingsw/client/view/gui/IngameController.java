package it.polimi.ingsw.client.view.gui;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * InGame controller class
 */
public class IngameController implements Initializable {

    // Interface nodes
    @FXML Label example;


    /** @see Initializable#initialize(URL, ResourceBundle)  */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        example.setText("Example working!");
    }

}
