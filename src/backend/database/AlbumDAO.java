package backend.database;

import javax.persistence.*;
import java.util.List;

/**
 * The type Album dao.
 */
@Entity
public class AlbumDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int albumId;
    private String albumName;
    @ManyToMany(targetEntity = ImageDAO.class)
    private List<ImageDAO> imageList;
    private int userID;

    /**
     * Instantiates a new Album dao.
     */
    public AlbumDAO() {
    }

    /**
     * Instantiates a new Album dao.
     *
     * @param albumName the album name
     * @param imageList the image list
     * @param userID    the user id
     */
    public AlbumDAO(String albumName, List<ImageDAO> imageList, int userID) {
        this.albumName = albumName;
        this.imageList = imageList;
        this.userID = userID;
    }

    /**
     * Gets album id.
     *
     * @return the album id
     */
    public int getAlbumId() {
        return albumId;
    }

    /**
     * Get name string.
     *
     * @return the string
     */
    public String getName(){
        return albumName;
    }

    /**
     * Sets album id.
     *
     * @param albumId the album id
     */
    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    /**
     * Remove image.
     *
     * @param imageDAO the image dao
     */
    public void removeImage(ImageDAO imageDAO){
        imageList.remove(imageDAO);
    }

    /**
     * Add image.
     *
     * @param imageDAO the image dao
     */
    public void addImage(ImageDAO imageDAO){
        imageList.add(imageDAO);
    }

    /**
     * Gets album name.
     *
     * @return the album name
     */
    public String getAlbumName() {
        return albumName;
    }

    /**
     * Sets album name.
     *
     * @param albumName the album name
     */
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    /**
     * Get user id int.
     *
     * @return the int
     */
    public int getUserID(){
        return userID;
    }

    /**
     * Get images list.
     *
     * @return the list
     */
    public List getImages(){
        return imageList;
    }

    /**
     * Clear images.
     */
    public void clearImages(){
        this.imageList.clear();
    }

    /**
     * Set images.
     *
     * @param imageList the image list
     */
    public void setImages(List imageList){
        this.imageList = imageList;
    }
}
