package it.polimi.ingsw.client.view.gui;


import it.polimi.ingsw.client.App;
import it.polimi.ingsw.common.TileState;
import it.polimi.ingsw.common.TileType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Class in charge of creating a tile grid (either for boards or libraries) of specified size
 *
 * @author Ferrarini Andrea
 */
public class GridManager extends GridPane {

    // Grid dimensions
    private final int columns, rows;


    /**
     * Class constructor
     *
     * @param columns number of columns for the grid
     * @param rows number of rows for the grid
     * @param tileSize each tile is going to be a square. Specify square size here
     */
    public GridManager(int columns, int rows, int tileSize) {
        super();
        this.columns = columns;
        this.rows = rows;

        // Setting up the grid
        this.setMinSize(columns * tileSize, rows * tileSize);
        this.setVgap(tileSize / 10.0);
        this.setHgap(tileSize / 10.0);
        this.setAlignment(Pos.CENTER);

        // Creating the ImageView tile-grid
        for (int column=0; column < columns; column++) {
            for (int row=0; row < rows; row++) {
                // Create a new ImageView for the tile
                ImageView tileHolder = new ImageView();

                // Forcing tile size
                tileHolder.setFitHeight(tileSize);
                tileHolder.setFitWidth(tileSize);

                // Adding ImageView to grid at correct coordinates
                this.add(tileHolder, column, row);
            }
        }
    }

    /**
     * Method in charge of updating the grid's content with new tiles
     *
     * @param content new content to be rendered in the grid. "content" is a matrix of columns * rows
     */
    public void setGridContent(TileType[][] content) {
        // Checking argument is valid
        if (content.length != this.columns || content[0].length != this.rows) {
            throw new IllegalArgumentException(
                    "ERROR: content matrix must match the grid's size." +
                            "Grid: " + columns + " * " + rows + ", " +
                            "content: " + content.length + " * " + content[0].length
            );
        }

        // Iterating over each node of the grid
        int column, row;
        for (Node node : this.getChildren()) {
            // Fetching the node's coordinates
            column = GridPane.getColumnIndex(node);
            row = GridPane.getRowIndex(node);

            // Check if there is content at the current coordinates
            if (content[column][row] != null) {
                try {
                    // Assemble path to the tile's asset and set the asset in the ImageView
                    Path pathToAsset = Paths.get(App.ASSETS_BASE_PATH, "item tiles", content[column][row].getAssetName());
                    ((ImageView) node).setImage(new Image(new FileInputStream(pathToAsset.toString())));
                } catch (IOException ex) {
                    System.out.println("ERROR: IMAGE NOT FOUND");
                }
            }
        }
    }

    /**
     * Method in charge of decorating each item in the grid according to a TileState value
     *
     * @param states TileState of each tile in the grid. "states" is a matrix of columns * rows
     */
    public void setGridItemDecorators(TileState[][] states) {
        // Checking argument is valid
        if (states.length != this.columns || states[0].length != this.rows) {
            throw new IllegalArgumentException(
                    "ERROR: states matrix must match the grid's size." +
                            "Grid: " + columns + " * " + rows + ", " +
                            "states: " + states.length + " * " + states[0].length
            );
        }

        // Iterating over each node of the grid
        int column, row;
        for (Node node : this.getChildren()) {
            // Fetching the node's coordinates
            column = GridPane.getColumnIndex(node);
            row = GridPane.getRowIndex(node);

            // Check if there is content at the current coordinates
            if (states[column][row] == TileState.PICKABLE) {
                // ...
                System.out.println("pick-able at (" + column + ", " + row + ")");
            } else if (states[column][row] == TileState.PICKABLE_NEXT) {
                // ...
                System.out.println("pick-able_next at (" + column + ", " + row + ")");
            } else {
                // Reset border if NOT_PICK-ABLE (must be done!)
                System.out.println("not_pick-able at (" + column + ", " + row + ")");
            }
        }
    }

}
