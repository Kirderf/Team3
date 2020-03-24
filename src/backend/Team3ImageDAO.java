package backend;
import javax.persistence.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Queue;


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
    public void storeNewImage(Team3Image team3Image){
        EntityManager em = getEM();
        try{
            em.getTransaction();
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
    public List<Team3Image> getNumberOfBooke(){
        EntityManager em = getEM();
        try{
            Query q = em.createQuery("SELECT OBJECT(O) FROM backend.Team3Image o");
        } finally {
            closeEM(em);
        }
    }
    private EntityManager getEM(){
        return emf.createEntityManager();
    }
    private void closeEM(EntityManager em){
        if(em!=null && em.isOpen()) em.close();
    }
}
