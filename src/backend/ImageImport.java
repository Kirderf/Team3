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


public class ImageImport {
    //this is the names of the various kinds of metadata we are interested in in com.drew.metadata methods
    private List<String> interestingMetadata = Arrays.asList("File Size","Date/Time Original", "Image Height", "Image Width", "GPS Latitude", "GPS Longitude");
    private int noOfData = interestingMetadata.size();


    public boolean isImage(File file){
        try {
            if(file.exists()){
                return getExtensionFromFile(file).equalsIgnoreCase(".jpg") || getExtensionFromFile(file).equalsIgnoreCase(".png");
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public Double conMinutesToDecimal(String latOrLong) {
        //number of seconds
        double second = Double.parseDouble(latOrLong.substring(latOrLong.indexOf(" "),latOrLong.indexOf("'")))*60 + Double.parseDouble(latOrLong.substring(latOrLong.indexOf("' ")+1,latOrLong.length()-1).replaceAll(",","."));
        return Double.parseDouble(latOrLong.substring(0,latOrLong.indexOf("°"))) + second/3600;
    }
    public String[] getMetaData(File file) throws ImageProcessingException, IOException {
        try {
            if (isImage(file)) {
                //array with metadata
                String[] metaArray = new String[noOfData];
                //reads metadata
                Metadata metadata = ImageMetadataReader.readMetadata(file);
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
    public String getExtensionFromFile(File file){
        return file.getPath().substring(file.getPath().lastIndexOf("."));
    }

}

