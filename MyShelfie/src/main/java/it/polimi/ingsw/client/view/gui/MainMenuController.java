package it.polimi.ingsw.client.view.gui;


import it.polimi.ingsw.client.App;
import it.polimi.ingsw.client.controller.JRMIConnection;
import it.polimi.ingsw.client.controller.SocketConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.util.ResourceBundle;

import static it.polimi.ingsw.client.Client.*;


/**
 * Main menu controller class
 */
public class MainMenuController implements Initializable {


    @FXML
    TextField address;


    /** Method in charge of starting a new pregame session with a socket connection */
    @FXML
    private void enterPregameSocket() {
        // We try to establish a socket connection with the server
        new Thread(() -> {
            try {
                App.connection = new SocketConnection(address.getText(), SOCKET_PORT);
                App.connection.establishConnection();
                Platform.runLater(() -> App.setRoot("pregame"));
            } catch (IOException | NotBoundException ex) {
                System.out.println("ERROR: failed to establish connection with socket server: " + ex);
                App.connection = null;
            }
        }).start();
    }

    /** Method in charge of starting a new pregame session with a jRMI connection */
    @FXML
    private void enterPregameJRMI() {
        // We try to establish a jRMI connection with the server
        new Thread(() -> {
            try {
                App.connection = new JRMIConnection(address.getText(), JRMI_PORT);
                App.connection.establishConnection();
                Platform.runLater(() -> App.setRoot("pregame"));
            } catch (IOException | NotBoundException ex) {
                System.out.println("ERROR: failed to connect to jRMI server: " + ex);
                App.connection = null;
            }
        }).start();
    }

    /** Method in charge of safely killing the application */
    @FXML
    private void exit() {
        System.exit(0);
    }

    /** @see Initializable#initialize(URL, ResourceBundle)  */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Only purpose of this method is to set the default server IP address
        // in the address TextField
        address.setText(SERVER_ADDR);
    }
}
