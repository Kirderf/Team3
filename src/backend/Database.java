package backend;

import com.sun.corba.se.spi.monitoring.StatisticMonitoredAttribute;
import javafx.beans.binding.StringBinding;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

//TODO: add javadoc

public class Database {
    Random random = new Random();
    int upperBound = 10000000;
    String table = "fredrjul_" + random.nextInt(upperBound);
    Connection con = null;

    /**
     * This method is going to create a new database table and declare a variable to the rest of the class
     */
    private boolean regTable() throws SQLException {
        PreparedStatement stmt = con.prepareStatement(
                "CREATE TABLE " + table + " (\n" +
                        "ImageID int AUTO_INCREMENT,\n" +
                        "Path varchar(255) UNIQUE ,\n" +
                        "Tags varchar(255),\n" +
                        "File_size varchar(255),\n" +
                        "DATE int(11),\n" +
                        "Height int(11),\n" +
                        "Width int(11),\n" +
                        "GPS_Latitude double(17,15),\n" +
                        "GPS_Longitude double(17,15),\n" +
                        "PRIMARY KEY (ImageID));");
        return !stmt.execute();
    }

    /**
     * This method is going to write data to the database
     * //TODO: Add parameter to method
     *
     * @param // data to add
     */
    public boolean addImageToTable(String path, String tags, int file_size, Long date, int image_height, int image_wight, double GPS_Latitude, double GPS_Longitude) throws SQLException {
        if (!isTableInDatabase(table)) {
            createTable();
        }
        String sql1 = "Insert into " + table + " Values(?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql1);
        preparedStatement.setNull(1, 0);
        preparedStatement.setString(2, path);
        preparedStatement.setString(3, tags);
        preparedStatement.setInt(4, file_size);
        preparedStatement.setLong(5, date);
        preparedStatement.setInt(6, image_height);
        preparedStatement.setInt(7, image_wight);
        preparedStatement.setDouble(8, GPS_Latitude);
        preparedStatement.setDouble(9, GPS_Longitude);
        boolean result = !preparedStatement.execute();
        preparedStatement.close();
        return result;

    }

    /**
     * Only use if getting for one image. For getting all tags from all images use getData
     * @param path
     * @return String
     */
    public StringBuilder getTags(String path) throws SQLException {
        String sql = "SELECT * FROM "+table+" WHERE "+table+".ImageID = "+findImage(path);
        Statement statement = con.createStatement();
        ResultSet resultSet =statement.executeQuery(sql);
        if (resultSet.next()){
            return new StringBuilder(resultSet.getString(3));
        }
        return new StringBuilder("");
    }
    public boolean addTags(String path,String[] tags) throws SQLException {
        StringBuilder oldtags = getTags(path);
        for (String string : tags) {
            oldtags.append(",").append(string);
        }
        PreparedStatement statement1 = con.prepareStatement("UPDATE fredrjul_ImageApp."+table+" SET fredrjul_ImageApp."+table+".Tags = '"+oldtags+"' WHERE fredrjul_ImageApp."+table+".ImageID = "+findImage(path));
        return !statement1.execute();
    }

    /**
     * Reads the database table and finds the column with columnName. Then return a ArrayList with the elements.
     *
     * @param columnName
     * @return
     * @throws SQLException
     */
    public ArrayList getColumn(String columnName) throws SQLException {
        String sql = "Select " + columnName + " from " + table;
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet result = stmt.executeQuery();
        ArrayList<Object> arrayList = new ArrayList();
        while (result.next()) {
            arrayList.add(result.getObject(columnName));
        }
        return arrayList;
    }

    /**
     * Get image metadata from path
     *
     * @param path Path to image
     * @return A String array of all parameters except ImageID
     * @throws SQLException
     */
    public String[] getImageMetadata(String path) throws SQLException {
        String sql = "SELECT * FROM " + table + "\n" +
                "WHERE " + table + ".Path" + " LIKE '%" + path + "%' LIMIT 1";
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            String[] returnValues = new String[8];
            returnValues[0] = rs.getString(2);
            returnValues[1] = rs.getString(3);
            returnValues[2] = String.valueOf(rs.getInt(4));
            returnValues[3] = rs.getDate(5).toString();
            returnValues[4] = String.valueOf(rs.getInt(6));
            returnValues[5] = String.valueOf(rs.getInt(7));
            returnValues[6] = String.valueOf(rs.getDouble(8));
            returnValues[7] = String.valueOf(rs.getDouble(9));
            return returnValues;
        } else {
            return null;
        }

    }

    /**
     * Checks if a table is in a database
     *
     * @param tableName table to check
     * @return boolean
     * @throws SQLException
     */
    public boolean isTableInDatabase(String tableName) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM information_schema.tables WHERE table_schema = 'fredrjul_ImageApp' AND table_name = " + "\'" + table + "\'" +
                "");
        return stmt.executeQuery().next();
    }

    /**
     * Checks if there is an open connection
     *
     * @return false if there is not an open connection. Return true if there is an open connection.
     * @throws SQLException Failed to check
     */
    public boolean isConnection() throws SQLException {
        if (con == null) {
            return false;
        }
        return !con.isClosed();
    }

    /**
     * @return
     * @throws SQLException
     */
    public boolean createTable() throws SQLException {
        if (!isConnection()) {
            openConnection();
        }
        while (isTableInDatabase(table)) {
            table = "fredrjul_" + random.nextInt(upperBound);
        }
        return regTable();

    }

    /**
     * Deletes a the current table from database
     *
     * @return
     * @throws SQLException
     */
    public boolean deleteTable() throws SQLException {
        String sql = "DROP TABLE " + table;
        PreparedStatement stmt = con.prepareStatement(sql);
        return stmt.execute();
    }

    public boolean deleteFromDatabase(String path) throws SQLException {
        String sql = "DELETE FROM " + table + " WHERE " + table + ".ImageID=" + findImage(path);
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        return !preparedStatement.execute();
    }

    public int findImage(String path) throws SQLException {
        String sql = "SELECT * FROM " + table + "\n" +
                "WHERE " + table + ".Path" + " LIKE '%" + path + "%'";
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        if (!rs.next()) {
            return 0;
        } else {
            do {
                if (rs.getString(2).equalsIgnoreCase(path)) {
                    return rs.getInt(1);
                }
            } while (rs.next());
        }
        return 0;
    }

    /**
     * Must be called before calling any other method
     *
     * @throws SQLException
     */
    public boolean openConnection() throws SQLException {
        if (!isConnection()) {
            con = DriverManager.getConnection("jdbc:mysql://mysql.stud.ntnu.no:3306/fredrjul_ImageApp", "fredrjul_Image", "Password123");
            return true;
        }
        return false;

    }

    /**
     * Must be called last after openConnection and any other method
     *
     * @throws SQLException
     */
    public boolean closeConnection() throws SQLException {

        if (con != null) {
            con.close();
            return con.isClosed();
        } else {
            return false;
        }
    }

    /**
     * This method closes the database and deletes the current table to free up space.
     *
     * @return
     * @throws SQLException
     */
    public boolean closeDatabase() throws SQLException {
        if (openConnection()) {
            try {
                deleteTable();
            } catch (SQLException e) {
                System.out.println("Didnt delete table");
            }
        }
        return closeConnection();
    }

    //TODO sort the database based on the parameter in this method
    public boolean sortBy(String sort) {
        if (sort.equals("Size Ascending")) {

        }
        return false;
    }

}