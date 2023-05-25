package it.polimi.ingsw.client.view.gui;


import it.polimi.ingsw.client.App;
import it.polimi.ingsw.client.controller.JRMIConnection;
import it.polimi.ingsw.client.controller.SocketConnection;
import javafx.fxml.FXML;

import java.io.IOException;
import java.rmi.NotBoundException;

import static it.polimi.ingsw.client.Client.*;


/**
 * Main menu controller class
 */
public class MainMenuController {

    /** Method in charge of starting a new pregame session with a socket connection */
    @FXML
    private void enterPregameSocket() {
        // We try to establish a socket connection with the server
        try {
            App.connection = new SocketConnection(SERVER_ADDR, SOCKET_PORT);
            App.connection.establishConnection();
            App.setRoot("pregame");
        } catch (IOException | NotBoundException ex) {
            System.out.println("ERROR: failed to establish connection with socket server: " + ex);
            App.connection = null;
        }
    }

    /** Method in charge of starting a new pregame session with a jRMI connection */
    @FXML
    private void enterPregameJRMI() {
        // We try to establish a jRMI connection with the server
        try {
            App.connection = new JRMIConnection(SERVER_ADDR, JRMI_PORT);
            App.connection.establishConnection();
            App.setRoot("pregame");
        } catch (IOException | NotBoundException ex) {
            System.out.println("ERROR: failed to connect to jRMI server: " + ex);
            App.connection = null;
        }
    }

    /** Method in charge of safely killing the application */
    @FXML
    private void exit() {
        System.exit(0);
    }

}
