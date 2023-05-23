package it.polimi.ingsw.client.view.gui;


import it.polimi.ingsw.client.App;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Class in charge of handling basic GUI operations
 *
 * @author Gottardi Arianna
 * @author Ferrara Silvia
 */
public class GUIUtils {

    /**
     * This function is in charge of computing the amount of points obtained after achieving a common-objective
     * @param numOfPlayers number of players in the current game
     * @param order ordinal number representing when the player has achieved the common objective
     * @return amount of points achieved
     * @throws IllegalArgumentException when numOfPlayers is not between 1 and 4 or when order isn't
     *                                  between 1 and numOfPlayers
     */
    public static int mapCommonPoints(int numOfPlayers, int order) throws IllegalArgumentException {
        // Based on number of players we map the order of common-objective achievement to an amount of points
        if (numOfPlayers == 2) {
            return switch (order) {
                case 1 -> 8;
                case 2 -> 4;
                default -> throw new IllegalArgumentException(
                        "ERROR: common points for numOfPlayers == 2 requires " +
                                "order to be between 1 and 2 inclusive."
                );
            };
        } else if (numOfPlayers == 3) {
            return switch (order) {
                case 1 -> 8;
                case 2 -> 6;
                case 3 -> 4;
                default -> throw new IllegalArgumentException(
                        "ERROR: common points for numOfPlayers == 3 requires " +
                                "order to be between 1 and 3 inclusive."
                );
            };
        } else if (numOfPlayers == 4) {
            return switch (order) {
                case 1 -> 8;
                case 2 -> 6;
                case 3 -> 4;
                case 4 -> 2;
                default -> throw new IllegalArgumentException(
                        "ERROR: common points for numOfPlayers == 4 requires " +
                                "order to be between 1 and 4 inclusive."
                );
            };
        }

        // Fallthrough exception... numOfPlayers is invalid
        throw new IllegalArgumentException(
                "ERROR: numOfPlayers has to be between 1 and 4 inclusive."
        );
    }

    /**
     * Helper method in charge of opening a FileInputStream for asset loading
     *
     * @param assetDirectory asset directory under ASSET_BASE_PATH
     * @param assetName asset file name under ASSET_BASE_PATH/assetDirectory
     * @param width width to load image with
     * @param height height to load image with
     * @return new Image instance with opened asset
     */
    public static Image loadAsset(String assetDirectory, String assetName, int width, int height) {
        Path pathToAsset = Paths.get(App.ASSETS_BASE_PATH, assetDirectory, assetName);
        try {
            return new Image(new FileInputStream(pathToAsset.toString()), width, height, false, false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Helper method in charge of opening a FileInputStream for asset loading
     *
     * @param assetDirectory asset directory under ASSET_BASE_PATH
     * @param assetName asset file name under ASSET_BASE_PATH/assetDirectory
     * @return new Image instance with opened asset
     */
    public static Image loadAsset(String assetDirectory, String assetName) {
        return loadAsset(assetDirectory, assetName, 20, 20);
    }

}
