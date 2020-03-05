package backend;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Datasource {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setJdbcUrl( "jdbc:mysql://mysql.stud.ntnu.no:3306/fredrjul_ImageApp" );
        config.setUsername( "fredrjul_Image" );
        config.setPassword( "Password123" );
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        ds = new
        HikariDataSource( config );
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
}
}