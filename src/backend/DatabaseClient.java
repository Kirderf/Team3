package backend;
import com.drew.imaging.ImageProcessingException;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


//TODO add javadoc
public class DatabaseClient {
    private Database imageDatabase = new Database();
    private ImageImport imageImport = new ImageImport();
    private ArrayList<String> addedPaths = new ArrayList<>();


    //TODO delete tables when closing program
    public boolean closeApplication() throws SQLException {
        if (!imageDatabase.isConnection()){
            imageDatabase.openConnection();
        }
        return imageDatabase.closeDatabase();
    }
    public boolean closeConnection() throws SQLException{
        return imageDatabase.closeConnection();
    }

    /**
     * method checks if all the paths in addedPaths are also present in the sql database
     * @return true if they are all present, false if not
     * @throws SQLException
     * @author Ingebrigt Hovind
     */
    public boolean listEqualToDatabase() throws SQLException {
        if(getData("Path").size() != addedPaths.size()){
            return false;
        }
        for(String s : addedPaths){
            if(!imageDatabase.isTableInDatabase(s)){
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param columnName eks: Path,ImageID,Tags,File_size,DATE,Height,Width.
     * @return An arraylist of data objects
     * @throws SQLException could not find input from columnName
     */
    public ArrayList getData(String columnName) throws SQLException {
        imageDatabase.openConnection();
       ArrayList arrayList = imageDatabase.getColumn(columnName);
       imageDatabase.closeConnection();
        return arrayList;
    }
    //TODO add image with metadata to database
    public boolean addImage(File image) throws ImageProcessingException, IOException {
        try {
            imageDatabase.openConnection();
            String[] metadata = imageImport.getMetaData(image);
            if(metadata != null) {
                try {
                    if (imageDatabase.addImageToTable(
                            image.getPath(),
                            "",
                            Integer.parseInt(metadata[0]),
                            Long.parseLong(metadata[1]),
                            Integer.parseInt(metadata[2]),
                            Integer.parseInt(metadata[3]),
                            Double.parseDouble(metadata[4]),
                            Double.parseDouble(metadata[5]))) {
                        addedPaths.add(image.getPath());
                    }
                    return true;
                    }catch (SQLIntegrityConstraintViolationException ignored){
                    System.out.println("Already in database");
                }
                return false;
            }
        } catch (ImageProcessingException | IOException | SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean removeImage(String path) throws SQLException {
        if(imageDatabase.deleteFromDatabase(path)){
            return true;
        }
        return false;
    }
    //TODO given a path to a specific image, this should return null if the image is not in the database, and an array with metadata if it is
    public String[] getMetaDataFromDatabase(String path) throws SQLException {
        imageDatabase.openConnection();
        String[] result = imageDatabase.getImageMetadata(path);
        imageDatabase.closeConnection();
        return result;
    }

    /**
     * Legger til tags i databasen
     * @param path path til bilde
     * @param tag String[] av tags
     * @return boolean
     * @throws SQLException
     */
    public boolean addTag(String path,String[] tag) throws SQLException {
        imageDatabase.openConnection();
        boolean result =imageDatabase.addTags(path,tag);
        imageDatabase.closeConnection();
        return result;
    }
}
