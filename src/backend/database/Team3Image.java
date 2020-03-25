package backend.database;

import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;

import javax.naming.Name;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Random;
import javax.persistence.*;
//should we have a named query?
//named this way to avoid confusion with existing Image classes
@Entity
public class Team3Image implements Serializable {
    @Id
    private String path;
    private int fileSize;
    private long date;
    private int imageHeight;
    private int imageWidth;
    private double latitude;
    private double longitude;
    private String tags;

    public Team3Image(String path, int fileSize, long date, int imageHeight, int imageWidth, double latitude, double longitude) {
        String.valueOf(new Random().nextInt(500));
        this.path = path;
        this.fileSize = fileSize;
        this.date = date;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tags = "";
    }
    public Team3Image(){
        ClassDescriptor.shouldUseFullChangeSetsForNewObjects = true;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
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
            this.tags = "," + tag;
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
        if (obj instanceof Team3Image) {
            //if the path is equal then the Images are equal
            return (((Team3Image) obj).getPath().equalsIgnoreCase(this.getPath()));
        }
        return false;
    }
}
