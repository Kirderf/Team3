package backend;

import com.drew.imaging.ImageProcessingException;

import java.io.File;
import java.io.IOException;

public class DatabaseClient {
    Database imageDatabase = new Database();
    ImageImport imageImport = new ImageImport();

    public boolean closeApplication(){
      /*  if(imageDatabase.closeDatabase()){
            return true;
        }*/
      return false;
    }
    public boolean addImage(File image) throws ImageProcessingException, IOException {
        String[] metadata = imageImport.getMetaData(image);
        if(metadata != null){
            //imageDatabase.writeToDatabase(image.getPath(), "", Integer.parseInt(metadata[0]), Date.parse(metadata[1]),Integer.parseInt(metadata[2]),Integer.parseInt(metadata[3]),Double.parseDouble(metadata[4]),Double.parseDouble(metadata[5]));
           // imageDatabase.closeConnection();
        }
        return false;
    }
    public boolean removeImage(){
        return false;
    }
    public boolean isInDatabase(){
        return false;
    }

}
