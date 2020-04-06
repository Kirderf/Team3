package backend.database;

import javax.persistence.*;
import java.util.List;

@Entity
public class AlbumDAO {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int albumId;
    private String albumName;
    @ManyToMany(targetEntity = ImageDAO.class)
    private List<ImageDAO> imageList;
    private int userID;

    public AlbumDAO() {
    }

    public AlbumDAO(String albumName, List<ImageDAO> imageList, int userID) {
        this.albumName = albumName;
        this.imageList = imageList;
        this.userID = userID;
    }

    public int getAlbumId() {
        return albumId;
    }

    public String getName(){
        return albumName;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public void removeImage(ImageDAO imageDAO){
        imageList.remove(imageDAO);
    }

    public void addImage(ImageDAO imageDAO){
        imageList.add(imageDAO);
    }
    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public int getUserID(){
        return userID;
    }

    public List getImages(){
        return imageList;
    }

    public void clearImages(){
        this.imageList.clear();
    }

    public void setImages(List imageList){
        this.imageList = imageList;
    }
}
