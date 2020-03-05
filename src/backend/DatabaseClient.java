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

/**
 * @author Ingebrigt Hovind & Fredrik Julsen
 */
//TODO add javadoc
public class DatabaseClient {
    private Database imageDatabase;
    {
        try {
            imageDatabase = new Database();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ImageImport imageImport = new ImageImport();
    private static ArrayList<String> addedPaths = new ArrayList<>();

    /**
     *
     * @return
     * @throws SQLException
     */
    //TODO delete tables when closing program
    public boolean closeApplication() throws SQLException {
        if (imageDatabase.isConnection()){
            imageDatabase.openConnection();
        }
        return imageDatabase.closeDatabase();
    }
    public boolean closeConnection() throws SQLException{
        return imageDatabase.closeConnection();
    }

    /**
     * get all the paths that were added locally
     * @return arraylist with all the paths
     */
    public static ArrayList<String> getAddedPaths() {
        return addedPaths;
    }

    /**
     * method checks if all the paths in addedPaths are also present in the sql database
     * @return true if they are all present, false if not
     */
    public boolean addedPathsContains(String path) {
        return addedPaths.contains(path);
    }

    /**
     * clears all the added paths in the local list
     */
    public void clearPaths() {addedPaths.clear();}

    /**
     * Gets all the data in a given column, e.g all the paths
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

    /**
     * Adds a image to database
     * @param image imagefile to add
     * @return if the image was added to database
     */
    public boolean addImage(File image) {
        try {
            imageDatabase.openConnection();
            String[] metadata = imageImport.getMetaData(image);
            if(metadata != null) {
                try {
                    if (addedPathsContains(image.getPath())){
                        System.out.println("test");
                        imageDatabase.closeConnection();
                        return false;
                    }
                    if (imageDatabase.addImageToTable(
                            image.getPath(),
                            "",
                            Integer.parseInt(metadata[0]),
                            Long.parseLong(metadata[1]),
                            Integer.parseInt(metadata[2]),
                            Integer.parseInt(metadata[3]),
                            Double.parseDouble(metadata[4]),
                            Double.parseDouble(metadata[5]))) {
                    }
                    imageDatabase.closeConnection();
                    return true;
                    }catch (SQLIntegrityConstraintViolationException ignored){
                    System.out.println("Already in database");
                }
                imageDatabase.closeConnection();
                return false;
            }
        } catch (ImageProcessingException | IOException | SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * WORK IN PROGRESS
     * @param path
     * @return
     * @throws SQLException
     */
    @Deprecated
    public boolean removeImage(String path) throws SQLException {
        if(imageDatabase.deleteFromDatabase(path)){
            return true;
        }
        return false;
    }

    /**
     * Get metadata for one specific image
     * @param path path to image
     * @return String[] of metadata
     * @throws SQLException
     */
    public String[] getMetaDataFromDatabase(String path){

        String[] result = new String[0];
        try {
            imageDatabase.openConnection();
            result = imageDatabase.getImageMetadata(path);
            imageDatabase.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        boolean result = imageDatabase.addTags(path,tag);
        imageDatabase.closeConnection();
        return result;
    }

    /**
     * removes all the tags of the picture matching the tags in the arraylist
     * @param path
     * @param tags
     * @return
     * @throws SQLException
     */
    public boolean removeTag(String path, String[] tags) throws SQLException {
        imageDatabase.openConnection();
        boolean result = imageDatabase.removeTag(path,tags);
        imageDatabase.closeConnection();
        return result;
    }

    /**
     * searches through database and returns arraylist with the path to pictures which are found in the search
     * @param searchFor keyword or phrase that you are searching for
     * @param searchIn what column you are searching in, e.g path, or date
     * @return an arraylist with the paths that are found
     * @throws SQLException
     * @author Ingebrigt Hovind
     */
    public ArrayList<String> search(String searchFor,String searchIn) throws SQLException {
        imageDatabase.openConnection();
        ArrayList result = imageDatabase.search(searchFor,searchIn);
        imageDatabase.closeConnection();
        return result;
    }
    public ArrayList<String> sort(String sortBy, boolean ascending) throws SQLException {
        imageDatabase.openConnection();
        ArrayList<String> result = imageDatabase.sortBy(sortBy,ascending);
        imageDatabase.closeConnection();
        return result;
    }
}
