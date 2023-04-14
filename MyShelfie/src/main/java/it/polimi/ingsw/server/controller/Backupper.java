package it.polimi.ingsw.server.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import it.polimi.ingsw.server.model.GameState;

public class Backupper implements Runnable {
    private final GameState gameState;

    public Backupper(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void run() {
        // check if the backup folder exists first, otherwise create it
        File backupFolder = new File("backups");
        if (!backupFolder.exists()) {
            backupFolder.mkdir();
        }

        // create the backup file
        File backupFile = new File("backups/" + this.gameState.getGameID() + ".back");
        boolean success = true;
        if (!backupFile.exists()) {
            try {
                success = backupFile.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (success) {
            try {
                // Create the output stream and write the game state to disk
                FileOutputStream backupFileStream = new FileOutputStream(backupFile);
                ObjectOutputStream gameStateStream = new ObjectOutputStream(backupFileStream);
                gameStateStream.writeObject(this.gameState);
                gameStateStream.close();
                System.out.println("[Backupper] Game '" + this.gameState.getGameID() + "' saved to disk!");
            } catch (IOException e) {
                System.err.println("[Backupper] Error while writing game state to disk!");
                e.printStackTrace();
            }
        } else {
            System.out.println("[Backupper] Backup file could not be created  for game: " + this.gameState.getGameID());
        }
    }
}
