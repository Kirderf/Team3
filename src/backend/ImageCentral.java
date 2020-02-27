package backend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

public class ImageCentral {

 /* vet ikke hva fredrik ville gjøre her
    public ArrayList<File> getAllImages(String path) throws IOException {
        ArrayList<File> all = new ArrayList<File>();
        if (getFiles(new File(path), all)) {
            System.out.println(all);
            return all;
        }
        return null;

    }*/

    /**
     *
     * @param pathToFile the link to a specific image
     * @return true if the import is successfull, false otherwise, even if no pictures
     * @throws IOException if
     */
    private boolean getFileFromFolder(File pathToFile) throws IOException{
        //if(pathToFile.)
        String extension = pathToFile.getPath().substring(pathToFile.getPath().lastIndexOf("."));
        return false;
    }
    private String getExtension(File path){
        path.getName();


        return "";
    }

    /*
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
    }*/
}

