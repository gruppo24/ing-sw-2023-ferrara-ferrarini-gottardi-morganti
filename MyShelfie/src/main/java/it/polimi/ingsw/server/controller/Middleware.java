package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.TileType;
import it.polimi.ingsw.server.exceptions.AlreadyUsedIndex;
import it.polimi.ingsw.server.exceptions.InvalidReorderingIndices;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;

/**
 * Middleware class for model updating
 *
 * @author Ferrara Silvia
 */
public class Middleware {
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

    public static void doPickTile(GameState game, Player player, int x, int y){
        // NOTICE: we subtract 1 from player.getSelectionBufferSize() because we are about to
        // push a new tile in the buffer
        TileType pickedTile = game.getBoard().pick(x, y, player.getSelectionBufferSize()-1);
        if (pickedTile != null) player.pushTileToSelectionBuffer(pickedTile);
        synchronized (game.gameLock) { game.gameLock.notifyAll(); }
    }

    public static void doReorder(GameState game, Player player, int first, int second, int third){
        try {
            player.reorderSelectionBuffer(first, second, third);
            player.flushBufferIntoLibrary();
            game.turnIsOver();
            // No need to notify the gameLock: GameState::turnIsOver will already do o for us
        } catch (AlreadyUsedIndex | InvalidReorderingIndices | IndexOutOfBoundsException ex) {
            System.out.println("---> Error during reordering for " + player.nickname + ": " + ex);
            // We wake up the threads here, since turnIsOver hasn't done so for us in case of an exception
            synchronized (game.gameLock) { game.gameLock.notifyAll(); }
        }
    }
}
