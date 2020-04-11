package backend.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public abstract class UserDAOManager {

    public static UserDAO newUser(String username, String password, EntityManagerFactory emf){
        EntityManager em = emf.createEntityManager();
        try{
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

    }
    public static UserDAO login(String username, String password, EntityManagerFactory emf){
        EntityManager em = emf.createEntityManager();
        try {
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
