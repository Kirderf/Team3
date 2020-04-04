package backend.database;

import org.eclipse.persistence.descriptors.ClassDescriptor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

//should we have a named query?
//named this way to avoid confusion with existing Image classes
@Entity
public class ImageDAO implements Serializable {
    @Id
    private String path;
    private int ID;
    private int fileSize;
    private int date;
    private int imageHeight;
    private int imageWidth;
    private double latitude;
    private double longitude;
    private String tags;

    public ImageDAO(int id, String path, int fileSize, int date, int imageHeight, int imageWidth, double latitude, double longitude) {
        this.ID = id;
        this.path = path;
        this.fileSize = fileSize;
        this.date = date;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tags = "";
    }

    public ImageDAO() {
        ClassDescriptor.shouldUseFullChangeSetsForNewObjects = true;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void addTag(String tag) {
        if (this.tags.length() != 0) {
            this.tags += "," + tag;
        } else {
            this.tags += tag;
        }
    }

    @Override
    public String toString() {
        return "Image{" +
                "fileSize=" + fileSize +
                ", Date=" + date +
                ", imageHeight=" + imageHeight +
                ", imageWidth=" + imageWidth +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ImageDAO) {
            //if the path is equal then the Images are equal
            return (((ImageDAO) obj).getPath().equalsIgnoreCase(this.getPath()));
        }
        return false;
    }
}
