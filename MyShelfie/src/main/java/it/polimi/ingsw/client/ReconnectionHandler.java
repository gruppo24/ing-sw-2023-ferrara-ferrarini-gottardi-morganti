package it.polimi.ingsw.client;

import java.io.*;

/**
 * This class is in charge of handling all reconnections of the client
 * after disconnections occur. This is done by storing the gameID and
 * username associated to a session temporarily in a file
 *
 * @author Ferrarini Andrea
 */
public class ReconnectionHandler {

    private File file;

    /**
     * Class constructor
     */
    public ReconnectionHandler() {
        // Firstly, we make sure the "tmp" directory has been created / exists
        boolean created = (new File("./tmp")).mkdir();
        if (created) System.out.println("=== tmp directory created for recovery files ===");

        // We create a temp. storage file, or open one that already exist
        this.file = new File("./tmp/recover.mys");
        try {
            this.file.createNewFile();
        } catch (IOException ex) {
            System.out.println("ERROR: couldn't create a recovery file --> reconnection will be impossible");
        }

    }

    /**
     * Method in charge of storing the given game session information to a local storage
     * @param gameID unique game identifier
     * @param username username associated to the player at this client
     */
    public void setParameters(String gameID, String username) {
        // We simply write the pair as a string array to the file
        String[] output = {gameID, username};
        try {
            ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(this.file));
            writer.writeObject(output);
            writer.close();
        } catch (FileNotFoundException ex) {
            System.out.println("ERROR: recovery file deleted or moved... couldn't be found!");
        } catch (IOException ex) {
            System.out.println("ERROR: something went wrong while writing to the recovery file --> reconnection will be impossible");
        }
    }

    /**
     * Method in charge of reading stored game session information for reconnection
     * @return String[] containing gameID and username of last session
     * @throws FileNotFoundException if the file 'recovery.mys' couldn't be found
     * @throws IOException for general exceptions during file reading
     * @throws ClassNotFoundException for exceptions during content decoding
     */
    public String[] getParameters() throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream reader = new ObjectInputStream(new FileInputStream(this.file));
        String[] content = (String[]) reader.readObject();
        reader.close();
        return content;
    }

}
