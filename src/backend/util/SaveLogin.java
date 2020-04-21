package backend.util;

import java.io.*;

/**
 * IO Class for saving login details locally
 */
public class SaveLogin {
    //writes a new directory to the the same area as the jar file
    private File saveFile = new File(DirectoryMaker.folderMaker("login") + "login.txt");
    private FileWriter fw = new FileWriter(saveFile, true);
    private BufferedWriter bw = new BufferedWriter(fw);


    /**
     * Default constructor
     *
     * @throws IOException
     */
    public SaveLogin() throws IOException {
    }

    /**
     * Get user details from the save file
     *
     * @return string array with user details
     * @throws IOException
     */
    public String[] getUser() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(saveFile));
        String line = "";
        line = br.readLine();
        String[] user = line.split(",");
        br.close();
        return user;
    }

    /**
     * Saves a (new) user to the local txt file
     *
     * @param username the user's username
     * @param password the user's password
     * @throws IOException
     */
    public void saveUser(String username, String password) throws IOException {
        if (isRemembered()) {
            deleteSaveData();
        }
        bw.write(username + "," + password);
    }

    /**
     * Deletes data in save file
     *
     * @throws IOException
     */
    public void deleteSaveData() throws IOException {
        BufferedWriter fileClearer = new BufferedWriter(new FileWriter(saveFile));
        fileClearer.write("");
        fileClearer.close();
    }

    /**
     * Checks if there already exist a user in the save file
     *
     * @return true if a user exists, false if not
     * @throws IOException
     */
    public boolean isRemembered() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(saveFile));
        if (br.readLine() != null) {
            br.close();
            return true;
        } else {
            br.close();
            return false;
        }
    }

    /**
     * Close writing streams
     *
     * @throws IOException
     */
    public void close() throws IOException {
        bw.close();
        fw.close();
    }


}
