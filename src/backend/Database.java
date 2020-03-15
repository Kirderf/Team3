package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

//TODO: add javadoc

/**
 * @author Fredrik Julsen
 */
public class Database {
    private static final Log logger = new Log("Log.log");
    private Random random = new Random();
    private int upperBound = 10000000;
    private String table = "fredrjul_" + random.nextInt(upperBound);
    private Connection con = null;
    private ResultSet resultSet = null;
    private PreparedStatement statement = null;
    private Statement stmt = null;

    public Database() {
        logger.logNewInfo("Creating Database object");
        try {
            openConnection();
            createTable();
            close();
        } catch (SQLException e) {
            logger.logNewFatalError(e.getLocalizedMessage());
            System.exit(0);
        }
    }

    /**
     * This method is going to create a new database table and declare a variable to the rest of the class
     */
    private boolean regTable() throws SQLException {
        logger.logNewInfo(table);
        statement = con.prepareStatement(
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
        return !statement.execute();
    }

    /**
     * This method is going to write data to the database
     * //TODO: Add parameter to method
     *
     * @param // data to add
     */
    public boolean addImageToTable(String path, String tags, int fileSize, Long date, int imageHeight, int imageWight, double gpsLatitude, double gpsLongitude) throws SQLException {
        logger.logNewInfo("Added Image to path" + path);
        String sql1 = "Insert into " + table + " Values(?,?,?,?,?,?,?,?,?)";
        statement = con.prepareStatement(sql1);
        statement.setNull(1, 0);
        statement.setString(2, path.replaceAll("\\\\", "/"));
        statement.setString(3, tags);
        statement.setInt(4, fileSize);
        statement.setLong(5, date);
        statement.setInt(6, imageHeight);
        statement.setInt(7, imageWight);
        statement.setDouble(8, gpsLatitude);
        statement.setDouble(9, gpsLongitude);
        boolean result = !statement.execute();
        statement.close();
        return result;

    }

    /**
     * Only use if getting for one image. For getting all tags from all images use getData
     *
     * @param path
     * @return String
     */
    public StringBuilder getTags(String path) throws SQLException {
        logger.logNewInfo("Getting Tags");
        String sql = "SELECT * FROM " + table + " WHERE " + table + ".ImageID = " + findImage(path);
        stmt = con.createStatement();
        resultSet = stmt.executeQuery(sql);
        if (resultSet.next()) {
            return new StringBuilder(resultSet.getString(3));
        }
        return new StringBuilder("");
    }

    public boolean addTags(String path, String[] tags) throws SQLException {
        logger.logNewInfo("Adding Tags");
        StringBuilder oldtags = getTags(path);
        for (String string : tags) {
            oldtags.append(",").append(string);
        }
        statement = con.prepareStatement("UPDATE fredrjul_ImageApp." + table + " SET fredrjul_ImageApp." + table + ".Tags = '" + oldtags + "' WHERE fredrjul_ImageApp." + table + ".ImageID = " + findImage(path));
        return !statement.execute();
    }

    /**
     * Reads the database table and finds the column with columnName. Then return a ArrayList with the elements.
     *
     * @param columnName
     * @return
     * @throws SQLException
     */
    public ArrayList getColumn(String columnName) throws SQLException {
        logger.logNewInfo("Getting Column");
        String sql = "Select " + columnName + " from " + table;
        statement = con.prepareStatement(sql);
        resultSet = statement.executeQuery();
        ArrayList arrayList = new ArrayList();
        while (resultSet.next()) {
            arrayList.add(resultSet.getObject(columnName));
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
        logger.logNewInfo("Getting ImageMetaData from " + path);
        String sql = "SELECT * FROM " + table + " WHERE " + table + ".Path" + " LIKE '%" + path.replaceAll("\\\\", "/") + "%' LIMIT 1";
        statement = con.prepareStatement(sql);
        try (ResultSet rs = statement.executeQuery()) {
            //TODO fix bug where this is always false, and the statement is always empty
            if (rs.next()) {
                String[] returnValues = new String[8];
                returnValues[0] = rs.getString(2);
                returnValues[1] = rs.getString(3);
                returnValues[2] = String.valueOf(rs.getInt(4));
                returnValues[3] = String.valueOf(rs.getLong(5));
                returnValues[4] = String.valueOf(rs.getInt(6));
                returnValues[5] = String.valueOf(rs.getInt(7));
                returnValues[6] = String.valueOf(rs.getDouble(8));
                returnValues[7] = String.valueOf(rs.getDouble(9));
                return returnValues;
            } else {
                return null;
            }
        }

    }

    /**
     * Checks if a table is in a database
     *
     * @return boolean
     * @throws SQLException
     */
    public boolean isTableInDatabase() throws SQLException {
        logger.logNewInfo("Checking if table is in the database");
        statement = con.prepareStatement("SELECT * FROM information_schema.tables WHERE table_schema = 'fredrjul_ImageApp' AND table_name = " + "\'" + table + "\'" +
                "");
        return statement.executeQuery().next();
    }

    /**
     * Checks if there is an open connection
     *
     * @return false if there is not an open connection. Return true if there is an open connection.
     * @throws SQLException Failed to check
     */
    public boolean isConnection() throws SQLException {
        logger.logNewInfo("Checking database connection");
        if (con == null) {
            logger.logNewInfo("There is no connection");
            return false;
        }
        return !con.isClosed();
    }

    /**
     * @return
     * @throws SQLException
     */
    private boolean createTable() throws SQLException {
        logger.logNewInfo("Creating table");
        while (isTableInDatabase()) {
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
        logger.logNewInfo("Deleting table");
        String sql = "DROP TABLE " + table;
        statement = con.prepareStatement(sql);
        return statement.execute();
    }

    public boolean deleteFromDatabase(String path) throws SQLException {
        logger.logNewWarning("Deleting image from database from " + path);
        String sql = "DELETE FROM " + table + " WHERE " + table + ".ImageID=" + findImage(path);
        statement = con.prepareStatement(sql);
        return !statement.execute();
    }

    public int findImage(String path) throws SQLException {
        logger.logNewInfo("Finding image from " + path);
        String sql = "SELECT * FROM " + table + "\n" +
                "WHERE " + table + ".Path" + " LIKE '%" + path + "%'";
        stmt = con.createStatement();
        resultSet = stmt.executeQuery(sql);
        if (!resultSet.next()) {
            return 0;
        } else {
            do {
                if (resultSet.getString(2).equalsIgnoreCase(path)) {
                    return resultSet.getInt(1);
                }
            } while (resultSet.next());
        }
        return 0;
    }

    /**
     * Must be called before calling any other method
     *
     * @throws SQLException
     */
    public boolean openConnection() throws SQLException {
        logger.logNewInfo("Opening connection to database..");
        if (!isConnection()) {
            con = Datasource.getConnection();
            return true;
        }
        return false;

    }

    /**
     * Must be called last after openConnection and any other method
     *
     * @throws SQLException
     */
    public boolean close() throws SQLException {
        boolean closed = false;
        if (con != null) {
            con.close();
            closed = true;
        }
        if (resultSet != null) {
            resultSet.close();
            closed = true;
        }
        if (stmt != null) {
            stmt.close();
            closed = true;
        }
        if (statement != null) {
            statement.close();
            closed = true;
        }
        return closed;

    }

    /**
     * This method closes the database and deletes the current table to free up space.
     *
     * @return
     * @throws SQLException
     */
    public boolean closeDatabase() throws SQLException {
        logger.logNewInfo("Closing database");
        if (openConnection()) {
            try {
                deleteTable();
            } catch (SQLException e) {
                logger.logNewFatalError(e.getLocalizedMessage());
            }
        }
        return close();
    }

    /**
     * columnname is the column the table is to be sorted by, the method then returns an arraylist with the paths in ascending or descending order
     *
     * @param columnName name of the column that is to be sorted by
     * @param ascending  boolean whether or smallest or the largest values are to be at the top
     * @return arraylist with the all of the paths in the database in correct order
     * @author Ingebrigt
     */
    public ArrayList<String> sortBy(String columnName, boolean ascending) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            if (isTableInDatabase()) {
                if (ascending) {
                    String sql = "SELECT " + " Path " + " from " + table + " ORDER BY " + columnName + " ASC";
                    statement = con.prepareStatement(sql);
                    resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        arrayList.add(resultSet.getString("Path"));
                    }
                    return arrayList;
                } else {
                    String sql = "SELECT " + "Path" + " from " + table + " ORDER BY " + columnName + " DESC";
                    statement = con.prepareStatement(sql);
                    resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        arrayList.add(resultSet.getString("Path"));
                    }
                    return arrayList;
                }
            }
        } catch (SQLException e) {
            logger.logNewFatalError(e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * removes all the tag in the given array
     *
     * @param path path of the file that you want to remove tags from
     * @param tags a string array with the tags you want to remove, not case-sensetive
     * @return boolean if removal was successful
     * @throws SQLException
     * @author Ingebrigt Hovind
     */
    public boolean removeTag(String path, String[] tags) throws SQLException {
        logger.logNewInfo("Removing tags from " + path);
        //gets all the tags for the specific path
        StringBuilder oldtags = getTags(path);
        //iterates through tags for the given image
        for (String string : tags) {
            //TODO fix bug where substrings of tags are also removed, e.g if carpet is a tag and you attempt to remove a "car" tag, then you will be left with "pet"
            int index = oldtags.toString().toLowerCase().indexOf(string.toLowerCase());
            //the conditionals are here to ensure that a single comma is left between each word
            //if the first word is selected, then no comma is removed
            //if the last word is selected then the last comma is selected
            //if a word between two others is selected then a single comma is removed
            oldtags.replace((index == 0) ? index : index - 1, (index == 0) ? index + string.length() + 1 : index + string.length(), "");
        }
        statement = con.prepareStatement("UPDATE fredrjul_ImageApp." + table + " SET fredrjul_ImageApp." + table + ".Tags = '" + oldtags + "' WHERE fredrjul_ImageApp." + table + ".ImageID = " + findImage(path));
        return !statement.execute();
    }

    /**
     * Searches the database and returns the paths of all elements that contain the given parameter in their metadata or tags
     *
     * @param searchFor the phrase you want to search for
     * @return an arraylist with the paths that contain data that mathch the search
     * @author Ingebrigt Hovind
     */
    public ArrayList<String> search(String searchFor, String searchIn) {
        ArrayList<String> searchResults = new ArrayList<>();
        try {
            logger.logNewInfo("Searching for matching values");
            //select paths where the search term is present in any column
            String sql = "SELECT * FROM " + table + " WHERE " + searchIn + " LIKE " + "'%" + searchFor + "%'";
            if (searchIn.equalsIgnoreCase("metadata")) {
                //TODO this is a pretty ghetto way of doing this, check if there's a better way of searching through these columns in the database
                sql = "SELECT * FROM " + table + " WHERE File_size LIKE " + "'%" + searchFor + "%' or DATE LIKE " + "'%" + searchFor + "%' or Height LIKE " + "'%" + searchFor + "%' or Width LIKE " + "'%" + searchFor + "%' or GPS_Longitude LIKE '%" + searchFor + "%' or GPS_Latitude LIKE '%" + searchFor + "%'";
            }
            statement = con.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                searchResults.add((String) resultSet.getObject("Path"));
            }
            return searchResults;
        } catch (Exception e) {
            logger.logNewFatalError(e.getLocalizedMessage());
        }
        return null;
    }

}