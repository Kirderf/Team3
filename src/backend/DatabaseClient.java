package backend;

import java.io.File;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

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
        logger.logNewInfo("DatabaseClient : " + "Closing application");
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
            logger.logNewInfo("DatabaseClient : " + "Adding image");
            imageDatabase.openConnection();
            String[] metadata = imageImport.getMetaData(image);
            if (metadata != null) {
                try {
                    if (getColumn("Path").contains(image.getPath().replaceAll("\\\\", "/"))) {
                        return false;
                    } else {
                        imageDatabase.openConnection();
                        imageDatabase.addImageToTable(
                                image.getPath(),
                                "",
                                Integer.parseInt(metadata[0]),
                                Long.parseLong(metadata[1]),
                                Integer.parseInt(metadata[2]),
                                Integer.parseInt(metadata[3]),
                                Double.parseDouble(metadata[4]),
                                Double.parseDouble(metadata[5]));
                        imageDatabase.close();
                        return true;
                    }
                } catch (SQLIntegrityConstraintViolationException e) {
                    logger.logNewFatalError("DatabaseClient : " + e.getLocalizedMessage());
                    imageDatabase.close();
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.logNewFatalError("DatabaseClient : " + e.getLocalizedMessage());
            return false;
        }
        return false;
    }

    public String getTags(String path) {
        logger.logNewInfo("Getting tags from " + path);

        try {
            imageDatabase.openConnection();
            //returns null if the image is not in the database
            String result = imageDatabase.getTags(path).toString().substring(1);
            imageDatabase.close();
            return result;
        } catch (SQLException e) {
            logger.logNewFatalError(e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Get metadata for one specific image
     *
     * @param path path to image
     * @return String[] of metadata
     * @throws SQLException
     */
    public String[] getMetaDataFromDatabase(String path) {
        logger.logNewInfo("DatabaseClient : " + "Getting metadata from " + path);

        String[] result = new String[0];
        try {
            imageDatabase.openConnection();
            //returns null if the image is not in the database
            result = imageDatabase.getImageMetadata(path);
            imageDatabase.close();
        } catch (SQLException e) {
            logger.logNewFatalError("DatabaseClient : " + e.getLocalizedMessage());
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
        logger.logNewInfo("DatabaseClient : " + "Adding tag to " + path);
        imageDatabase.openConnection();
        try {
            boolean result = imageDatabase.addTags(path, tag);
            imageDatabase.close();
            return result;
        } catch (IllegalArgumentException e) {
            logger.logNewFatalError(e.getLocalizedMessage());
            return false;
        } catch (Exception e) {
            logger.logNewFatalError(e.getLocalizedMessage());
            return false;
        }
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
        logger.logNewInfo("DatabaseClient : " + "Removing tag from " + path);
        try {
            imageDatabase.openConnection();
            boolean result = imageDatabase.removeTag(path, tags);
            imageDatabase.close();
            return result;
        } catch (Exception e) {
            logger.logNewFatalError(e.getLocalizedMessage());
            return false;
        }
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
        logger.logNewInfo("DatabaseClient : " + "Searching for" + searchFor);
        try {
            imageDatabase.openConnection();
            ArrayList result = imageDatabase.search(searchFor, searchIn);
            imageDatabase.close();
            return result;
        } catch (Exception e) {
            logger.logNewFatalError(e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Returns an sorted arraylist sorted by the column in sortby
     *
     * @param sortBy    column in database to sort by
     * @param ascending
     * @return sorted arraylist
     * @throws SQLException
     */
    public ArrayList<String> sort(String sortBy, boolean ascending) throws SQLException {
        logger.logNewInfo("DatabaseClient : " + "Sorting by " + sortBy);
        try {
            imageDatabase.openConnection();
            ArrayList<String> result = imageDatabase.sortBy(sortBy, ascending);
            imageDatabase.close();
            return result;
        } catch (Exception e) {
            logger.logNewFatalError(e.getLocalizedMessage());
            imageDatabase.close();
            return null;
        }
    }
}
