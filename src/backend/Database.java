package backend;
import java.sql.*;
import java.util.Random;

//TODO: add javadoc

public class Database {
    Random random = new Random();
    String table = "fredrjul_" + random.nextInt(10000000);
    Connection con;

    public void init() {
        while (isTableInDatabase(table)) {

        }
    }

    /**
     * This method is going to create a new database table and declare a variable to the rest of the class
     */
    public void regTable() throws Exception {
        // Url to database, username and password to setup connection to database

        PreparedStatement stmt = con.prepareStatement(
                "CREATE TABLE " + table + " (\n" +
                "    ImageID int NOT NULL AUTO_INCREMENT,\n" +
                "    Path varchar(255),\n" +
                "    Tags varchar(255),\n" +
                "    PRIMARY KEY (ImageID)\n" +
                ");");
        stmt.execute();
    }

    /**
     * This method is going to write data to the database
     * //TODO: Add parameter to method
     *
     * @param // data to add
     */
    public void writeToDatabase() throws SQLException {
    PreparedStatement preparedStatement = con.prepareStatement(
            "SELECT "+ table +" "
    );

    }

    /**
     *
     */
    public void readDatabase() {
        //TODO: Add method functionality
    }

    private boolean isTableInDatabase(String tableName) {
        return false;
    }

    public void openConnection() throws SQLException {
        con = DriverManager.getConnection("jdbc:mysql://mysql.stud.ntnu.no:3306/fredrjul_ImageApp", "fredrjul_Image", "Password123");
    }
    public void closeConnection() throws SQLException {
        con.close();
    }
}