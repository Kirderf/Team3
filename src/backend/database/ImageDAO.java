package backend.database;

import javafx.scene.image.Image;

import javax.persistence.*;
import java.io.File;
import java.net.MalformedURLException;

/**
 * This class creates ImageDAO objects that represent the
 * images being added to the database.
 */
//should we have a named query?
//named this way to avoid confusion with existing Image classes
@Entity
public class ImageDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long imageID;
    private String path;
    private int fileSize;
    private int date;
    private int imageHeight;
    private int imageWidth;
    private double latitude;
    private double longitude;
    private String tags;
    @ManyToOne
    private UserDAO userDAO;

    /**
     * Generates a new ImageDAO object to be saved in the database.
     *
     * @param userDAO     The user this image is saved to
     * @param path        path to where the image is saved
     * @param fileSize    file size
     * @param date        creation date
     * @param imageHeight height in pixels
     * @param imageWidth  width in pixels
     * @param latitude    geographical latitude (if it has one)
     * @param longitude   geographical longitude (if it has one)
     */
    public ImageDAO(UserDAO userDAO, String path, int fileSize, int date,
                    int imageHeight, int imageWidth, double latitude, double longitude) {
        this.userDAO = userDAO;
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
     * Constructor without parameters according to NetBeans standard
     */
    public ImageDAO() {
    }

    /**
     * gets the thumbnail of this image object
     *
     * @return resized image object
     * @throws MalformedURLException calls resize function which may throw this error
     */
    public Image getThumbnail() throws MalformedURLException {
        return this.rezise();
    }

    private Image rezise() throws MalformedURLException {
        //requestedWidth is just a placeholder, simply needs to be bigger than height
        return new Image(new File(path).toURI().toURL().toExternalForm(), 186, 185, true, true);
    }

    /**
     * Gets userDAO.
     *
     * @return the userDAO
     */
    UserDAO getUserDAO() {
        return this.userDAO;
    }

    /**
     * Set userDAO.
     *
     * @param userDAO the userDAO
     */
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Gets the path of an image
     *
     * @return String containing the path to the local image
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the path of an image
     *
     * @param path the String you want to set it to
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Returns the file size of this image
     *
     * @return int representing the size of the image in bytes
     */
    int getFileSize() {
        return fileSize;
    }

    /**
     * Sets the file size
     *
     * @param fileSize the int you want to set it to
     */
    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Gets the current date
     *
     * @return the date in the format yyyymmdd
     */
    int getDate() {
        return date;
    }

    /**
     * Sets the date to a specific value
     *
     * @param date date in the format yyyymmdd
     */
    public void setDate(int date) {
        this.date = date;
    }

    /**
     * Gets the height of an image
     *
     * @return height in number of pixels
     */
    int getImageHeight() {
        return imageHeight;
    }

    /**
     * Sets the image's height to a value
     * This method does not actually change the image,
     * only the value in the object
     *
     * @param imageHeight height in pixels you want to set it to
     */
    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    /**
     * Gets the width of an image
     *
     * @return width in pixels
     */
    int getImageWidth() {
        return imageWidth;
    }

    /**
     * Sets the height to a value
     * This method does not actually change the image,
     * only the value in the object
     *
     * @param imageWidth width in pixels you want to set it to
     */
    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    /**
     * Image latitude
     *
     * @return 0 if the image does not have valid gps data, otherwise latitude in decimals
     */
    double getLatitude() {
        return latitude;
    }

    /**
     * Sets the image latitude to a value
     * Does not change the image metadata, only this object
     *
     * @param latitude latitude in decimals
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Image longitude
     *
     * @return 0 if the image does not have valid gps data, otherwise longitude in decimals
     */
    double getLongitude() {
        return longitude;
    }

    /**
     * Sets the image longitude to a value
     * Does not change the image metadata, only this object
     *
     * @param longitude longitude in decimals
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the tags that have been added to the object
     *
     * @return a string of tags with commas separating every tag,
     * or an empty string if no tags are present
     */
    public String getTags() {
        return tags;
    }

    /**
     * Sets the tags to a given string
     *
     * @param tags String you want to set the tags field to
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * Adds tag to the object, formatting it automatically
     *
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
        return "Image{"
                + "fileSize=" + fileSize
                + ", Date=" + date
                + ", imageHeight=" + imageHeight
                + ", imageWidth=" + imageWidth + ", latitude=" + latitude
                + ", longitude=" + longitude
                + ", tags =" + tags + '}';
    }

    /**
     * Two imageDAO objects are equal if their path is equal and the userId of their users are equal
     *
     * @param obj the object you want to compare it to
     * @return true if they are equal, false if not
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ImageDAO) {
            //if the path is equal and the users are equal then the Images are equal
            return (((ImageDAO) obj).getPath().equalsIgnoreCase(this.getPath())
                    && this.userDAO.equals(((ImageDAO) obj).userDAO));
        }
        return false;
    }

}
