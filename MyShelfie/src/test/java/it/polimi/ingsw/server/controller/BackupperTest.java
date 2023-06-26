package it.polimi.ingsw.server.controller;


import it.polimi.ingsw.client.controller.Connection;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.controller.jrmi.PreGameStubImpl;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.Player;
import org.junit.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class BackupperTest {

    private static Registry registry;

    private GameState gameState;

    @BeforeClass
    public static void setUpClass() {
        // setup server, if not already online
        try {
            // setup PreGameStubImpl
            BackupperTest.registry = LocateRegistry.createRegistry(Server.JRMI_PORT);
            PreGameStubImpl preGame = new PreGameStubImpl(BackupperTest.registry);
            BackupperTest.registry.rebind("remotePreGame", preGame);
        } catch (RemoteException ex) {}
    }


    @Before
    public void setUp() {
        // Generate a new game to store
        this.gameState = new GameState(Connection.generateGameID(), 2);

        // Adding two players
        gameState.addNewPlayerToGame(new Player("testPlayer1"));
        gameState.addNewPlayerToGame(new Player("testPlayer2"));
    }

    @After
    public void tearDown() {
        /* Do nothing */
    }

    @AfterClass
    public static void tearDownClass() {
//        if (BackupperTest.registry != null) {
//            try {
//                BackupperTest.registry.unbind("remotePreGame");
//            } catch (RemoteException | NotBoundException ex) {
//            }
//        }
    }

    @Test
    public void storeGame_noBackupFolder_createsBackupFolder() throws IOException {
        // Deleting backups folder, if it exists
        File backupsFolder = new File("backups");
        if (backupsFolder.exists()) {

            // At first, emptying the backups directory
            File[] listOfFiles = backupsFolder.listFiles();
            if (listOfFiles != null)
                for (File backup : listOfFiles)
                    if (!backup.delete())
                        throw new IOException("Error deleting backup folder!");

            // Then, deleting the folder altogether
            if (!backupsFolder.delete())
                throw new IOException("Error deleting backup folder!");
        }

        // Storing game-state on disk (SYNCHRONOUSLY)
        Backupper backupper = new Backupper(gameState);
        backupper.run();
    }

    @Test
    public void storeAndRestoreGame_storesAndRestoresAGameFromDisk() throws IOException, ClassNotFoundException {
        // Storing game-state on disk (SYNCHRONOUSLY)
        Backupper backupper = new Backupper(gameState);
        backupper.run();

        // Restoring game-state from disk
        File backupFile = new File("backups/" + gameState.getGameID() + ".back");
        FileInputStream fileIn = new FileInputStream(backupFile);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        GameState restoredGame = (GameState) in.readObject();

        // Checking whether the game is the same:
        if (!restoredGame.getGameID().equals(gameState.getGameID()))
            throw new RuntimeException("GAMES ARE NOT THE SAME!");

        // Deleting backup
        if (!backupFile.delete())
            throw new IOException("Error deleting backup file!");
    }

}
