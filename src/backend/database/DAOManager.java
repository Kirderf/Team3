package backend.database;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import java.sql.Connection;
import java.sql.SQLException;
//TODO add logger to entire class
//https://stackoverflow.com/questions/12812256/how-do-i-implement-a-dao-manager-using-jdbc-and-connection-pools
public class DAOManager {
    private DataSource src = new DataSource();
    private Connection con;

    public Connection getCon() throws SQLException{
        try {
            open();
        } catch (Exception e){
            e.printStackTrace();
        }
        return con;
    }
    DAOManager() throws Exception{
        try {
            InitialContext ctx = new InitialContext();
        } catch (Exception e){
            //TODO implement logger
            e.printStackTrace();
        }
    }
    public static DAOManager getInstance(){
        return DAOManagerSingleton.INSTANCE.get();
    }
    public void open() throws SQLException {
        try {
            if (this.con == null || this.con.isClosed()) {
                this.con = src.getConnection();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void close() throws SQLException{
        try {
            if(this.con != null && !this.con.isClosed()){
                this.con.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static class DAOManagerSingleton {
        static final ThreadLocal<DAOManager> INSTANCE;
        static
        {
            ThreadLocal<DAOManager> dm;
            try
            {
                dm = ThreadLocal.withInitial(() -> {
                    try
                    {
                        return new DAOManager();
                    }
                    catch(Exception e)
                    {
                        return null;
                    }
                });
            }
            catch(Exception e) {
                e.printStackTrace();
                dm = null;
            }
            INSTANCE = dm;
        }

    }

}
