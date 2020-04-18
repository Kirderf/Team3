package backend.util;

import java.io.*;

public class SaveLogin {
    File saveFile = new File(System.getProperty("user.dir") + File.separator + "resources" + File.separator + "loginDetails.txt");
    FileWriter fw = new FileWriter(saveFile, true);
    BufferedWriter bw = new BufferedWriter(fw);



    public SaveLogin() throws IOException {
    }

    public String[] getUser() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(saveFile));
        String line = "";
        line = br.readLine();
        String[] user = line.split(",");
        br.close();
        return user;
    }

    public void saveUser(String username, String password) throws IOException {
        if (isRemembered()) {
            deleteSaveData();
        }
        bw.write(username + "," + password);
    }

    public void deleteSaveData() throws IOException {
        FileWriter fileClearer = new FileWriter(saveFile);
        fileClearer.write("");
        fileClearer.close();
    }

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

    public void close() throws IOException {
        bw.close();
        fw.close();
    }


}
