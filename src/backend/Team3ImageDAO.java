package backend;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.io.IOException;
import java.util.Date;
import java.util.List;


public class Team3ImageDAO {
    //thread safe
    private EntityManagerFactory emf;

    public Team3ImageDAO(EntityManagerFactory emf) throws IOException {
        this.emf = emf;
    }
    /**
     * create new images
     * persist works as SQL INSERT
     * The image path needs to be unique
     */
    public void storeNewTeam3Image(Team3Image team3Image){
        EntityManager em = getEM();
        try{
            //paths are stored with forward slashes instead of backslashes, this helps functionality later in the program
            //team3Image.setPath(team3Image.getPath().replaceAll("\\\\","/"));
            em.getTransaction().begin();
            em.persist(team3Image);//into persistence context
            em.getTransaction().commit();//store into database
        }finally {
            closeEM(em);
        }
    }
    public Team3Image findTeam3Image(String path){
        EntityManager em = getEM();
        try{
            return em.find(Team3Image.class, path);
        } finally {
            closeEM(em);
        }
    }
    public void deleteTeam3Image(String path){
        EntityManager em = getEM();
        try{
            Team3Image t = findTeam3Image(path);
            em.getTransaction().begin();
            em.remove(t);
            em.getTransaction().commit();
        } finally {
            closeEM(em);
        }
    }

    public List<Team3Image> getAllTeam3Images(){
        EntityManager em = getEM();
        try{
            //same result with SELECT p FROM Team3Image o
            Query q = em.createQuery("SELECT OBJECT(o) FROM Team3Image o");
            return q.getResultList();
        } finally {
            closeEM(em);
        }
    }
    public int getNumberOfTeam3Images(){
        EntityManager em = getEM();
        try{
            Query q = em.createQuery("SELECT COUNT (o) FROM Team3Image o");
            Long num = (Long)q.getSingleResult();
            return num.intValue();
        } finally {
            closeEM(em);
        }
    }
    public List<Team3Image> getTeam3ImageWithGPSData(){
        EntityManager em = getEM();
        try{
            //selects all images with latitude and longitude that's not 0.0
            Query q = em.createQuery("SELECT OBJECT (o) FROM Team3Image o WHERE NOT o.latitude = 0.0 AND NOT o.longitude = 0.0");
            return q.getResultList();
        } finally {
            closeEM(em);
        }
    }
    public void editTeam3Image(Team3Image team3Image) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            Team3Image t = em.merge(team3Image);
            em.getTransaction().commit();
        } finally{
            closeEM(em);
        }
    }
    private EntityManager getEM(){
        return emf.createEntityManager();
    }
    private void closeEM(EntityManager em){
        if(em!=null && em.isOpen()) em.close();
    }
    //simple test
    public static void main(String args[]) throws Exception{
        EntityManagerFactory emf = null;
        Team3ImageDAO facade = null;
        System.out.println("start...");
        try{

            emf = Persistence.createEntityManagerFactory("LecturePU");
            //LecturePU=Persistence Unit Name, see persistence.xml
            System.out.println("EntityManagerFactory created " + emf);
            facade = new Team3ImageDAO(emf);
            System.out.println("Team3ImageDAO created");
            //create an Image using the set methods
            Team3Image team3Image = new Team3Image();
            team3Image.setPath("Hjem til jul");
            team3Image.setFileSize(33);
            team3Image.setDate(new Date(20010911));
            team3Image.setImageHeight(14);
            team3Image.setImageWidth(435);
            team3Image.setLatitude(44.8);
            team3Image.setLongitude(544.58);
            team3Image.setTags("jonas");
            facade.storeNewTeam3Image(team3Image);
//make new book with constructor instead of the set methods
            team3Image = new Team3Image("mamam",55,new Date(19991103),22,55,535.2,663.3);
            facade.storeNewTeam3Image(team3Image);
//List books
            System.out.println("These Images are stored in the database:");
            List<Team3Image> imageList = facade.getAllTeam3Images();
            for (Team3Image t : imageList){
                System.out.println("---" + t);
            }
            team3Image = (Team3Image)imageList.get(0);
            team3Image.setPath("changed path");
            facade.editTeam3Image(team3Image);
            team3Image = facade.findTeam3Image(team3Image.getPath());//fetch book
            System.out.println("Image changed and is now like this: " + team3Image);
//find number of books in database
            int numberOfTeam3Images = facade.getNumberOfTeam3Images();
            System.out.println("There are " + numberOfTeam3Images + " images in the databse in the database");
//list books by a chosen author
            String theAuthor = "Author2";
            imageList = facade.getTeam3ImageWithGPSData();
            System.out.println(" These images exists with valid gps data" + imageList.size());
            for (Team3Image b : imageList){
                System.out.println("\t" + b.getLatitude() + "," + b.getLongitude());
            }
        }finally{
            if(emf != null) {
                emf.close();
            }
        }
    }
}

