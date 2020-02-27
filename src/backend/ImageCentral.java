package backend;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.GpsDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.ArrayList;


public class ImageCentral {
    //this is the names of the various kinds of metadata we are interested in in com.drew.metadata methods
    private List<String> interestingMetadata = Arrays.asList("File Size","Date/Time Original", "Image Height", "Image Width", "GPS Latitude", "GPS Longitude");

    int noOfData = interestingMetadata.size();
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
    public String[] getMetaData(File file) throws ImageProcessingException, IOException {
        //array with metadata
        String[] metaArray = new String[noOfData];
        //reads metadata
        Metadata metadata = ImageMetadataReader.readMetadata(file);
        //iterates through directory
        for (Directory directory : metadata.getDirectories()) {
            //iterates through tags in directory
            for (Tag tag : directory.getTags()) {
                if(interestingMetadata.contains(tag)){
                    metaArray[interestingMetadata.indexOf(tag)] = tag.toString();
                }
            }
        }
        for(GpsDirectory directory : metadata.getDirectoriesOfType(GpsDirectory.class)){
            for (Tag tag : directory.getTags()) {
                if(interestingMetadata.contains(tag)){
                    metaArray[interestingMetadata.indexOf(tag)] = tag.toString();
                }
            }
        }
        return metaArray;
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

