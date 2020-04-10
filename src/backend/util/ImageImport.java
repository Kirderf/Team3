package backend.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.GpsDirectory;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Ingebrigt Hovind
 */
public abstract class ImageImport {
    private static final Log logger = new Log();
    //this is the names of the various kinds of metadata we are interested in in com.drew.metadata methods
    private static List<String> interestingMetadata = Arrays.asList("File Size","Date/Time Original", "Image Height", "Image Width", "GPS Latitude", "GPS Longitude","File Modified Date");
    private static int noOfData = interestingMetadata.size() -1;
    //needs to be all lowercase, update if we accept other file types
    private static List<String> validImageExtensions = Arrays.asList("jpg","png","jpeg");
    private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    /**
     * Checks whether a file is an image or not based on the extension, validImageExtions contains all file extensions that are valid
     * public for tests only
     * @param file the file that is to be checked if it is an image
     * @return true if the file has an extension that is in validImageExtensions
     * @author Ingebrigt Hovind
     */
    private static boolean isImage(File file){
        try {
            if(file.exists()){
                return validImageExtensions.contains(FilenameUtils.getExtension(file.getPath()).toLowerCase());
            }
            return false;
        } catch (Exception e) {
            logger.logNewFatalError("ImageImport : " + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Converts from degrees, minutes and seconds to just degrees
     * @param latOrLong either latitude or longitude
     * @return the corresponding coordinate in decimal form
     * @author Ingebrigt Hovind
     */
    private static Double conMinutesToDecimal(String latOrLong) {
        //number of seconds
        double second = Double.parseDouble(latOrLong.substring(latOrLong.indexOf(" "),latOrLong.indexOf("'")))*60 + Double.parseDouble(latOrLong.substring(latOrLong.indexOf("' ")+1,latOrLong.length()-1).replaceAll(",","."));
        return Double.parseDouble(latOrLong.substring(0,latOrLong.indexOf("°"))) + second/3600;
    }
    /**
     * returns an array with the interesting metadata, the metadata is in the same order as the interestingmetadata arraylist
     * needs to be public
     * @param file the file you want to find the metadata for
     * @return an array with the interesting metadata, the metadata is in the same order as the interestingmetadata arraylist, null if no corresponding data is found
     * @author Ingebrigt Hovind
     */
    public static String[] getMetaData(File file) {
        logger.logNewInfo("ImageImport : " + "Getting metadata from file");
        try {
            if (isImage(file)) {
                //array with metadata
                String[] metaArray = new String[noOfData];
                //reads metadata
                Metadata metadata = ImageMetadataReader.readMetadata(file.getAbsoluteFile());
                //iterates through directory
                boolean hasDateTime = false;
                for (Directory directory : metadata.getDirectories()) {
                    //iterates through tags in directory
                    for (Tag tag : directory.getTags()) {
                        //if the tag is part of the tags we are interested in
                        if(interestingMetadata.contains(tag.getTagName())){
                            //png images have slightly different metadata for dates, this fixes that
                            if(tag.getTagName().equals("Date/Time Original")||tag.getTagName().equals("File Modified Date")){
                                //this is preferable as this is a more relevant date
                                if(tag.getDescription().equals("Date/Time Original")){
                                    hasDateTime = true;
                                    Date date1 = directory.getDate(tag.getTagType());
                                    LocalDate localDate = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    System.out.println(localDate.toString());
                                    String formattedDate = localDate.toString().replaceAll("-","");
                                    metaArray[interestingMetadata.indexOf("Date/Time Original")] = formattedDate;
                                }
                                else if(tag.getTagName().equalsIgnoreCase("File Modified Date")&&!hasDateTime){
                                    Date date1 = directory.getDate(tag.getTagType());
                                    LocalDate localDate = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    System.out.println(localDate.toString());
                                    String formattedDate = localDate.toString().replaceAll("-","");
                                    metaArray[interestingMetadata.indexOf("Date/Time Original")] = formattedDate;
                                }
                                //the new tag is placed in the index that corresponds with the index of the tag in the interestingmetadata array
                            }
                            else {
                                metaArray[interestingMetadata.indexOf(tag.getTagName())] = tag.getDescription();
                            }
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
                    metaArray[1] = format.format(new Date());
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
            else{
                throw new IllegalArgumentException("The file does not exist, or is not an image");
            }
            //thrown by the metadata-library we are using
        } catch(Exception e){
            logger.logNewFatalError("ImageImport : " + e.getLocalizedMessage());
        }
        //if the file this is run on is not a valid image
        return null;
    }
}

