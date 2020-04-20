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
    ImageDAO(UserDAO userDAO, String path, int fileSize, int date,
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
     * Gets the thumbnail of this image object
     *
     * @return resized image object
     * @throws MalformedURLException calls {@link ImageDAO#resize()} function which may throw this error
     */
    Image getThumbnail() throws MalformedURLException {
        return this.resize();
    }

    private Image resize() throws MalformedURLException {
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
     * Returns the file size of an image
     *
     * @return int representing the size of the image in bytes
     */
    int getFileSize() {
        return fileSize;
    }

    /**
     * Gets the date
     *
     * @return the date as an int in the format yyyymmdd
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
     * Gets the width of an image
     *
     * @return width in pixels
     */
    int getImageWidth() {
        return imageWidth;
    }

    /**
     * Gets the image latitude if it has one
     *
     * @return 0 if the image does not have valid gps data, otherwise latitude in decimals
     */
    double getLatitude() {
        return latitude;
    }

    /**
     * Gets the image longitude if it has one
     *
     * @return 0 if the image does not have valid gps data, otherwise longitude in decimals
     */
    double getLongitude() {
        return longitude;
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
