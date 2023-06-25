package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.exceptions.AlreadyUsedIndex;
import it.polimi.ingsw.server.exceptions.EmptySelectionBuffer;
import it.polimi.ingsw.server.exceptions.InvalidReorderingIndices;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

/**
 * Middleware class for model updating
 *
 * @author Ferrara Silvia
 */
public class Middleware {

    /**
     * Method in charge of actually performing column selection operations on the model
     *
     * @param game game whose model we want to access
     * @param player player who has requested a column select
     * @param column column selected by the player
     */
    public static void doSelectColumn(GameState game, Player player, int column){
        try {
            player.selectColumn(column);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("---> USER " + player.nickname + " selected an invalid column!");
        }
        System.out.println("=== COLUMN CHOSEN ===");
        synchronized (game.gameLock){
            game.gameLock.notifyAll();
        }
    }

    /**
     * Method in charge of actually performing tile pick operations on the model
     *
     * @param game game whose model we want to access
     * @param player player who has requested a tile pick
     * @param x x coordinate of the picked tile
     * @param y y coordinate of the picked tile
     */
    public static void doPickTile(GameState game, Player player, int x, int y){
        // NOTICE: we subtract 1 from player.getSelectionBufferSize() because we are about to
        // push a new tile in the buffer
        TileType pickedTile = game.getBoard().pick(x, y, player.getSelectionBufferSize()-1);
        if (pickedTile != null) player.pushTileToSelectionBuffer(pickedTile);
        synchronized (game.gameLock) { game.gameLock.notifyAll(); }
    }

    /**
     * Method in charge of actually performing selection buffer reordering operations on the model
     *
     * @param game game whose model we want to access
     * @param player player who has requested a selection buffer reordering
     * @param first index of the first tile to insert in the library
     * @param second index of the second tile to insert in the library
     * @param third index of the third tile to insert in the library
     */
    public static void doReorder(GameState game, Player player, int first, int second, int third){
        try {
            player.reorderSelectionBuffer(first, second, third);
            player.flushBufferIntoLibrary();
            game.turnIsOver();
            // No need to notify the gameLock: GameState::turnIsOver will already do o for us
        } catch (AlreadyUsedIndex | InvalidReorderingIndices | IndexOutOfBoundsException | EmptySelectionBuffer ex) {
            System.out.println("---> Error during reordering for " + player.nickname + ": " + ex);
            // We wake up the threads here, since turnIsOver hasn't done so for us in case of an exception
            synchronized (game.gameLock) { game.gameLock.notifyAll(); }
        }
    }
}
