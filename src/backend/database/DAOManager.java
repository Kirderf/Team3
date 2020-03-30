package backend.database;

import javax.naming.InitialContext;
import java.sql.Connection;
import java.sql.SQLException;

//TODO add logger to entire class
//https://stackoverflow.com/questions/12812256/how-do-i-implement-a-dao-manager-using-jdbc-and-connection-pools
public class DAOManager {
    private Connection con;

    DAOManager(){

    }

    public static DAOManager getInstance() {
        return DAOManagerSingleton.INSTANCE.get();
    }

    public Connection getCon() {
        try {
            open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    public void open() {
        try {
            if (this.con == null || this.con.isClosed()) {
                this.con = DataSource.getConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (this.con != null && !this.con.isClosed()) {
                this.con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class DAOManagerSingleton {
        static final ThreadLocal<DAOManager> INSTANCE;

        static {
            ThreadLocal<DAOManager> dm;
            try {
                dm = ThreadLocal.withInitial(() -> {
                    try {
                        return new DAOManager();
                    } catch (Exception e) {
                        return null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                dm = null;
            }
            INSTANCE = dm;
        }

    }

}
