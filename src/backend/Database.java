package backend;

import java.sql.*;
import java.util.Random;

//TODO: add javadoc

public class Database {
    Random random = new Random();
    String table = "fredrjul_" + random.nextInt(3);
    Connection con;

    public void init() throws SQLException {
    }

    /**
     * This method is going to create a new database table and declare a variable to the rest of the class
     */
    private void regTable() throws SQLException {
        // Url to database, username and password to setup connection to database

        PreparedStatement stmt = con.prepareStatement(
                "CREATE TABLE " + table + " (\n" +
                        "ImageID int AUTO_INCREMENT,\n" +
                        "Path varchar(255),\n" +
                        "Tags varchar(255),\n" +
                        "File_size varchar(255),\n" +
                        "Date date not null ,\n" +
                        "Height int(11),\n" +
                        "Width int(11),\n" +
                        "GPS_Latitude double(10,7),\n" +
                        "GPS_Longitude double(10,7),\n" +
                        "PRIMARY KEY (ImageID));");
        stmt.execute();
    }

    /**
     * This method is going to write data to the database
     * //TODO: Add parameter to method
     *
     * @param // data to add
     */
    public void writeToDatabase(String sql) throws SQLException {
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.execute();
    }
    public void readDatabase() {
        //TODO: Add method functionality
    }

    /**
     * Checks if a table is in a database
     * @param tableName table to check
     * @return boolean
     * @throws SQLException
     */
    private boolean isTableInDatabase(String tableName) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM information_schema.tables WHERE table_schema = 'fredrjul_ImageApp' AND table_name = " + "\'" + table + "\'" +
                "");
        ResultSet resultSet = stmt.executeQuery();
        if (resultSet.getRow() == 0) {
            return false;
        }
        return true;
    }

    public void createTable() throws SQLException {
        if (isTableInDatabase(table)) {
            table = "fredrjul_" + random.nextInt(3);
            createTable();
        } else {
            regTable();
        }

    }

    public void openConnection() throws SQLException {
        con = DriverManager.getConnection("jdbc:mysql://mysql.stud.ntnu.no:3306/fredrjul_ImageApp", "fredrjul_Image", "Password123");
    }

    public void closeConnection() throws SQLException {
        con.close();
    }
}