package backend.database;

import backend.util.ImageImport;
import backend.util.Log;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The DatabaseClient class represents objects that work as a middleman between the database and the user.
 * The methods in this class mainly calls methods from the {@link ImageDAOManager} class.
 *
 * @author Ingebrigt Hovind, Fredrik Julsen & Erling Sletta
 */
public class DatabaseClient {
    private static final Log logger = new Log();
    private static DatabaseClient instance;
    private static ImageDAOManager imageDatabase = null;
    private static EntityManagerFactory emf = null;
    private static Properties properties;

    /**
     * This constructor creates a DatabaseClient object, which represents a users unique instance of the program.
     * It checks the users .properties file to find the login details, which tells
     * the program who's using it, and thus which images should be loaded in.
     * The DatabaseClient has an object of the {@link ImageDAOManager} class, which acts as the image database.
     *
     * @throws IOException
     */
    private DatabaseClient() throws IOException {
        Map newProperties = new HashMap();
        //loads the local .properties file
        properties = loadProperties();
        //loads username and password to local map
        newProperties.put("javax.persistence.jdbc.user", properties.getProperty("USERNAME"));
        newProperties.put("javax.persistence.jdbc.password", properties.getProperty("PASSWORD"));
        //loads persistenceunit with local map containing username and password
        emf = javax.persistence.Persistence.createEntityManagerFactory("DatabasePU", newProperties);
        imageDatabase = new ImageDAOManager(emf);
    }

    /**
     * logs the user into the system.
     *
     * @param username the username
     * @param password the password
     * @return true if login was successful, false if not
     */
    public boolean login(String username, String password) {
        logger.logNewInfo("DatabaseClient : login");
        boolean result = imageDatabase.login(username, password);
        imageDatabase.setInitialized(true);
        return result;
    }

    /**
     * New user boolean.
     * both username and password needs to consist of only ascii characters
     *
     * @param username the username
     * @param password the password
     * @return true if registration was successful, false if not. username must not be present in our system
     */
    public boolean newUser(String username, String password) {
        boolean usernameASCII = StandardCharsets.US_ASCII.newEncoder().canEncode(username);
        boolean passwordASCII = StandardCharsets.US_ASCII.newEncoder().canEncode(password);
        if (usernameASCII && passwordASCII) {
            logger.logNewInfo("DatabaseClient : new user");
            return imageDatabase.newUser(username, password);
        } else {
            return false;
        }
    }

    /**
     * Loads the .properties file that is saved in resources folder
     *
     * @return a Properties object corresponding with the .properties file
     */
    private Properties loadProperties() {
        Properties prop = new Properties();
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream(".properties");
            // load a properties file
            prop.load(input);
            assert input != null;
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop;
    }


    /**
     * Singleton method for getting an instance of this class
     *
     * @return instance of DatabaseClient
     * @throws IOException the io exception
     */
    public static DatabaseClient getInstance() throws IOException {
        if (imageDatabase == null && emf == null) {
            instance = new DatabaseClient();
        }
        return instance;
    }

    /**
     * Gets all the data in a given column, e.g all the paths
     *
     * @param columnName name of the column to get, e.g. path, imageid, tags, date, height or width.
     * @return an ArrayList of data objects
     * @see ImageDAOManager#getColumn(String) ImageDAOManager#getColumn(String)
     */
    public ArrayList<String> getColumn(String columnName) {
        return (ArrayList<String>) imageDatabase.getColumn(columnName);
    }

    /**
     * Adds an image, more specifically its path and metadata, to the database
     *
     * @param image the File, in our case an image, that will be added to the database
     * @return true if the image was successfully added to database, false if not
     * @see ImageDAOManager#addImageToTable(String, int, int, int, int, double, double) ImageDAOManager#addImageToTable(String, int, int, int, int, double, double)
     */
    public boolean addImage(File image) {
        logger.logNewInfo("DatabaseClient : Adding image");
        String[] metadata = ImageImport.getMetaData(image);
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

    /**
     * Gets all the tags of a chosen image
     *
     * @param path path to the image
     * @return a String with all the image's tags
     * @see ImageDAOManager#getTags(String) ImageDAOManager#getTags(String)
     */
    public String getTags(String path) {
        logger.logNewInfo("Getting tags from " + path);
        return imageDatabase.getTags(path);
    }

    /**
     * Gets the metadata for a specific image
     *
     * @param path path to the image
     * @return String array with the image's metadata
     * @throws SQLException
     * @see ImageDAOManager#getImageMetadata(String) ImageDAOManager#getImageMetadata(String)
     */
    public String[] getMetaDataFromDatabase(String path) {
        logger.logNewInfo("DatabaseClient : Getting metadata from " + path);
        return imageDatabase.getImageMetadata(path);
    }

    /**
     * Adds tags to the image in the database
     *
     * @param path path to the image
     * @param tag  String[] of tags
     * @return true if tags were added successfully, false if not
     * @see ImageDAOManager#addTags(String, String[]) ImageDAOManager#addTags(String, String[])
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

    /**
     * Removes an image from the database.
     *
     * @param path the image's path
     * @return true boolean
     * @see ImageDAOManager#removeImageDAO(String) ImageDAOManager#removeImageDAO(String)
     */
    public boolean removeImage(String path) {
        imageDatabase.removeImageDAO(path);
        return true;
    }


    /**
     * Removes all tags in the parameter Array from a given image
     *
     * @param path path to the image
     * @param tags String array of tags to be removed
     * @return true if the tags are successfully removed, false if not
     * @see ImageDAOManager#removeTag(String, String[]) ImageDAOManager#removeTag(String, String[])
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
     * Searches through database and finds the paths to all images that match the search criteria
     *
     * @param searchFor keyword or phrase that you are searching for
     * @param searchIn  the column in which the search should take place, e.g. path, or date
     * @return ArrayList with the paths that are found
     * @see ImageDAOManager#search(String, String) ImageDAOManager#search(String, String)
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
     * Sorts an ArrayList by a given rule, e.g. filesize or date
     *
     * @param sortBy    column in database to sort by
     * @param ascending boolean that decides what order the images should be sorted in
     * @return an sorted ArrayList
     * @see ImageDAOManager#sortBy(String, boolean) ImageDAOManager#sortBy(String, boolean)
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

    /**
     * Creates a new album in the database
     *
     * @param name  name of the album
     * @param paths paths to images in the album
     * @see ImageDAOManager#addAlbum(String, List) ImageDAOManager#addAlbum(String, List)
     */
    public void addAlbum(String name, List<String> paths) {
        imageDatabase.addAlbum(name, paths);
    }

    /**
     * Deletes an album from the database
     *
     * @param name name of the album
     * @see ImageDAOManager#removeAlbum(String) ImageDAOManager#removeAlbum(String)
     */
    public void removeAlbum(String name) {
        imageDatabase.removeAlbum(name);
    }

    /**
     * Removes a path, and thereby it's corresponding image, from a given album
     *
     * @param name  name of the album
     * @param paths paths to the images
     * @return true if image is successfully removed, false if not
     * @see ImageDAOManager#removePathFromAlbum(String, String[]) ImageDAOManager#removePathFromAlbum(String, String[])
     */
    public boolean removeFromAlbum(String name, String[] paths) {
        return imageDatabase.removePathFromAlbum(name, paths);
    }

    /**
     * Adds a path, and thereby it's corresponding image, to a given album
     *
     * @param name  name of the album
     * @param paths path to the image
     * @return true if image is successfully added, false if not
     * @see ImageDAOManager#addPathToAlbum(String, ArrayList) ImageDAOManager#addPathToAlbum(String, ArrayList)
     */
    public boolean addPathToAlbum(String name, ArrayList<String> paths) {
        return imageDatabase.addPathToAlbum(name, paths);
    }

    /**
     * Gets all the albums from the database
     *
     * @return a Map with all existing Albums
     * @see ImageDAOManager#getAllAlbums() ImageDAOManager#getAllAlbums()
     */
    public Map getAllAlbums() {
        return (Map) imageDatabase.getAllAlbums().stream()
                //collects the stream into the hashmap, calling the key becoming each objects album name
                .collect(Collectors.toMap(AlbumDAO::getAlbumName,AlbumDAO::getImagePaths));
    }
}
