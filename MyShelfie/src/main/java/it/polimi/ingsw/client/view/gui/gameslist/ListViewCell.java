package it.polimi.ingsw.client.view.gui.gameslist;


import javafx.scene.control.ListCell;
import java.util.Map.Entry;


/**
 * Class representing a ListView item
 */
public class ListViewCell extends ListCell<Entry<String, int[]>> {

    @Override
    public void updateItem(Entry<String, int[]> _game, boolean empty)
    {
        super.updateItem(_game, empty);

        // Setting item content
        if(_game != null) {
            Game game = new Game(_game);
            game.setInfo();
            setGraphic(game.getContent());
        }
    }

}
