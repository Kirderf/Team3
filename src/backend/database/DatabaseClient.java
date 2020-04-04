package backend.database;

import backend.util.ImageImport;
import backend.util.Log;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Fredrik Julsen & Ingebrigt Hovind
 */
public class DatabaseClient {
    private static final Log logger = new Log();
    private static DatabaseClient instance;
    private static ImageDAOManager imageDatabase = null;
    private static EntityManagerFactory emf = null;
    private ImageImport imageImport = new ImageImport();


    private DatabaseClient() {
        emf = Persistence.createEntityManagerFactory("DatabasePU");
        imageDatabase = new ImageDAOManager(emf);

    }

    /**
     * Singleton method for getting an instance of this class
     *
     * @return instance of DatabaseClient
     */
    public static DatabaseClient getInstance() {
        if (imageDatabase == null && emf == null) {
            instance = new DatabaseClient();
        }
        return instance;
    }

    /**
     * Gets all the data in a given column, e.g all the paths
     *
     * @param columnName eks: Path,ImageID,Tags,File_size,DATE,Height,Width.
     * @return An arraylist of data objects
     */
    public ArrayList<String> getColumn(String columnName) {
        return (ArrayList<String>) imageDatabase.getColumn(columnName);
    }

    /**
     * Adds a image to database
     *
     * @param image imagefile to add
     * @return if the image was added to database
     */
    public boolean addImage(File image) {
        logger.logNewInfo("DatabaseClient : Adding image");
        String[] metadata = imageImport.getMetaData(image);
        if (metadata != null) {
            if (getColumn("Path").contains(image.getPath().replaceAll("\\\\", "/"))) {
                return false;
            } else {
                imageDatabase.addImageToTable(
                        image.getPath(),
                        Integer.parseInt(metadata[0]),
                        Integer.parseInt(metadata[1]),
                        Integer.parseInt(metadata[2]),
                        Integer.parseInt(metadata[3]),
                        Double.parseDouble(metadata[4]),
                        Double.parseDouble(metadata[5]));
                if (!imageDatabase.isInitialized()) {
                    imageDatabase.setInitialized(true);
                }
                return true;
            }
        }
        return false;
    }

    public String getTags(String path) {
        logger.logNewInfo("Getting tags from " + path);
        return imageDatabase.getTags(path);
    }

    /**
     * Get metadata for one specific image
     *
     * @param path path to image
     * @return String[] of metadata
     * @throws SQLException
     */
    public String[] getMetaDataFromDatabase(String path) {
        logger.logNewInfo("DatabaseClient : Getting metadata from " + path);
        return imageDatabase.getImageMetadata(path);
    }

    /**
     * Legger til tags i databasen
     *
     * @param path path til bilde
     * @param tag  String[] av tags
     * @return boolean
     */
    public boolean addTag(String path, String[] tag) {
        logger.logNewInfo("DatabaseClient : Adding tag to " + path);
        try {
            return imageDatabase.addTags(path, tag);
        } catch (Exception e) {
            logger.logNewFatalError(e.getLocalizedMessage());
            return false;
        }
    }
    public boolean removeImage(String path) {
        imageDatabase.deleteTeam3Image(path);
        return true;
    }


    /**
     * removes all the tags of the picture matching the tags in the arraylist
     *
     * @param path
     * @param tags
     * @return
     */
    public boolean removeTag(String path, String[] tags) {
        logger.logNewInfo("DatabaseClient : Removing tag from " + path);
        try {
            return imageDatabase.removeTag(path, tags);
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
     * @author Ingebrigt Hovind
     */
    public ArrayList<String> search(String searchFor, String searchIn) {
        logger.logNewInfo("DatabaseClient : " + "Searching for" + searchFor);
        try {
            return imageDatabase.search(searchFor, searchIn);
        } catch (Exception e) {
            logger.logNewFatalError(e.getLocalizedMessage());
            return (ArrayList) Collections.emptyList();
        }
    }

    /**
     * Returns an sorted arraylist sorted by the column in sortby
     *
     * @param sortBy    column in database to sort by
     * @param ascending
     * @return sorted arraylist
     */
    public ArrayList<String> sort(String sortBy, boolean ascending) {
        logger.logNewInfo("DatabaseClient : " + "Sorting by " + sortBy);
        try {
            return imageDatabase.sortBy(sortBy, ascending);
        } catch (Exception e) {
            logger.logNewFatalError(e.getLocalizedMessage());
            return null;
        }
    }
}
