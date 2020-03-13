package backend;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class Datasource {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    private static Properties prop;


    static {
        loadProperties();
        config.setJdbcUrl("jdbc:mysql://mysql.stud.ntnu.no:3306/fredrjul_ImageApp");
        config.setUsername(prop.getProperty("USERNAME"));
        config.setPassword(prop.getProperty("PASSWORD"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new
                HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    private static void loadProperties() {
        try {
            prop = new Properties();
            String propFileName = ".properties";
            InputStream inputStream = Datasource.class.getClassLoader().getResourceAsStream(propFileName);
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}