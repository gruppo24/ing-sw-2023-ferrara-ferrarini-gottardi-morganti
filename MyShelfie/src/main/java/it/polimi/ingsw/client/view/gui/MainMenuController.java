package it.polimi.ingsw.client.view.gui;

import java.io.IOException;

import javafx.fxml.FXML;

public class MainMenuController {

    @FXML
    private void buttonPress() throws IOException {
        System.out.println("Pressed!");
    }

    @FXML
    private void exit() {
        System.exit(0);
    }
}
