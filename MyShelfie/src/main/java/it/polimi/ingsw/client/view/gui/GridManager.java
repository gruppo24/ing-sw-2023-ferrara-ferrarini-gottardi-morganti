package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.App;
import it.polimi.ingsw.common.TileType;
import javafx.beans.NamedArg;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;

/**
 * Class in charge of creating a tile grid (either for boards or libraries) of
 * specified size
 *
 * @author Ferrarini Andrea
 */
public class GridManager extends GridPane {

    // Grid dimensions
    public final int columns, rows;

    /**
     * Class constructor
     *
     * @param columns  number of columns for the grid
     * @param rows     number of rows for the grid
     * @param tileSize each tile is going to be a square. Specify square size
     *                 here
     */
    public GridManager(
            @NamedArg("columns") int columns,
            @NamedArg("rows") int rows,
            @NamedArg("tileSize") double tileSize) {
        this(columns, rows, tileSize, 4, 0);
    }

    /**
     * Class constructor
     *
     * @param columns     number of columns for the grid
     * @param rows        number of rows for the grid
     * @param tileSize    each tile is going to be a square. Specify square size
     *                    here
     * @param borderSize  Dimension separating each tile
     * @param paddingSize Dimension to be added to both vertical and horizontal size
     */
    public GridManager(
            @NamedArg("columns") int columns,
            @NamedArg("rows") int rows,
            @NamedArg("tileSize") double tileSize,
            @NamedArg("borderSize") double borderSize,
            @NamedArg("paddingSize") double paddingSize) {
        this(columns, rows, tileSize, borderSize, borderSize, paddingSize, paddingSize);
    }

    /**
     * Class constructor
     *
     * @param columns      number of columns for the grid
     * @param rows         number of rows for the grid
     * @param tileSize     each tile is going to be a square. Specify square size
     *                     here
     * @param borderSizeH  Horizontal dimension separating each tile
     * @param borderSizeV  Vertical dimension separating each tile
     * @param paddingSizeH Dimension to be added to the horizontal size
     * @param paddingSizeV Dimension to be added to the vertical size
     */
    public GridManager(
            @NamedArg("columns") int columns,
            @NamedArg("rows") int rows,
            @NamedArg("tileSize") double tileSize,
            @NamedArg("borderSizeH") double borderSizeH,
            @NamedArg("borderSizeV") double borderSizeV,
            @NamedArg("paddingSizeH") double paddingSizeH,
            @NamedArg("paddingSizeV") double paddingSizeV) {
        super();
        if (borderSizeH < 4 || borderSizeV < 4)
            throw new IllegalArgumentException("GridManager borderSize cannot be less than 4px");
        this.columns = columns;
        this.rows = rows;

        // Setting up the grid
        this.setMinSize(
                columns * (tileSize + borderSizeH) + paddingSizeH,
                rows * (tileSize + borderSizeV) + paddingSizeV);
        this.setHgap(borderSizeH - 4);
        this.setVgap(borderSizeV - 4);
        this.setAlignment(Pos.CENTER);

        // Creating the ImageView tile-grid
        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                Pane container = new Pane();
                container.setMinWidth(tileSize + 4);
                container.setMinHeight(tileSize + 4);
                // container.setStyle("-fx-background-color: #ffa");

                // Create a new ImageView for the tile
                ImageView tileHolder = new ImageView();
                tileHolder.translateXProperty().set(2);
                tileHolder.translateYProperty().set(2);

                // Forcing tile size
                tileHolder.minWidth(tileSize);
                tileHolder.minHeight(tileSize);
                tileHolder.setFitWidth(tileSize);
                tileHolder.setFitHeight(tileSize);

                // Adding ImageView to grid at correct coordinates
                container.getChildren().add(tileHolder);
                this.add(container, column, row);
            }
        }
    }

    /**
     * Method in charge of updating the grid's content with new tiles
     *
     * @param content new content to be rendered in the grid. "content" is a matrix
     *                of columns * rows
     */
    public void setGridContent(TileType[][] content) {
        // Checking argument is valid
        if (content.length != this.columns || content[0].length != this.rows) {
            throw new IllegalArgumentException(
                    "ERROR: content matrix must match the grid's size." +
                            "Grid: " + columns + " * " + rows + ", " +
                            "content: " + content.length + " * " + content[0].length);
        }

        // Iterating over each node of the grid
        int column, row;
        for (Node _node : this.getChildren()) {
            Node node = ((Pane) _node).getChildren().get(0);

            // Fetching the node's coordinates
            column = GridPane.getColumnIndex(_node);
            row = content[column].length - GridPane.getRowIndex(_node) - 1;

            // Check if there is content at the current coordinates
            if (content[column][row] != null) {
                try {
                    // Assemble path to the tile's asset and set the asset in the ImageView
                    Path pathToAsset = Paths.get(App.ASSETS_BASE_PATH, "item tiles",
                            content[column][row].getAssetName());
                    ((ImageView) node).setImage(new Image(new FileInputStream(pathToAsset.toString())));
                } catch (IOException ex) {
                    System.out.println("ERROR: IMAGE NOT FOUND");
                }
            } else {
                // Resetting ImageView in case no tile is present
                ((ImageView) node).setImage(null);
            }
        }
    }

    /**
     * Method in charge of attaching an event handler to tile click in grid
     *
     * @param callback callback method, takes tile coordinates as argument
     */
    public void setActionHandler(BiConsumer<Integer, Integer> callback) {
        // Iterating over each node of the grid
        for (Node node : this.getChildren()) {

            // Fetching the node's coordinates
            final int column = GridPane.getColumnIndex(node);
            final int row = rows - GridPane.getRowIndex(node) - 1;

            // Attaching event to ImageView
            node.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
                new Thread(() -> callback.accept(column, row)).start();
            });
        }
    }

}
