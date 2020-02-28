package backend;
import com.drew.imaging.ImageProcessingException;
import java.io.File;
import java.io.IOException;

public class DatabaseClient {
    private Database imageDatabase = new Database();
    private ImageImport imageImport = new ImageImport();
    //TODO delete tables when closing program
    public boolean closeApplication(){
        return false;
    }
    //do we actually need this, or will the class variable create the new database?
    public boolean openApplication(){
        return false;
    }
    //TODO add image with metadata to database
    public boolean addImage(File image) throws ImageProcessingException, IOException {
        try {
            String[] metadata = imageImport.getMetaData(image);
            if(metadata != null){
                //imageDatabase.writeToDatabase(image.getPath(), "", Integer.parseInt(metadata[0]), Date.parse(metadata[1]),Integer.parseInt(metadata[2]),Integer.parseInt(metadata[3]),Double.parseDouble(metadata[4]),Double.parseDouble(metadata[5]));
               // imageDatabase.closeConnection();
            }
            return false;
        } catch (ImageProcessingException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    //TODO given a path, this should remove the image from the database
    public boolean removeImage(String path){
        return false;
    }
    //TODO given a path to a specific image, this should return null if the image is not in the database, and an array with metadata if it is
    public String[] getMetaDataFromDatabase(String path){
        return null;
    }
    //TODO add tag funtionality
    public boolean addTag(String tag){
        return false;
    }
    //TODO add pdf funtionality
    public boolean exportToPdf(){
        return false;
    }
}
