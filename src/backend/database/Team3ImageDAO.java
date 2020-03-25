package backend.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

//use compostion, as the only Image objects we would want to manipulate would be the ones already in the database, therefore it should be done through this class
public class Team3ImageDAO {
    //thread safe
    private EntityManagerFactory emf;

    public Team3ImageDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * create new images
     * persist works as SQL INSERT
     * The image path needs to be unique
     */
    public void addImageToTable(String path, String tags, int fileSize, Long date, int imageHeight, int imageWidth, double gpsLatitude, double gpsLongitude) {
        EntityManager em = getEM();
        try {
            //paths are stored with forward slashes instead of backslashes, this helps functionality later in the program
            path = path.replaceAll("\\\\", "/");
            Team3Image team3Image = new Team3Image(path, fileSize, date, imageHeight, imageWidth, gpsLatitude, gpsLongitude);
            em.getTransaction().begin();
            em.persist(team3Image);//into persistence context
            em.getTransaction().commit();//store into database
        } finally {
            closeEM(em);
        }
    }

    /**
     * @param path the path of the image you want to find
     * @return returns null if the image is not found, Team3Image object if it is found
     */
    public Team3Image findTeam3Image(String path) {
        EntityManager em = getEM();
        try {
            return em.find(Team3Image.class, path);
        } finally {
            closeEM(em);
        }
    }

    public void deleteTeam3Image(String path) {
        EntityManager em = getEM();
        try {
            Team3Image t = findTeam3Image(path);
            em.getTransaction().begin();
            em.remove(t);
            em.getTransaction().commit();
        } finally {
            closeEM(em);
        }
    }

    public List<?> getAllTeam3Images() {
        EntityManager em = getEM();
        try {
            //same result with SELECT p FROM Team3Image o
            Query q = em.createQuery("SELECT OBJECT(o) FROM Team3Image o");
            return q.getResultList();
        } finally {
            closeEM(em);
        }
    }

    public int getNumberOfTeam3Images() {
        EntityManager em = getEM();
        try {
            Query q = em.createQuery("SELECT COUNT (o) FROM Team3Image o");
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
            Query q = em.createQuery("SELECT OBJECT (o) FROM Team3Image o WHERE NOT o.latitude = 0.0 AND NOT o.longitude = 0.0");
            return q.getResultList();
        } finally {
            closeEM(em);
        }
    }

    public String getTags(String path) {
        EntityManager em = getEM();
        try {
            Team3Image team3Image = em.find(Team3Image.class, path);
            return team3Image.getPath();
        } finally {
            closeEM(em);
        }
    }

    public boolean addTags(String path, String[] tags) {
        EntityManager em = getEM();
        int counter = 0;
        try {
            Team3Image team3Image = em.find(Team3Image.class, path);
            //convert to lowercase
            List<String> tagList = Arrays.stream(team3Image.getTags().split(",")).map(String::toLowerCase).collect(Collectors.toList());
            for (String s : tags) {
                if (!tagList.contains(s.toLowerCase())) {
                    //The first letter is a capital letter, rest is lower case
                    team3Image.addTag(s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase());
                } else {
                    counter++;
                    //TODO add something if the tag is already present
                }
            }
            //This might not do anything, if em.find returns a shallow copy, then we do not need this
            em.getTransaction().begin();
            Team3Image t = em.merge(team3Image);
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
            Team3Image team3Image = em.find(Team3Image.class, path);
            //convert to lowercase
            List<String> tagList = Arrays.stream(team3Image.getTags().split(","))
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            Arrays.stream(tags).map(tagList::remove);
            //sets the list to the one with the removed tags
            team3Image.setTags(String.join(",", tagList));
            //This might not do anything, if em.find returns a shallow copy, then we do not need this
            em.getTransaction().begin();
            em.merge(team3Image);
            em.getTransaction().commit();
            //returns true if any tag was removed, false if not
            return tagList.size() == tags.length;
        } finally {
            closeEM(em);
        }
    }

    public ArrayList<String> sortBy(String columnName, boolean ascending) {
        List<Team3Image> images = (List<Team3Image>) getAllTeam3Images();
        columnName = columnName.toLowerCase();
        switch (columnName) {
            //checks what column you are looking for, creates a new arraylist containing only that using lambda
            case "path":
                images.sort(Comparator.comparing(Team3Image::getPath));
                break;
            case "file_size":
                images.sort(Comparator.comparing(Team3Image::getFileSize));
                break;
            case "date":
                images.sort(Comparator.comparing(Team3Image::getDate));
                break;
            case "height":
                images.sort(Comparator.comparing(Team3Image::getImageHeight));
                break;
            case "width":
                images.sort(Comparator.comparing(Team3Image::getImageWidth));
                break;
            case "tags":
                //sorts by number of tags
                images.sort(Comparator.comparing((Team3Image t) -> t.getTags().split(",").length));
                break;
            default:
                throw new IllegalArgumentException("Invalid Column");
        }
        if (!ascending) {
            Collections.reverse(images);
        }
        ArrayList<String> stringPath = new ArrayList<>();
        for (Team3Image t : images) {
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
        List<Team3Image> images = (List<Team3Image>) getAllTeam3Images();
        ArrayList<String> pathResults = new ArrayList<>();

        if (searchIn.equalsIgnoreCase("path")) {
            pathResults.addAll(images.stream()
                    .filter(s -> String.valueOf(s.getPath()).contains(searchFor))
                    .map(Team3Image::getPath)
                    .collect(Collectors.toList()));
        }
        if (searchIn.equalsIgnoreCase("metadata")) {
            //creates a stream, filters it based on whether the attribute contains the search term and whether pathresult already contains the image, then it maps the paths, turn these into a list and adds this list to the pathresult list
            pathResults.addAll(images.stream()
                    .filter(s -> String.valueOf(s.getFileSize()).contains(searchFor))
                    .map(Team3Image::getPath)
                    .collect(Collectors.toList()));
            pathResults.addAll(images.stream()
                    .filter(s -> String.valueOf(s.getDate()).contains(searchFor) && !pathResults.contains(s.getPath()))
                    .map(Team3Image::getPath)
                    .collect(Collectors.toList()));
            pathResults.addAll(images.stream()
                    .filter(s -> String.valueOf(s.getImageHeight()).contains(searchFor) && !pathResults.contains(s.getPath()))
                    .map(Team3Image::getPath)
                    .collect(Collectors.toList()));
            pathResults.addAll(images.stream()
                    .filter(s -> String.valueOf(s.getImageWidth()).contains(searchFor) && !pathResults.contains(s.getPath()))
                    .map(Team3Image::getPath)
                    .collect(Collectors.toList()));
            pathResults.addAll(images.stream()
                    .filter(s -> String.valueOf(s.getLatitude()).contains(searchFor) && !pathResults.contains(s.getPath()))
                    .map(Team3Image::getPath)
                    .collect(Collectors.toList()));
            pathResults.addAll(images.stream()
                    .filter(s -> String.valueOf(s.getLongitude()).contains(searchFor) && !pathResults.contains(s.getPath()))
                    .map(Team3Image::getPath)
                    .collect(Collectors.toList()));
        }
        if (searchIn.equalsIgnoreCase("tags")) {
            //checks whether a list of the tags in the each picture contains the search term and whether pathResults already contains the path for s. It then takes the path for all the valid images, adds them to a list, and then adds the entire list to pathresults
            pathResults.addAll(images.stream()
                    .filter(s ->
                            Arrays.asList(s.getTags().split(","))
                                    .contains(searchFor.toUpperCase().substring(0, 1) + searchFor.substring(1).toLowerCase()) && !pathResults.contains(s.getPath()))
                    .map(Team3Image::getPath).collect(Collectors.toList()));
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
            List<Team3Image> imageList = (List<Team3Image>) getAllTeam3Images();
            switch (columnName) {
                //checks what column you are looking for, creates a new arraylist
                case "path":
                    return (ArrayList<String>) imageList.stream().map(Team3Image::getPath).collect(Collectors.toList());
                case "tags":
                    return (ArrayList<String>) imageList.stream().map(Team3Image::getTags).collect(Collectors.toList());
                case "file_size":
                    return (ArrayList<Integer>) imageList.stream().map(Team3Image::getFileSize).collect(Collectors.toList());
                case "date":
                    return (ArrayList<Long>) imageList.stream().map(Team3Image::getDate).collect(Collectors.toList());
                case "height":
                    return (ArrayList<Integer>) imageList.stream().map(Team3Image::getImageHeight).collect(Collectors.toList());
                case "width":
                    return (ArrayList<Integer>) imageList.stream().map(Team3Image::getImageWidth).collect(Collectors.toList());
                case "gps_latitude":
                    return (ArrayList<Double>) imageList.stream().map(Team3Image::getLatitude).collect(Collectors.toList());
                case "gps_longitude":
                    return (ArrayList<Double>) imageList.stream().map(Team3Image::getLongitude).collect(Collectors.toList());
                default:
                    throw new IllegalArgumentException("Invalid Column");
            }
        } finally {
            em.close();
        }
    }

    public String[] getImageMetadata(String path) {
        Team3Image team3Image = findTeam3Image(path);
        return new String[]{team3Image.getPath(),team3Image.getTags(),String.valueOf(team3Image.getFileSize()),String.valueOf(team3Image.getDate()),String.valueOf(team3Image.getImageHeight()),String.valueOf(team3Image.getImageWidth()),String.valueOf(team3Image.getLatitude()),String.valueOf(team3Image.getLongitude())};
    }

    public void editTeam3Image(Team3Image team3Image) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            Team3Image t = em.merge(team3Image);
            em.getTransaction().commit();
        } finally {
            closeEM(em);
        }
    }

    private EntityManager getEM() {
        return emf.createEntityManager();
    }

    private void closeEM(EntityManager em) {
        if (em != null && em.isOpen()) em.close();
    }
}

