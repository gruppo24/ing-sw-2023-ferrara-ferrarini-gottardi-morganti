package it.polimi.ingsw.server.controller.socket;

import it.polimi.ingsw.server.model.GameState;

public class ReconnectionTimer implements Runnable {

    private final GameState game;
    public final int waitTime;

    /**
     * Class constructor
     *
     * @param game reference to the game which has to be ended if reconnection time elapses
     * @param waitTime time to wait before terminating the provided game
     */
    public ReconnectionTimer(GameState game, int waitTime) {
        this.game = game;
        this.waitTime = waitTime;
    }

    /**
     * Class constructor
     *
     * @param game reference to the game which has to be ended if reconnection time elapses
     */
    public ReconnectionTimer(GameState game) {
        // We call the full constructor with a default waitTime value
        this(game, 60_000);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(this.waitTime);
            this.game.terminate();
        } catch (InterruptedException e) {
            // Do nothing...
            System.out.println("Player rejoined... stopping timer");
        }
    }

}
