package backend;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.GpsDirectory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Ingebrigt Hovind
 */
public class ImageImport {
    //this is the names of the various kinds of metadata we are interested in in com.drew.metadata methods
    private List<String> interestingMetadata = Arrays.asList("File Size","Date/Time Original", "Image Height", "Image Width", "GPS Latitude", "GPS Longitude");
    private int noOfData = interestingMetadata.size();
    //needs to be all lowercase, update if we accept other file types
    private List<String> validImageExtensions = Arrays.asList(".jpg",".png");

    /**
     * Checks whether a file is an image or not based on the extension, validImageExtions contains all file extensions that are valid
     * public for tests only
     * @param file the file that is to be checked if it is an image
     * @return true if the file has an extension that is in validImageExtensions
     * @author Ingebrigt Hovind
     */
    public boolean isImage(File file){
        try {
            if(file != null){
                return validImageExtensions.contains(getExtensionFromFile(file).toLowerCase());
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    //public for tests only

    /**
     * Converts from degrees, minutes and seconds to just degrees
     * @param latOrLong either latitude or longitude
     * @return the corresponding coordinate in decimal form
     * @author Ingebrigt Hovind
     */
    public Double conMinutesToDecimal(String latOrLong) {
        //number of seconds
        double second = Double.parseDouble(latOrLong.substring(latOrLong.indexOf(" "),latOrLong.indexOf("'")))*60 + Double.parseDouble(latOrLong.substring(latOrLong.indexOf("' ")+1,latOrLong.length()-1).replaceAll(",","."));
        return Double.parseDouble(latOrLong.substring(0,latOrLong.indexOf("°"))) + second/3600;
    }
    /**
     * returns an array with the interesting metadata, the metadata is in the same order as the interestingmetadata arraylist
     * needs to be public
     * @param file the file you want to find the metadata for
     * @return an array with the interesting metadata, the metadata is in the same order as the interestingmetadata arraylist, null if no corresponding data is found
     * @throws ImageProcessingException com.drew.metadata
     * @throws IOException needs to read a file from com.drew.metadata
     * @author Ingebrigt Hovind
     */
    public String[] getMetaData(File file) throws ImageProcessingException, IOException {
        try {
            if (isImage(file)) {
                //array with metadata
                String[] metaArray = new String[noOfData];
                //reads metadata
                Metadata metadata = ImageMetadataReader.readMetadata(file.getAbsoluteFile());
                //iterates through directory
                for (Directory directory : metadata.getDirectories()) {
                    //iterates through tags in directory
                    for (Tag tag : directory.getTags()) {
                        //if the tag is part of the tags we are interested in
                        if(interestingMetadata.contains(tag.getTagName())){
                            //the new tag is placed in the index that corresponds with the index of the tag in the interestingmetadata array
                            metaArray[interestingMetadata.indexOf(tag.getTagName())] = tag.getDescription();
                        }
                    }
                }
                for(GpsDirectory directory : metadata.getDirectoriesOfType(GpsDirectory.class)){
                    for (Tag tag : directory.getTags()) {
                        if(tag.getTagName().equals("GPS Latitude")){
                            //this gives a value in degrees, minutes and seconds, but it is converted to decimal, then converted to string
                            metaArray[interestingMetadata.indexOf(tag.getTagName())] = ""+ conMinutesToDecimal(tag.getDescription());
                        }
                        else if(tag.getTagName().equals("GPS Longitude")){
                            metaArray[interestingMetadata.indexOf(tag.getTagName())] = ""+ conMinutesToDecimal(tag.getDescription());
                        }
                    }
                }
                //even if there is no interesting metadata, then an empty array is returned
                if (metaArray[1] == null){
                    metaArray[1] = "0";
                }else {
                    metaArray[1] = metaArray[1].replaceAll(":","").substring(0,8).trim();
                }
                if (metaArray[0] != null){
                    metaArray[0] = metaArray[0].replaceAll("bytes","").trim();
                }
                if (metaArray[2] != null){
                    metaArray[2] = metaArray[2].replaceAll("pixels","").trim();
                }
                if (metaArray[3] != null){
                    metaArray[3] = metaArray[3].replaceAll("pixels","").trim();
                }
                if (metaArray[4] == null){
                    metaArray[4] = "0";
                }
                if (metaArray[5] == null){
                    metaArray[5] = "0";
                }
                return metaArray;

            }
            //thrown by the metadata-library we are using
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //if the file this is run on is not a valid image
        return null;
    }


    /**
     * gets the extension from a given file, used to verify that the file is an image
     * @param file the file you want to find the extension for
     * @return a string with the extension, including the full stop
     * @author Ingebrigt Hovind
     */
    public String getExtensionFromFile(File file){
        //public for tests only
        return file.getPath().substring(file.getPath().lastIndexOf("."));
    }

}

