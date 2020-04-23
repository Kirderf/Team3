package backend.database;

import javafx.scene.image.Image;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.io.File;
import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class manages images, and directly interacts with the SQL database.
 */
//use compostion, as the only Image objects we would want to manipulate would be the ones already in the database, therefore it should be done through this class
public class DAOManager {
    private boolean isInitialized = false;
    private UserDAO userDAO;
    private EntityManagerFactory emf;

    /**
     * Instantiates a new ImageDAO Manager.
     *
     * @param emf the emf
     */
    DAOManager(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Registers a new user
     *
     * @param username the username, cannot already be in the database, not case-sensitive
     * @param password the password being registered
     * @return true if registration was successful, false if username is taken, or if a non-ascii character has been entered
     */
    boolean newUser(String username, String password) {
        EntityManager em = getEM();
        try {
            ArrayList<String> usernames = (ArrayList<String>) getAllUsers().stream().map(UserDAO::getUsername).collect(Collectors.toList());
            if (usernames.contains(username)) return false;

            UserDAO newUser = new UserDAO(username.toLowerCase(), password);
            em.getTransaction().begin();
            em.persist(newUser);
            em.getTransaction().commit();
            this.userDAO = newUser;
            return true;
        } finally {
            closeEM(em);
        }
    }

    /**
     * Attempts to login with the given username or password
     *
     * @param username username to the user you want to log in as, not case-sensetive
     * @param password password to this user, case-sensetive
     * @return false if username was not found or password is incorrect, true is password is correct for the given user
     */
    boolean login(String username, String password) {
        EntityManager em = getEM();
        try {
            List<UserDAO> users = getAllUsers();
            for (UserDAO u : users) {
                //if username and password are equal
                if (u.getUsername().equalsIgnoreCase(username) && (u.verifyPassword(password))) {
                    this.userDAO = u;
                    return true;
                }
            }
        } finally {
            closeEM(em);
        }
        return false;
    }

    private List<UserDAO> getAllUsers() {
        EntityManager em = getEM();
        try {
            Query q = em.createQuery("SELECT OBJECT(o) FROM UserDAO o");
            return q.getResultList();
        } finally {
            closeEM(em);
        }
    }

    /**
     * Is initialized boolean.
     *
     * @return true if initialized, false if not
     */
    boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Sets initialized.
     *
     * @param initialized boolean
     */
    void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    /**
     * Create new images and adds them to the database.
     * Persist works as SQL INSERT.
     * The image path needs to be unique for a given user.
     *
     * @param path         the path
     * @param fileSize     the file size
     * @param date         the date
     * @param imageHeight  the image height
     * @param imageWidth   the image width
     * @param gpsLatitude  the gps latitude
     * @param gpsLongitude the gps longitude
     */
    void addImageToTable(String path, int fileSize, int date, int imageHeight, int imageWidth, double gpsLatitude, double gpsLongitude) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            ImageDAO imageDAO = new ImageDAO(userDAO, path, fileSize, date, imageHeight, imageWidth, gpsLatitude, gpsLongitude);
            em.persist(imageDAO);
            em.getTransaction().commit();//store into database
        } finally {
            closeEM(em);
        }
    }

    void removePathsFromAlbum(String name, List<String> paths) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            AlbumDAO a = findAlbumDAO(name);
            if (!em.contains(a)) {
                a = em.merge(a);
            }
            for(String s : paths) {
                a.removeImage(s);
            }
            em.getTransaction().commit();
        } finally {
            closeEM(em);
        }

    }


    /**
     * Adds an album to the database.
     *
     * @param name  the name of the album
     * @param paths a List with the paths to images in album
     */
    void addAlbum(String name, List<String> paths) {
        EntityManager em = getEM();
        try {
            //throw exception because this should not happen under normal cirumstances
            if (paths.isEmpty()) throw new IllegalArgumentException("You cannot create an empty album");
            for (AlbumDAO a : getAllAlbums()) {
                if (a != null && a.getAlbumName().equalsIgnoreCase(name)) {
                    //throw exception here because it should not be possible to enter a name that already exists due to previous checks
                    throw new IllegalArgumentException("That album already exists");
                }
            }
            em.getTransaction().begin();
            ArrayList<ImageDAO> images = (ArrayList<ImageDAO>) paths.stream().map(this::findImageDAO).collect(Collectors.toList());
            AlbumDAO newAlbum = new AlbumDAO(name, images, userDAO);
            em.persist(newAlbum);
            em.getTransaction().commit();
        } finally {
            closeEM(em);
        }
    }

    /**
     * Finds an album
     *
     * @param name name of album you want to find
     * @return corresponding album object
     */
    private AlbumDAO findAlbumDAO(String name) {
        EntityManager em = getEM();
        try {
            List<AlbumDAO> albums = getAllAlbums();
            List<AlbumDAO> albumList = albums.stream().filter(s -> s.getAlbumName().equalsIgnoreCase(name) && s.getUserID() == this.userDAO.getAccountID()).collect(Collectors.toList());
            if (albumList.isEmpty()) {
                return new AlbumDAO("", new ArrayList<>(), this.userDAO);
            }
            return albumList.get(0);
        } finally {
            closeEM(em);
        }
    }

    /**
     * Gets all existing Albums
     *
     * @return Map of albums with String keys (album name) and List values (image paths)
     */
    Map<String, List<String>> getAlbumMap() {
        return getAllAlbums().stream().collect(Collectors.toMap(AlbumDAO::getAlbumName, AlbumDAO::getImagePaths));
    }

    private List<AlbumDAO> getAllAlbums() {
        EntityManager em = getEM();
        try {
            if (isInitialized) {
                Query q = em.createQuery("SELECT OBJECT(o) FROM AlbumDAO o WHERE o.userDAO.accountID=" + this.userDAO.getAccountID(), AlbumDAO.class);
                return q.getResultList();
            }
            return Collections.emptyList();
        } finally {
            closeEM(em);
        }

    }

    /**
     * Removes an album.
     *
     * @param name the name of the album
     */
    void removeAlbum(String name) {
        EntityManager em = getEM();
        try {
            AlbumDAO a = findAlbumDAO(name);
            em.getTransaction().begin();
            if (!em.contains(a)) {
                a = em.merge(a);
            }
            em.remove(a);
            em.getTransaction().commit();
        } finally {
            closeEM(em);
        }
    }

    /**
     * Adds one or more image(s) to an album.
     *
     * @param name  the name of the album
     * @param paths the path(s) to the image(s)
     */
    void addPathToAlbum(String name, List<String> paths) {
        EntityManager em = getEM();
        try {
            AlbumDAO albumDAO = findAlbumDAO(name);
            for (String s : paths) {
                if (!albumDAO.getImagePaths().contains(s)) {
                    albumDAO.addImage(findImageDAO(s));
                }
            }
            //This might not do anything, if em.find returns a shallow copy, then we do not need this
            em.getTransaction().begin();
            em.merge(albumDAO);
            em.getTransaction().commit();
            //returns true if any tag was added, false if not
        } finally {
            closeEM(em);
        }
    }

    /**
     * Finds an image by a given path
     *
     * @param path the path of the image you want to find
     * @return the ImageDAO if an image is found, null if not
     */
    private ImageDAO findImageDAO(String path) {
        EntityManager em = getEM();
        try {
            List<ImageDAO> images = getAllImageDAO();
            List<ImageDAO> imageDAOList = images.stream().filter(s -> s.getPath().equalsIgnoreCase(path) && s.getUserDAO().getAccountID() == this.userDAO.getAccountID()).collect(Collectors.toList());
            if (imageDAOList.isEmpty()) {
                return new ImageDAO(this.userDAO, "", -1, -1, -1, -1, -1, -1);
            }
            return imageDAOList.get(0);
        } finally {
            closeEM(em);
        }
    }

    /**
     * Removes an image from the database.
     *
     * @param path the path of the image
     */
    void removeImageDAO(String path) {
        EntityManager em = getEM();
        try {
            ImageDAO t = findImageDAO(path);
            if (t != null) {
                em.getTransaction().begin();
                if (!em.contains(t)) {
                    t = em.merge(t);
                }
                em.remove(t);
                em.getTransaction().commit();
            }
        } finally {
            closeEM(em);
        }
    }

    /**
     * Gets all images.
     *
     * @return the all image dao
     */
    private List<ImageDAO> getAllImageDAO() {
        EntityManager em = getEM();
        try {
            if (isInitialized) {
                Query q = em.createQuery("SELECT OBJECT(o) FROM ImageDAO o WHERE o.userDAO.accountID =" + this.userDAO.getAccountID());
                return q.getResultList();
            }
            return Collections.emptyList();
        } finally {
            closeEM(em);
        }
    }

    /**
     * Gets the thumbnail of a given image
     *
     * @param path path to the image
     * @return an imageview with the resized image
     */
    Image getThumbnail(String path) throws MalformedURLException {
        for (ImageDAO o : getAllImageDAO()) {
            //if the path is found
            if (o.getPath().equals(path)) {
                return o.getThumbnail();
            }
        }
        return null;
    }

    /**
     * Gets an image's tags.
     *
     * @param path path to the image
     * @return the image's tags
     */
    String getTags(String path) {
        EntityManager em = getEM();
        try {
            return findImageDAO(path).getTags();
        } finally {
            closeEM(em);
        }
    }

    /**
     * Adds tags to a given image.
     *
     * @param path path to the image
     * @param tags tags to be added
     * @return true if tags were added, false if not
     */
    boolean addTags(String path, String[] tags) {
        EntityManager em = getEM();
        int counter = 0;
        try {
            for (String s : tags) {
                if (s.contains(",")) {
                    throw new IllegalArgumentException("Cannot add a tag containing a comma");
                }
            }
            ImageDAO imageDAO = findImageDAO(path);
            //convert to lowercase
            List<String> tagList = Arrays.stream(imageDAO.getTags().split(",")).map(String::toLowerCase).collect(Collectors.toList());
            for (String s : tags) {
                if (!tagList.contains(s.toLowerCase())) {
                    //The first letter is a capital letter, rest is lower case
                    imageDAO.addTag(s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase());
                } else {
                    //counts number of tags already present
                    counter++;
                }
            }
            //This might not do anything, if em.find returns a shallow copy, then we do not need this
            em.getTransaction().begin();
            em.merge(imageDAO);
            em.getTransaction().commit();
            //returns true if any tag was added, false if not
            return counter != tags.length;
        } finally {
            closeEM(em);
        }
    }

    /**
     * Removes tags from a given image
     *
     * @param path path to the image
     * @param tags tags to be removed
     * @return true if tags were removed, false if not
     */
    boolean removeTag(String path, String[] tags) {
        EntityManager em = getEM();
        try {
            ImageDAO imageDAO = findImageDAO(path);
            List<String> tagList = Arrays.stream(imageDAO.getTags().split(",")).collect(Collectors.toList());
            Arrays.stream(tags).forEach(tagList::remove);
            //sets the list to the one with the removed tags
            if (!em.contains(imageDAO)) {
                imageDAO = em.merge(imageDAO);
            }
            imageDAO.setTags(String.join(",", tagList));
            //This might not do anything, if em.find returns a shallow copy, then we do not need this
            em.getTransaction().begin();
            em.merge(imageDAO);
            em.getTransaction().commit();
            //returns true if any tag was removed, false if not
            return tagList.size() == tags.length;
        } finally {
            closeEM(em);
        }
    }

    /**
     * Sorts the images by a given column.
     *
     * @param columnName the column name to sort by, i.e.: path, file_size, date and filename
     * @return sorted ArrayList of images
     */
    ArrayList<String> sortBy(String columnName) {
        List<ImageDAO> images = getAllImageDAO();
        columnName = columnName.toLowerCase();
        switch (columnName) {
            //checks what column you are looking for, creates a new arraylist containing only that using lambda
            case "path":
                images.sort((o1, o2) -> o1.getPath().compareToIgnoreCase(o2.getPath()));
                break;
            case "size":
                images.sort(Comparator.comparing(ImageDAO::getFileSize));
                break;
            case "date":
                images.sort(Comparator.comparing(ImageDAO::getDate));
                break;
            case "filename":
                images.sort((o1, o2) -> o1.getPath().substring(o1.getPath().lastIndexOf(File.separator)).compareToIgnoreCase(o2.getPath().substring(o2.getPath().lastIndexOf(File.separator))));
                break;
            default:
                throw new IllegalArgumentException("Invalid Column");
        }
        ArrayList<String> stringPath = new ArrayList<>();
        for (ImageDAO t : images) {
            stringPath.add(t.getPath());
        }
        return stringPath;

    }

    /**
     * Searches for images that by a given term in a given column.
     *
     * @param searchFor term to search for
     * @param searchIn  column to search in
     * @return the array list with results of path that match the criteria
     */
    public List<String> search(String searchFor, String searchIn) {
        ArrayList<String> validColumns = new ArrayList<>();
        validColumns.add("path");
        validColumns.add("tags");
        validColumns.add("metadata");
        if (!validColumns.contains(searchIn.toLowerCase()) || searchFor == null) return new ArrayList<>();
        List<ImageDAO> images = getAllImageDAO();
        ArrayList<String> pathResults = new ArrayList<>();

        if (searchIn.equalsIgnoreCase("path")) {
            pathResults.addAll(images.stream()
                    .filter(s -> String.valueOf(s.getPath()).contains(searchFor))
                    .map(ImageDAO::getPath)
                    .collect(Collectors.toList()));
        }
        if (searchIn.equalsIgnoreCase("metadata")) {
            //creates a stream, filters it based on whether the attribute contains the search term and whether pathresult already contains the image, then it maps the paths, turn these into a list and adds this list to the pathresult list
            pathResults.addAll(images.stream()
                    .filter(s -> String.valueOf(s.getFileSize()).contains(searchFor))
                    .map(ImageDAO::getPath)
                    .collect(Collectors.toList()));
            pathResults.addAll(images.stream()
                    .filter(s -> String.valueOf(s.getDate()).contains(searchFor) && !pathResults.contains(s.getPath()))
                    .map(ImageDAO::getPath)
                    .collect(Collectors.toList()));
            pathResults.addAll(images.stream()
                    .filter(s -> String.valueOf(s.getImageHeight()).contains(searchFor) && !pathResults.contains(s.getPath()))
                    .map(ImageDAO::getPath)
                    .collect(Collectors.toList()));
            pathResults.addAll(images.stream()
                    .filter(s -> String.valueOf(s.getImageWidth()).contains(searchFor) && !pathResults.contains(s.getPath()))
                    .map(ImageDAO::getPath)
                    .collect(Collectors.toList()));
            pathResults.addAll(images.stream()
                    .filter(s -> String.valueOf(s.getLatitude()).contains(searchFor) && !pathResults.contains(s.getPath()))
                    .map(ImageDAO::getPath)
                    .collect(Collectors.toList()));
            pathResults.addAll(images.stream()
                    .filter(s -> String.valueOf(s.getLongitude()).contains(searchFor) && !pathResults.contains(s.getPath()))
                    .map(ImageDAO::getPath)
                    .collect(Collectors.toList()));
        }
        if (searchIn.equalsIgnoreCase("tags")) {
            //checks whether a list of the tags in the each picture contains the search term and whether pathResults already contains the path for s. It then takes the path for all the valid images, adds them to a list, and then adds the entire list to pathresults
            pathResults.addAll(images.stream()
                    .filter(s ->
                            Arrays.asList(s.getTags().split(","))
                                    .contains(searchFor.toUpperCase().substring(0, 1) + searchFor.substring(1).toLowerCase()) && !pathResults.contains(s.getPath()))
                    .map(ImageDAO::getPath).collect(Collectors.toList()));
        }
        return pathResults;
    }

    /**
     * Gets all the information in a column.
     *
     * @param columnName the column name
     * @return the column's information in List format
     */
    List<?> getColumn(String columnName) {
        //TODO: edit return to not a generic Wildcard
        EntityManager em = getEM();
        try {
            columnName = columnName.toLowerCase();
            List<ImageDAO> imageList = getAllImageDAO();
            switch (columnName) {
                //checks what column you are looking for, creates a new arraylist
                case "path":
                    return imageList.stream().map(ImageDAO::getPath).collect(Collectors.toList());
                case "tags":
                    return imageList.stream().map(ImageDAO::getTags).filter(s -> !s.equals("")).collect(Collectors.toList());
                case "file_size":
                    return imageList.stream().map(ImageDAO::getFileSize).collect(Collectors.toList());
                case "date":
                    return imageList.stream().map(ImageDAO::getDate).collect(Collectors.toList());
                case "height":
                    return imageList.stream().map(ImageDAO::getImageHeight).collect(Collectors.toList());
                case "width":
                    return imageList.stream().map(ImageDAO::getImageWidth).collect(Collectors.toList());
                case "gps_latitude":
                    return imageList.stream().map(ImageDAO::getLatitude).collect(Collectors.toList());
                case "gps_longitude":
                    return imageList.stream().map(ImageDAO::getLongitude).collect(Collectors.toList());
                default:
                    throw new IllegalArgumentException("Invalid Column");
            }
        } finally {
            closeEM(em);
        }
    }

    /**
     * Gets a given image's metadata in a String[].
     *
     * @param path path to the image
     * @return the metadata as a String[]
     */
    String[] getImageMetadata(String path) {
        ImageDAO imageDAO = findImageDAO(path);
        return new String[]{imageDAO.getPath(), imageDAO.getTags(), String.valueOf(imageDAO.getFileSize()), String.valueOf(imageDAO.getDate()), String.valueOf(imageDAO.getImageHeight()), String.valueOf(imageDAO.getImageWidth()), String.valueOf(imageDAO.getLatitude()), String.valueOf(imageDAO.getLongitude())};
    }


    private EntityManager getEM() {
        return emf.createEntityManager();
    }

    private void closeEM(EntityManager em) {
        if (em != null && em.isOpen()) {
            em.close();
        }

    }
}

