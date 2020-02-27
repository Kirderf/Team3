package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

//TODO: add javadoc

public class Database {
    Random random = new Random();
    int upperBound = 10000000;
    String table = "fredrjul_" + random.nextInt(upperBound);
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
                        "GPS_Latitude double(17,15),\n" +
                        "GPS_Longitude double(17,15),\n" +
                        "PRIMARY KEY (ImageID));");
        stmt.execute();
    }

    /**
     * This method is going to write data to the database
     * //TODO: Add parameter to method
     *
     * @param // data to add
     */
    public boolean writeToDatabase(String path, String tags, int file_size, Date date, int image_height, int image_wight, double GPS_Latitude, double GPS_Longitude) throws SQLException {
        String sql1="Insert into "+table+" Values(?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql1);
        preparedStatement.setNull(1,0);
        preparedStatement.setString(2,path);
        preparedStatement.setString(3,tags);
        preparedStatement.setInt(4,file_size);
        preparedStatement.setDate(5,date);
        preparedStatement.setInt(6,image_height);
        preparedStatement.setInt(7,image_wight);
        preparedStatement.setDouble(8,GPS_Latitude);
        preparedStatement.setDouble(9,GPS_Longitude);
        return preparedStatement.execute();
    }
    public String readDatabase() throws SQLException {
        //TODO: Add method functionality
        String sql = "Select * from "+ table;
        Statement stmt = con.createStatement();
        ResultSet result = stmt.executeQuery(sql);
        return result.toString();
    }
    public ArrayList readDatabase(String columnName) throws SQLException {
        //TODO: Add method functionality
        String sql = "Select "+columnName+" from "+ table;
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet result = stmt.executeQuery();
        ArrayList arrayList = new ArrayList();
        while (result.next()){
            arrayList.add(result.getObject(columnName));
        }
        return arrayList;
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
        boolean resultSet = stmt.executeQuery().next();
        if (!resultSet) {
            return false;
        }
        return true;
    }

    public void createTable() throws SQLException {
        while(isTableInDatabase(table)) {
            table = "fredrjul_" + random.nextInt(upperBound);
        }
        regTable();

    }

    public void openConnection() throws SQLException {
        con = DriverManager.getConnection("jdbc:mysql://mysql.stud.ntnu.no:3306/fredrjul_ImageApp", "fredrjul_Image", "Password123");
    }

    public void closeConnection() throws SQLException {
        con.close();
    }
}