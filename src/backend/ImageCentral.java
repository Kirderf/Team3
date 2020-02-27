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
    public Double[] minutesToDecimal(String latitude, String longitude) {
        double longitudeSecond = Double.parseDouble(longitude.substring(longitude.indexOf(" "),longitude.indexOf("'")))*60 + Double.parseDouble(longitude.substring(longitude.indexOf("' ")+2).replaceAll(",","."));
        Double latitudeSecond =  Double.parseDouble(latitude.substring(latitude.indexOf(" "),latitude.indexOf("'")))*60 + Double.parseDouble(latitude.substring(latitude.indexOf("' ")+2).replaceAll(",","."));
        double conLatitude = Double.parseDouble(latitude.substring(0,latitude.indexOf("°"))) + latitudeSecond/3600;
        double conLongitude = Double.parseDouble(longitude.substring(0,longitude.indexOf("°"))) + longitudeSecond/3600;
        return new Double[]{conLatitude,conLongitude};
    }
    public String[] getMetaData(File file) throws ImageProcessingException, IOException {
        if (isImage(file)) {
            //array with metadata
            String[] metaArray = new String[noOfData];
            //reads metadata
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            //iterates through directory
            for (Directory directory : metadata.getDirectories()) {
                //iterates through tags in directory
                for (Tag tag : directory.getTags()) {
                    if(interestingMetadata.contains(tag.getTagName())){
                        metaArray[interestingMetadata.indexOf(tag.getTagName())] = tag.getDescription();
                    }
                }
            }
            for(GpsDirectory directory : metadata.getDirectoriesOfType(GpsDirectory.class)){
                for (Tag tag : directory.getTags()) {
                    if(interestingMetadata.contains(tag.getTagName())){
                        metaArray[interestingMetadata.indexOf(tag.getTagName())] = tag.getDescription();
                    }
                }
            }
            return metaArray;
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
        //only jpg and png are supported
        if(isImage(file)){
            return file.getPath();

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

