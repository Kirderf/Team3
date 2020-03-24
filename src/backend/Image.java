package backend;

import java.util.Date;
import javax.persistence.*;
//should we have a named query?
@Entity
public class Image {
    @Id
    private String path;
    private int fileSize;
    private Date date;
    private int imageHeight;
    private int imageWidth;
    private int latitude;
    private int longitude;

    public Image(String path, int fileSize, java.util.Date date, int imageHeight, int imageWidth, int latitude, int longitude) {
        this.fileSize = fileSize;
        this.date = date;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPath(){
        return path;
    }
    public void setPath(String path){
        this.path = path;
    }
    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
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

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
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
        if(obj == this){
            return true;
        }
        if(obj instanceof Image){
            //if the path is equal then the Images are equal
            return (((Image) obj).getPath().equalsIgnoreCase(this.getPath()));
        }
        return false;
    }
}
