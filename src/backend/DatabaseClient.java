package backend;

import java.io.File;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ingebrigt Hovind & Fredrik Julsen
 */
//TODO add javadoc
public class DatabaseClient {
    private static final Log logger = new Log("Log.log");
    private Database imageDatabase = new Database();
    private ImageImport imageImport = new ImageImport();

    /**
     * @throws SQLException
     */
    public void closeApplication() throws SQLException {
        logger.logNewInfo("Clsoing application");
        if (imageDatabase.isConnection()) {
            imageDatabase.openConnection();
        }
        imageDatabase.closeDatabase();
    }


    /**
     * Gets all the data in a given column, e.g all the paths
     *
     * @param columnName eks: Path,ImageID,Tags,File_size,DATE,Height,Width.
     * @return An arraylist of data objects
     * @throws SQLException could not find input from columnName
     */
    public ArrayList getColumn(String columnName) throws SQLException {
        imageDatabase.openConnection();
        ArrayList arrayList = imageDatabase.getColumn(columnName);
        imageDatabase.close();
        return arrayList;
    }

    /**
     * Adds a image to database
     *
     * @param image imagefile to add
     * @return if the image was added to database
     */
    public boolean addImage(File image) throws SQLException {
        try {
            logger.logNewInfo("Adding image");
            String[] metadata = imageImport.getMetaData(image);
            if (metadata != null) {
                try {
                    if (getColumn("Path").contains(image.getPath())) {
                        return false;
                    }
                    else {
                        imageDatabase.openConnection();
                        System.out.println("adding image");
                        imageDatabase.addImageToTable(
                                image.getPath(),
                                "",
                                Integer.parseInt(metadata[0]),
                                Long.parseLong(metadata[1]),
                                Integer.parseInt(metadata[2]),
                                Integer.parseInt(metadata[3]),
                                Double.parseDouble(metadata[4]),
                                Double.parseDouble(metadata[5]));

                        //System.out.println("tru ");
                        imageDatabase.close();
                        return true;
                    }
                    /*else{
                        System.out.println("false");
                        imageDatabase.close();
                        return false;
                    }*/
                } catch (SQLIntegrityConstraintViolationException e) {
                    logger.logNewFatalError(e.getLocalizedMessage());
                    imageDatabase.close();
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.logNewFatalError(e.getLocalizedMessage());
            return false;
        }
        return false;
    }


    /**
     * Get metadata for one specific image
     *
     * @param path path to image
     * @return String[] of metadata
     * @throws SQLException
     */
    public String[] getMetaDataFromDatabase(String path) {
        logger.logNewInfo("Getting metadata from " + path);

        String[] result = new String[0];
        try {
            imageDatabase.openConnection();
            result = imageDatabase.getImageMetadata(path);
            imageDatabase.close();
        } catch (SQLException e) {
            logger.logNewFatalError(e.getLocalizedMessage());
        }
        return result;
    }

    /**
     * Legger til tags i databasen
     *
     * @param path path til bilde
     * @param tag  String[] av tags
     * @return boolean
     * @throws SQLException
     */
    public boolean addTag(String path, String[] tag) throws SQLException {
        logger.logNewInfo("Adding tag to " + path);
        imageDatabase.openConnection();
        boolean result = imageDatabase.addTags(path, tag);
        imageDatabase.close();
        return result;
    }

    /**
     * removes all the tags of the picture matching the tags in the arraylist
     *
     * @param path
     * @param tags
     * @return
     * @throws SQLException
     */
    public boolean removeTag(String path, String[] tags) throws SQLException {
        logger.logNewInfo("Removing tag from " + path);
        imageDatabase.openConnection();
        boolean result = imageDatabase.removeTag(path, tags);
        imageDatabase.close();
        return result;
    }

    /**
     * searches through database and returns arraylist with the path to pictures which are found in the search
     *
     * @param searchFor keyword or phrase that you are searching for
     * @param searchIn  what column you are searching in, e.g path, or date
     * @return an arraylist with the paths that are found
     * @throws SQLException
     * @author Ingebrigt Hovind
     */
    public ArrayList<String> search(String searchFor, String searchIn) throws SQLException {
        logger.logNewInfo("Searching for" + searchFor);
        imageDatabase.openConnection();
        ArrayList result = imageDatabase.search(searchFor, searchIn);
        imageDatabase.close();
        return result;
    }

    public ArrayList<String> sort(String sortBy, boolean ascending) throws SQLException {
        logger.logNewInfo("Sorting by " + sortBy);
        imageDatabase.openConnection();
        ArrayList<String> result = imageDatabase.sortBy(sortBy, ascending);
        imageDatabase.close();
        return result;
    }
}
