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
    public boolean isImage(File file){
        if(file.exists()){
            if(getExtensionFromFile(file).equals("jpg")||getExtensionFromFile(file).equals("png")){
                return true;
            }
        }
        return false;
    }
    public File getImageFromPath(String path){
        File image = new File(path);
        if(isImage(image)){
            return image;
        }
        return null;
    }
    /**
     * get the path
     * @param file the link to a specific image
     * @return true if the import is successful, false otherwise
     * @throws IOException if
     */
    private String getPathFromFile(File file) throws IOException{
        if(file.exists()){
            //only jpg and png are supported
            if(isImage(file)){
                return file.getPath();
            }
        }
        return null;
    }
    private String getExtensionFromFile(File file){
        return file.getPath().substring(file.getPath().lastIndexOf("."));
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

