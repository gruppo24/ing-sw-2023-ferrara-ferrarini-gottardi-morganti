package it.polimi.ingsw.client;


import java.io.IOException;

import it.polimi.ingsw.client.controller.Connection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * GUI initialization and management class
 */
public class App extends Application {

    // Main scene within the GUI stage
    public static Scene scene;

    // Connection to server instance
    public static Connection connection;

    // Thread lock
    public static final Object requestLock = new Object();


    public static void main(String[] args) throws IOException {
        launch(args);  // We simply launch our GUI...
    }

    /** @see Application#start(Stage)  */
    @Override
    public void start(Stage stage) throws Exception {
        // We create our initial scene and set main_menu.fxml to be our initial scene-graph root
        App.scene = new Scene(loadFXML("main_menu"), 800, 600);
        stage.setScene(App.scene);
        stage.setOnCloseRequest(event -> System.exit(0));  // On window close --> kill all connections / threads
        stage.show();
    }


    /**
     * Method in charge of switching scene-graph root (= changing view)
     *
     * @param fxml FXML file to switch to (*without extension*)
     */
    public static void setRoot(String fxml) {
        App.scene.setRoot(loadFXML(fxml));
    }

    /**
     * Method in charge of loading FXML file
     *
     * @param fxml FXML file name (*without extension*) to load
     * @return FXML Parent instance (null if FXML file couldn't be loaded)
     */
    public static Parent loadFXML(String fxml) {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));

        // Trying to load FXML file
        Parent view = null;
        try {
            view = fxmlLoader.load();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return view;
    }

}
