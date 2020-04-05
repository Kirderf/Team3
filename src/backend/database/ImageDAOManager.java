package backend.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

//use compostion, as the only Image objects we would want to manipulate would be the ones already in the database, therefore it should be done through this class
public class ImageDAOManager {
    private boolean isInitialized = false;
    private int instanceID = 13;
    private EntityManagerFactory emf;

    public ImageDAOManager(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    /**
     * create new images
     * persist works as SQL INSERT
     * The image path needs to be unique
     */
    public void addImageToTable(String path, int fileSize, int date, int imageHeight, int imageWidth, double gpsLatitude, double gpsLongitude) {
        EntityManager em = getEM();
        try {
            //paths are stored with forward slashes instead of backslashes, this helps functionality later in the program
            path = path.replaceAll("\\\\", "/");
            ImageDAO imageDAO = new ImageDAO(instanceID, path, fileSize, date, imageHeight, imageWidth, gpsLatitude, gpsLongitude);
            em.getTransaction().begin();
            em.persist(imageDAO);//into persistence context
            em.getTransaction().commit();//store into database
        } finally {
            closeEM(em);
        }
    }

    /**
     * @param path the path of the image you want to find
     * @return returns null if the image is not found, Team3Image object if it is found
     */
    public ImageDAO findTeam3Image(String path) {
        EntityManager em = getEM();
        try {
            return em.find(ImageDAO.class, path);
        } finally {
            closeEM(em);
        }
    }

    public void deleteTeam3Image(String path) {
        EntityManager em = getEM();
        try {
            ImageDAO t = findTeam3Image(path);
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

    public List<?> getAllTeam3Images() {
        EntityManager em = getEM();
        try {
            if (isInitialized) {
                Query q = em.createQuery("SELECT OBJECT(o) FROM ImageDAO o WHERE o.ID=" + this.instanceID);
                return q.getResultList();
            }
            //same result with SELECT p FROM Team3Image o
            return Collections.emptyList();
        } finally {
            closeEM(em);
        }
    }

    public void isAccountPresent() {
        if(getNumberOfTeam3Images() > 0) setInitialized(true);
    }

    public int getNumberOfTeam3Images() {
        EntityManager em = getEM();
        try {
            Query q = em.createQuery("SELECT COUNT (o) FROM ImageDAO o WHERE o.ID=" + this.instanceID);
            Long num = (Long) q.getSingleResult();
            return num.intValue();
        } finally {
            closeEM(em);
        }
    }

    public List<?> getTeam3ImageWithGPSData() {
        EntityManager em = getEM();
        try {
            //selects all images with latitude and longitude that's not 0.0
            Query q = em.createQuery("SELECT OBJECT (o) FROM ImageDAO o WHERE NOT o.latitude = 0.0 AND NOT o.longitude = 0.0 AND o.ID=" + this.instanceID);
            return q.getResultList();
        } finally {
            closeEM(em);
        }
    }

    public String getTags(String path) {
        EntityManager em = getEM();
        try {
            ImageDAO imageDAO = em.find(ImageDAO.class, path);
            return imageDAO.getTags();
        } finally {
            closeEM(em);
        }
    }

    public boolean addTags(String path, String[] tags) {
        EntityManager em = getEM();
        int counter = 0;
        try {
            ImageDAO imageDAO = em.find(ImageDAO.class, path);
            //convert to lowercase
            List<String> tagList = Arrays.stream(imageDAO.getTags().split(",")).map(String::toLowerCase).collect(Collectors.toList());
            for (String s : tags) {
                if (!tagList.contains(s.toLowerCase())) {
                    //The first letter is a capital letter, rest is lower case
                    imageDAO.addTag(s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase());
                } else {
                    counter++;
                    //TODO add something if the tag is already present
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

    public boolean removeTag(String path, String[] tags) {
        EntityManager em = getEM();
        try {
            ImageDAO imageDAO = em.find(ImageDAO.class, path);
            //convert to lowercase
            List<String> tagList = Arrays.stream(imageDAO.getTags().split(","))
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
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

    public ArrayList<String> sortBy(String columnName, boolean ascending) {
        List<ImageDAO> images = (List<ImageDAO>) getAllTeam3Images();
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

    public ArrayList<String> search(String searchFor, String searchIn) {
        ArrayList<String> validColumns = new ArrayList<>();
        validColumns.add("path");
        validColumns.add("tags");
        validColumns.add("metadata");
        if (!validColumns.contains(searchIn.toLowerCase()) || searchFor == null) return new ArrayList<>();
        List<ImageDAO> images = (List<ImageDAO>) getAllTeam3Images();
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

    public boolean isPathInDatabase(String path) {
        return (findTeam3Image(path) == null);
    }

    public ArrayList<? extends Serializable> getColumn(String columnName) {
        EntityManager em = getEM();
        try {
            columnName = columnName.toLowerCase();
            List<ImageDAO> imageList = (List<ImageDAO>) getAllTeam3Images();
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

    public String[] getImageMetadata(String path) {
        ImageDAO imageDAO = findTeam3Image(path);
        return new String[]{imageDAO.getPath(), imageDAO.getTags(), String.valueOf(imageDAO.getFileSize()), String.valueOf(imageDAO.getDate()), String.valueOf(imageDAO.getImageHeight()), String.valueOf(imageDAO.getImageWidth()), String.valueOf(imageDAO.getLatitude()), String.valueOf(imageDAO.getLongitude())};
    }

    public void editTeam3Image(ImageDAO imageDAO) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.merge(imageDAO);
            em.getTransaction().commit();
        } finally {
            closeEM(em);
        }
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

