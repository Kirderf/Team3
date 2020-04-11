package backend.database;

import org.eclipse.persistence.descriptors.ClassDescriptor;

import javax.persistence.*;
import java.io.Serializable;

//should we have a named query?
//named this way to avoid confusion with existing Image classes
@Entity
public class ImageDAO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long imageID;
    private String path;
    private long userID;
    private int fileSize;
    private int date;
    private int imageHeight;
    private int imageWidth;
    private double latitude;
    private double longitude;
    private String tags;

    /**
     * generates a new imageDAO object to be saved in the database
     * @param userID user userID for user saving this image, imageID is generated automatically
     * @param path path to where the image is saved
     * @param fileSize metadata
     * @param date metadata
     * @param imageHeight metadata
     * @param imageWidth metadata
     * @param latitude metadata
     * @param longitude metadata
     */
    public ImageDAO(long userID, String path, int fileSize, int date, int imageHeight, int imageWidth, double latitude, double longitude) {
        this.userID = userID;
        this.path = path;
        this.fileSize = fileSize;
        this.date = date;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tags = "";
    }

    /**
     * constructor without paramaters according to netbeans standard
     */
    public ImageDAO() {
        //TODO check if this is necessary
        ClassDescriptor.shouldUseFullChangeSetsForNewObjects = true;
    }

    /**
     * gets the image id for this image
     * @return int value of the users TENANT_ID
     */
    long getUserID() {
        return userID;
    }

    /**
     * sets the user id to a new value
     * @param userID the int you want to set it to
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * gets the path of the image
     * @return String containing the path to the local image
     */
    public String getPath() {
        return path;
    }

    /**
     * sets the path for a image
     * @param path the String you want to set it to
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * returns the file size of this image
     * @return int representing the size of the image in bytes
     */
    int getFileSize() {
        return fileSize;
    }

    /**
     * sets the file size
     * @param fileSize the int you want to set it to
     */
    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * gets the current date
     * @return the date in the format yyyymmdd
     */
    int getDate() {
        return date;
    }

    /**
     * sets the date to a specific value
     * @param date should be in the format yyyymmdd
     */
    public void setDate(int date) {
        this.date = date;
    }

    /**
     * gets the height of an image
     * @return height in number of pixels
     */
    int getImageHeight() {
        return imageHeight;
    }

    /**
     * sets the height to a value
     * does not actually change the image, only the value in the object
     * @param imageHeight height in pixels you want to set it to
     */
    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
    /**
     * gets the width of an image
     * @return width in pixels
     */
    int getImageWidth() {
        return imageWidth;
    }

    /**
     * sets the height to a value
     * does not actually change the image, only the value in the object
     * @param imageWidth width in pixels you want to set it to
     */
    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    /**
     * image latitude
     * @return 0 if the image does not have valid gps data, otherwise latitude in decimals
     */
    double getLatitude() {
        return latitude;
    }

    /**
     * sets the image latitude to a value
     * does not change the image metadata, only this object
     * @param latitude latitude in decimals
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * image longitude
     * @return 0 if the image does not have valid gps data, otherwise longitude in decimals
     */
    double getLongitude() {
        return longitude;
    }

    /**
     * Sets the image longitude to a value
     * does not change the image metadata, only this object
     * @param longitude longitude in decimals
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * gets the tags that have been added to the object
     * @return a string of tags with commas seperating every tag, empty string if no tags are present
     */
    public String getTags() {
        return tags;
    }

    /**
     * sets the tags to a specific string
     * @param tags String you want to set the tags field to
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * adds tag to the object formatting it automatically
     * @param tag the tag you want to add, should not contain commas
     */
    void addTag(String tag) {
        if (this.tags.length() != 0) {
            this.tags += "," + tag;
        } else {
            this.tags += tag;
        }
    }

    /**
     * @return String with all the information of the object
     */
    @Override
    public String toString() {
        return "Image{" +
                "fileSize=" + fileSize +
                ", Date=" + date +
                ", imageHeight=" + imageHeight +
                ", imageWidth=" + imageWidth +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", tags =" + tags +
                '}';
    }

    /**
     * Two imageDAO objects are equal if their path is equal and their userID is equal
     * @param obj the object you want to compare it to
     * @return true if they are equal, false if not
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ImageDAO) {
            //if the path is equal then the Images are equal
            return (((ImageDAO) obj).getPath().equalsIgnoreCase(this.getPath())&&this.getUserID() == ((ImageDAO) obj).getUserID());
        }
        return false;
    }
}
