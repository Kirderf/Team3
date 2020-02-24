package sample;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

public class ImageCentral {


    public ArrayList<File> getAllImages(String path) throws IOException {
        ArrayList<File> all = new ArrayList<File>();
        if (getFiles(new File(path), all)) {
            System.out.println(all);
            return all;
        }
        return null;

    }

    //Getting all files recursively
    private boolean getFiles(File file, ArrayList<File> all) throws IOException {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                all.add(child);
                getFiles(child, all);
            }
        }
        return true;
    }
}

