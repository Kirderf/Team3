package backend.database;

import backend.util.ImageImport;
import backend.util.Log;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.apache.commons.io.FilenameUtils;

import javax.activation.DataSource;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.crypto.Data;
import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Database client.
 *
 * @author Fredrik Julsen & Ingebrigt Hovind
 */
public class DatabaseClient {
    private static final Log logger = new Log();
    private static DatabaseClient instance;
    private static ImageDAOManager imageDatabase = null;
    private static EntityManagerFactory emf = null;
    private static Properties properties;

    private DatabaseClient() throws IOException {
        Map newProperties = new HashMap();
        //loads the local .properties file
        this.properties = loadProperties();
        //loads username and password to local map
        newProperties.put("javax.persistence.jdbc.user",properties.getProperty("USERNAME"));
        newProperties.put("javax.persistence.jdbc.password", properties.getProperty("PASSWORD"));
        //loads persistenceunit with local map containing username and password
        emf = javax.persistence.Persistence.createEntityManagerFactory("DatabasePU", newProperties);
        imageDatabase = new ImageDAOManager(emf);
        //sets the tenant id
        imageDatabase.setInstanceID(getTenantID());
        imageDatabase.isAccountPresent();
    }

    /**
     * loads the .properties file that is saved in resources folder
     * @return a Properties object corresponding with the .properties file
     */
    private Properties loadProperties() {
        Properties prop = new Properties();
            try  {
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
     * gets the tenant id if it is saved in the database, generates a new one if not
     * @return the int value of this new tenant id
     * @throws IOException reads from the tenant file
     */
    private static int getTenantID() throws IOException {
        //i struggled to find the .properties file using normal methods, so this is the implementation that i got working
        String test1 = (new File("").getAbsolutePath());
        String pathToProperties = (FilenameUtils.normalize(test1 + "\\resources\\.properties"));

        // load a properties file
        // get the property value and print it out
        if (properties.getProperty("TENANT_ID") == null) {
            logger.logNewInfo("generating new tenantID");
            Properties table = new Properties();
            //generates a random int to be used for tenant id
            Random rand = new Random();
            //TODO should this be different? find something that doesn't tie us to only 10000 tenants
            int randInt = rand.nextInt(10000);
            //if the tenant id exists, then a new one is generated
            while (imageDatabase.getAllUserID().contains(randInt)) {
                randInt = rand.nextInt(10000);
            }
            //iterates through .properties file in order to save the values that are there already
            Enumeration<String> enums = (Enumeration<String>) properties.propertyNames();
            while (enums.hasMoreElements()) {
                String key = enums.nextElement();
                String value = properties.getProperty(key);
                //adds the already existing keys and values to the new table that is being saved
                table.setProperty(key, value);
            }
            //adds tenant id value
            table.setProperty("TENANT_ID", String.valueOf(rand.nextInt(10000)));
            //writes to .properties, overwriting the old file
            FileOutputStream fr = new FileOutputStream(pathToProperties);
            //uses the outputstream to write
            table.store(fr,"tenant-id generated automatically");
            fr.close();
            //returns the new tenant id, get nullpointer if properties is used instead of table here
            return Integer.parseInt(table.getProperty("TENANT_ID"));
        } else if (!imageDatabase.getAllUserID().contains(Integer.parseInt(properties.getProperty("TENANT_ID")))) {
            //returns the existing table
            return Integer.parseInt(properties.getProperty("TENANT_ID"));
        }
        else{
            //if the key matches one that is already in the table
            //should maybe check if the images belonging to that tenant id can be loaded on this maching
            return Integer.parseInt(properties.getProperty("TENANT_ID"));
        }
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
     * Gets tags.
     *
     * @param path the path
     * @return the tags
     */
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
     * @return boolean boolean
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
     * Remove image boolean.
     *
     * @param path the path
     * @return the boolean
     */
    public boolean removeImage(String path) {
        imageDatabase.removeImageDAO(path);
        return true;
    }


    /**
     * removes all the tags of the picture matching the tags in the arraylist
     *
     * @param path the path
     * @param tags the tags
     * @return boolean
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
     * @param ascending the ascending
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


    /**
     * Add album.
     *
     * @param name  the name to the new album
     * @param paths the paths this album starts with
     */
    public void addAlbum(String name, List<String> paths){
        imageDatabase.addAlbum(name, paths);
    }

    /**
     * Remove album.
     *
     * @param name the name of the album
     */
    public void removeAlbum(String name){
        imageDatabase.removeAlbum(name);
    }

    /**
     * Remove from album boolean.
     *
     * @param name  the name of album you want to remove from
     * @param paths the paths you want to remove
     * @return the boolean
     */
    public boolean removeFromAlbum(String name, String[] paths){
        return imageDatabase.removePathFromAlbum(name,paths);
    }

    /**
     * Add path to album boolean.
     *
     * @param name  the name
     * @param paths the paths
     * @return the boolean
     */
    public boolean addPathToAlbum(String name, ArrayList<String> paths){
        return imageDatabase.addPathToAlbum(name,paths);
    }

    /**
     * Get all albums map.
     *
     * @return the map
     */
    public Map getAllAlbums(){
        return (Map) imageDatabase.getAllAlbums().stream()
                //collects the stream into the hashmap, calling the key becoming each objects album name
                .collect(Collectors.toMap(AlbumDAO::getAlbumName,
                        //gets the images belonging belonging to the album
                        s->s.getImages().stream()
                                //calls getpath on each of these
                                .map(a->((ImageDAO)a).getPath())
                                //collects the results into a list which is the value in the hashmap
                                .collect(Collectors.toList())));
    }
}
