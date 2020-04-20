package backend.database;

import org.eclipse.persistence.annotations.CascadeOnDelete;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The AlbumDAO class represents an album created in the application.
 * Each object of this class gets is assigned a name, list of images and
 * the UserDAO that it's bound to.
 */
@Entity
public class AlbumDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int albumId;
    private String albumName;
    @ManyToMany(targetEntity = ImageDAO.class)
    @CascadeOnDelete
    private List<ImageDAO> imageList;
    @ManyToOne
    private UserDAO userDAO;

    /**
     * Instantiates an empty AlbumDAO object.
     */
    public AlbumDAO() {
    }

    /**
     * Instantiates an AlbumDAO object.
     *
     * @param albumName the album name
     * @param imageList the image list
     * @param userDAO   the user that made the album
     */
    AlbumDAO(String albumName, List<ImageDAO> imageList, UserDAO userDAO) {
        this.albumName = albumName;
        this.imageList = imageList;
        this.userDAO = userDAO;
    }

    /**
     * Gets album name.
     *
     * @return the album name
     */
    String getAlbumName() {
        return albumName;
    }

    /**
     * Adds an image.
     *
     * @param imageDAO imageDAO to be added
     */
    void addImage(ImageDAO imageDAO) {
        imageList.add(imageDAO);
    }

    /**
     * Gets the ID of the user who created the album.
     *
     * @return userID as a long
     */
    long getUserID() {
        return userDAO.getAccountID();
    }

    /**
     * Get the List of images in the album
     *
     * @return a List with images
     */
    public List<ImageDAO> getImages() {
        return imageList;
    }

    /**
     * Sets the List of images in the album.
     *
     * @param imageList the new image list
     */
    public void setImages(List<ImageDAO> imageList) {
        this.imageList = imageList;
    }

    List<String> getImagePaths() {
        return getImages().stream().map(ImageDAO::getPath).collect(Collectors.toList());
    }
}
