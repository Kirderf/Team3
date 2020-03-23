package backend;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Fredrik Julsen
 * This datasource is created to load settings for database and load .properties file for username and password.
 */
public class DataSource {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    private static Properties prop;
    private static final Log logger = new Log("Log.log");


    static {
        loadProperties();
        config.setJdbcUrl("jdbc:mysql://mysql.stud.ntnu.no:3306/fredrjul_ImageApp");
        config.setUsername(prop.getProperty("USERNAME"));
        config.setPassword(prop.getProperty("PASSWORD"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    /**
     *
     * @return Connection to database
     * @throws SQLException if connection could not be made
     */
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    private static void loadProperties() {
        logger.logNewInfo("DatabaseSource : " + "Loading .properties file");
        try {
            prop = new Properties();
            String propFileName = ".properties";
            InputStream inputStream = DataSource.class.getClassLoader().getResourceAsStream(propFileName);
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
        } catch (IOException e) {
            logger.logNewFatalError("DatabaseSource : " + e.getLocalizedMessage());
        }
    }
}