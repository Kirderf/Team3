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
import java.util.*;

/**
 * This class handles imported images. It makes sure they're of the
 * right file type, and gets their metadata.
 */
public abstract class ImageImport {
    private static final Log logger = new Log();
    //this is the names of the various kinds of metadata we are interested in in com.drew.metadata methods
    private static List<String> interestingMetadata = Arrays.asList("File Size", "Date/Time Original", "Image Height", "Image Width", "GPS Latitude", "GPS Longitude", "File Modified Date");
    private static int noOfData = interestingMetadata.size() - 1;
    //needs to be all lowercase, update if we accept other file types
    private static List<String> validImageExtensions = Arrays.asList("jpg", "png", "jpeg");
    private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    private static boolean isImage(File file) {
        try {
            if (file.exists()) {
                return validImageExtensions.contains(FilenameUtils.getExtension(file.getPath()).toLowerCase());
            }
            return false;
        } catch (Exception e) {
            logger.logNewFatalError("ImageImport : " + e.getLocalizedMessage());
            return false;
        }
    }

    private static Double conMinutesToDecimal(String latOrLong) {
        //number of seconds
        double second = Double.parseDouble(latOrLong.substring(latOrLong.indexOf(" "), latOrLong.indexOf("'"))) * 60 + Double.parseDouble(latOrLong.substring(latOrLong.indexOf("' ") + 1, latOrLong.length() - 1).replaceAll(",", "."));
        return Double.parseDouble(latOrLong.substring(0, latOrLong.indexOf("°"))) + second / 3600;
    }

    /**
     * Returns an Array with a given file's interesting metadata.
     * If a given metadata is null, then it's value is set to -1.
     *
     * @param pathToFile the path to the file
     * @return an Array with the interesting metadata, or null if the file is invalid
     */
    public static String[] getMetaData(String pathToFile) {
        File file = new File(pathToFile);
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
                        if (interestingMetadata.contains(tag.getTagName())) {
                            //png images have slightly different metadata for dates, this fixes that
                            if (tag.getTagName().equals("Date/Time Original") || tag.getTagName().equals("File Modified Date")) {
                                //this is preferable as this is a more relevant date
                                if (tag.getDescription().equals("Date/Time Original")) {
                                    hasDateTime = true;
                                    Date date1 = directory.getDate(tag.getTagType());
                                    LocalDate localDate = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    String formattedDate = localDate.toString().replaceAll("-", "");
                                    metaArray[interestingMetadata.indexOf("Date/Time Original")] = formattedDate;
                                } else if (tag.getTagName().equalsIgnoreCase("File Modified Date") && !hasDateTime) {
                                    Date date1 = directory.getDate(tag.getTagType());
                                    LocalDate localDate = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    String formattedDate = localDate.toString().replaceAll("-", "");
                                    metaArray[interestingMetadata.indexOf("Date/Time Original")] = formattedDate;
                                }
                                //the new tag is placed in the index that corresponds with the index of the tag in the interestingmetadata array
                            } else {
                                metaArray[interestingMetadata.indexOf(tag.getTagName())] = tag.getDescription();
                            }
                        }
                    }
                }
                for (GpsDirectory directory : metadata.getDirectoriesOfType(GpsDirectory.class)) {
                    for (Tag tag : directory.getTags()) {
                        if (tag.getTagName().equals("GPS Latitude")) {
                            //this gives a value in degrees, minutes and seconds, but it is converted to decimal, then converted to string
                            metaArray[interestingMetadata.indexOf(tag.getTagName())] = "" + conMinutesToDecimal(tag.getDescription());
                        } else if (tag.getTagName().equals("GPS Longitude")) {
                            metaArray[interestingMetadata.indexOf(tag.getTagName())] = "" + conMinutesToDecimal(tag.getDescription());
                        }
                    }
                }
                //goes through the metadata and makes sure it is not null
                //date
                if (metaArray[1] == null) {
                    metaArray[1] = format.format(new Date());
                } else {
                    //if date is not null
                    metaArray[1] = metaArray[1].replaceAll(":", "").substring(0, 8).trim();
                }
                //file size
                if (metaArray[0] != null) {
                    metaArray[0] = metaArray[0].replaceAll("bytes", "").trim();
                } else {
                    metaArray[0] = "-1";
                }
                //image height
                if (metaArray[2] != null) {
                    metaArray[2] = metaArray[2].replaceAll("pixels", "").trim();
                } else {
                    metaArray[2] = "-1";
                }
                //image width
                if (metaArray[3] != null) {
                    metaArray[3] = metaArray[3].replaceAll("pixels", "").trim();
                } else {
                    metaArray[3] = "-1";
                }
                //latitude
                if (metaArray[4] == null) {
                    metaArray[4] = "-1";
                }
                //longitude
                if (metaArray[5] == null) {
                    metaArray[5] = "-1";
                }
                return metaArray;
            } else {
                throw new IllegalArgumentException("The file does not exist, or is not an image");
            }
            //thrown by the metadata-library we are using
        } catch (Exception e) {
            logger.logNewFatalError("ImageImport : " + e.getLocalizedMessage());
        }
        //if the file this is run on is not a valid image
        return null;
    }
}

