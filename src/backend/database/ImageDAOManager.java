package backend.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Image dao manager.
 */
//use compostion, as the only Image objects we would want to manipulate would be the ones already in the database, therefore it should be done through this class
public class ImageDAOManager {
    private boolean isInitialized = false;
    private long instanceID;
    private UserDAO userDAO;
    private EntityManagerFactory emf;

    /**
     * Instantiates a new Image dao manager.
     *
     * @param emf the emf
     */
    ImageDAOManager(EntityManagerFactory emf) {
        this.emf = emf;
    }

    boolean newUser(String username, String password){
        EntityManager em = getEM();
        try {
            ArrayList<String> usernames = (ArrayList<String>) getAllUsers().stream().map(UserDAO::getUsername).collect(Collectors.toList());
             if(usernames.contains(username)) return false;

            UserDAO newUser = new UserDAO(username,password);
            em.getTransaction().begin();
            em.persist(newUser);
            em.getTransaction().commit();
            this.userDAO = newUser;
            this.instanceID = newUser.getAccountID();
            return true;
        } finally {
            closeEM(em);
        }
    }
    boolean login(String username, String password){
        EntityManager em = getEM();
        try {
            List<UserDAO> users = getAllUsers();
            for(Object u : users){
                //if username and password are equal
                if(((UserDAO) u).getUsername().equals(username)&&(((UserDAO) u).verifyPassword(password))){
                    this.instanceID = ((UserDAO) u).getAccountID();
                    this.userDAO = ((UserDAO) u);
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
     * @return the boolean
     */
    boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Sets initialized.
     *
     * @param initialized the initialized
     */
    void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    /**
     * create new images
     * persist works as SQL INSERT
     * The image path needs to be unique for this user
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
            //paths are stored with forward slashes instead of backslashes, this helps functionality later in the program
            path = path.replaceAll("\\\\", "/");
            em.getTransaction().begin();
            ImageDAO imageDAO = new ImageDAO(userDAO, path, fileSize, date, imageHeight, imageWidth, gpsLatitude, gpsLongitude);
            em.persist(imageDAO);
            em.getTransaction().commit();//store into database
        } finally {
            closeEM(em);
        }
    }

    private UserDAO findUser(long id) {
        List<UserDAO> users = getAllUsers();
        return users.stream().filter(x -> x.getAccountID() == id).findFirst().get();

    }


    /**
     * Add album.
     *
     * @param name  the name of the album
     * @param paths the paths to images in lbum
     */
    void addAlbum(String name, List<String> paths) {
        EntityManager em = getEM();
        try {
            for (Object a : getAllAlbums()) {
                if (((AlbumDAO) a).getAlbumName().equalsIgnoreCase(name)) {
                    //throw exception here because it should not be possible to enter a name that already exists due to previous checks
                    throw new IllegalArgumentException("That album already exists");
                }
            }
            em.getTransaction().begin();
            ArrayList<ImageDAO> images = paths.stream().map(this::findImageDAO).collect(Collectors.toCollection(ArrayList::new));
            AlbumDAO newAlbum = new AlbumDAO(name, images, userDAO.getAccountID());
            em.persist(newAlbum);
            em.getTransaction().commit();
        }
        finally {
            closeEM(em);
        }
    }

    /**
     * finds album
     * @param name name of album you want to find
     * @return corresponding album object
     */
    private AlbumDAO findAlbumDAO(String name){
        EntityManager em = getEM();
        try{
            List<AlbumDAO> albums = getAllAlbums();
            return albums.stream().filter(s->s.getAlbumName().equalsIgnoreCase(name)&&s.getUserID()== this.userDAO.getAccountID()).collect(Collectors.toList()).get(0);
        }
        finally {
            closeEM(em);
        }
    }

    /**
     * Gets list of albums.
     * @return the list
     */
    List getAllAlbums(){
        EntityManager em = getEM();
        try {
            if (isInitialized) {
                Query q = em.createQuery("SELECT OBJECT(o) FROM AlbumDAO o WHERE o.userID=" + this.userDAO.getAccountID());
                return q.getResultList();
            }
            return Collections.emptyList();
        } finally {
            closeEM(em);
        }

    }

    /**
     * Remove album.
     *
     * @param name the name
     */
    void removeAlbum(String name){
        EntityManager em = getEM();
        try{
            AlbumDAO a = findAlbumDAO(name);
            em.getTransaction().begin();
            if (!em.contains(a)) {
                a = em.merge(a);
            }
            em.remove(a);
            em.getTransaction().commit();
        }
        finally {
            closeEM(em);
        }
    }

    /**
     * Add path to album boolean.
     *
     * @param name  the name
     * @param paths the paths
     * @return if successful
     */
    boolean addPathToAlbum(String name, ArrayList<String> paths){
        EntityManager em = getEM();
        int counter = 0;
        try {
            AlbumDAO albumDAO = findAlbumDAO(name);
            for (String s : paths) {
                if (!albumDAO.getImages().contains(s)) {
                    albumDAO.addImage(findImageDAO(s));
                } else {
                    //counts number of paths already present
                    counter++;
                }
            }
            //This might not do anything, if em.find returns a shallow copy, then we do not need this
            em.getTransaction().begin();
            em.merge(albumDAO);
            em.getTransaction().commit();
            //returns true if any tag was added, false if not
            return counter != paths.size();
        } finally {
            closeEM(em);
        }
    }

    /**
     * Remove path from album .
     *
     * @param name  the name
     * @param paths the paths
     * @return true if removal was successful
     */
    boolean removePathFromAlbum(String name, String[] paths){
        EntityManager em = getEM();
        int counter = 0;
        try {
            AlbumDAO albumDAO = findAlbumDAO(name);
            for (String s : paths) {
                if (albumDAO.getImages().contains(s.toLowerCase())) {
                    //The first letter is a capital letter, rest is lower case
                    albumDAO.removeImage(findImageDAO(s));
                } else {
                    //counts number of tags already present
                    counter++;
                }
            }
            //This might not do anything, if em.find returns a shallow copy, then we do not need this
            em.getTransaction().begin();
            em.merge(albumDAO);
            em.getTransaction().commit();
            //returns true if any tag was added, false if not
            return counter != paths.length;
        } finally {
            closeEM(em);
        }
    }

    /**
     * Find image dao by path
     *
     * @param path the path of the image you want to find
     * @return returns null if the image is not found, Team3Image object if it is found
     */
    private ImageDAO findImageDAO(String path) {
        EntityManager em = getEM();
        try {
            List<ImageDAO> images = getAllImageDAO();
            List<ImageDAO> imageDAOList = images.stream().filter(s -> s.getPath().equalsIgnoreCase(path) && s.getUserDAO().getAccountID() == this.userDAO.getAccountID()).collect(Collectors.toList());
            return imageDAOList.get(0);
        } finally {
            closeEM(em);
        }
    }

    /**
     * Remove image dao.
     *
     * @param path the path
     */
    void removeImageDAO(String path) {
        EntityManager em = getEM();
        try {
            ImageDAO t = findImageDAO(path);
            em.getTransaction().begin();
            if (!em.contains(t)) {
                t = em.merge(t);
            }
            em.remove(t);
            em.getTransaction().commit();
        } finally {
            closeEM(em);
        }
    }

    /**
     * Gets all image dao.
     *
     * @return the all image dao
     */
    private List<ImageDAO> getAllImageDAO() {
        EntityManager em = getEM();
        try {
            if (isInitialized) {
                Query q = em.createQuery("SELECT OBJECT(o) FROM ImageDAO o WHERE o.userDAO.accountID =" + this.userDAO.getAccountID());
                return (List<ImageDAO>) q.getResultList();
            }
            return Collections.emptyList();
        } finally {
            closeEM(em);
        }
    }

    /**
     * Gets tags.
     *
     * @param path the path
     * @return the tags
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
     * Add tags boolean.
     *
     * @param path the path
     * @param tags the tags
     * @return the boolean
     */
    boolean addTags(String path, String[] tags) {
        EntityManager em = getEM();
        int counter = 0;
        try {
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
     * Remove tag boolean.
     *
     * @param path the path
     * @param tags the tags
     * @return the boolean
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
     * Sort by array list.
     *
     * @param columnName the column name
     * @param ascending  the ascending
     * @return the array list
     */
    ArrayList<String> sortBy(String columnName, boolean ascending) {
        List<ImageDAO> images = getAllImageDAO();
        columnName = columnName.toLowerCase();
        switch (columnName) {
            //checks what column you are looking for, creates a new arraylist containing only that using lambda
            case "path":
                images.sort(Comparator.comparing(ImageDAO::getPath));
                break;
            case "file_size":
                images.sort(Comparator.comparing(ImageDAO::getFileSize));
                break;
            case "date":
                images.sort(Comparator.comparing(ImageDAO::getDate));
                break;
            case "height":
                images.sort(Comparator.comparing(ImageDAO::getImageHeight));
                break;
            case "width":
                images.sort(Comparator.comparing(ImageDAO::getImageWidth));
                break;
            case "tags":
                //sorts by number of tags
                images.sort(Comparator.comparing((ImageDAO t) -> t.getTags().split(",").length));
                break;
            default:
                throw new IllegalArgumentException("Invalid Column");
        }
        if (!ascending) {
            Collections.reverse(images);
        }
        ArrayList<String> stringPath = new ArrayList<>();
        for (ImageDAO t : images) {
            stringPath.add(t.getPath());
        }
        return stringPath;

    }

    /**
     * Search array list.
     *
     * @param searchFor the search for
     * @param searchIn  the search in
     * @return the array list with results of path
     */
    public ArrayList<String> search(String searchFor, String searchIn) {
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
     * Is path in database boolean.
     *
     * @param path the path
     * @return the boolean
     */
    public boolean isPathInDatabase(String path) {
        return (findImageDAO(path) == null);
    }

    /**
     * Gets column.
     *
     * @param columnName the column name
     * @return the column
     */
    ArrayList<? extends Serializable> getColumn(String columnName) {
        EntityManager em = getEM();
        try {
            columnName = columnName.toLowerCase();
            List<ImageDAO> imageList = getAllImageDAO();
            switch (columnName) {
                //checks what column you are looking for, creates a new arraylist
                case "path":
                    return (ArrayList<String>) imageList.stream().map(ImageDAO::getPath).collect(Collectors.toList());
                case "tags":
                    return (ArrayList<String>) imageList.stream().map(ImageDAO::getTags).filter(s->!s.equals("")).collect(Collectors.toList());
                case "file_size":
                    return (ArrayList<Integer>) imageList.stream().map(ImageDAO::getFileSize).collect(Collectors.toList());
                case "date":
                    return (ArrayList<Integer>) imageList.stream().map(ImageDAO::getDate).collect(Collectors.toList());
                case "height":
                    return (ArrayList<Integer>) imageList.stream().map(ImageDAO::getImageHeight).collect(Collectors.toList());
                case "width":
                    return (ArrayList<Integer>) imageList.stream().map(ImageDAO::getImageWidth).collect(Collectors.toList());
                case "gps_latitude":
                    return (ArrayList<Double>) imageList.stream().map(ImageDAO::getLatitude).collect(Collectors.toList());
                case "gps_longitude":
                    return (ArrayList<Double>) imageList.stream().map(ImageDAO::getLongitude).collect(Collectors.toList());
                default:
                    throw new IllegalArgumentException("Invalid Column");
            }
        } finally {
            closeEM(em);
        }
    }

    /**
     * Get image metadata string [ ].
     *
     * @param path the path
     * @return the string [ ]
     */
    public String[] getImageMetadata(String path) {
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

