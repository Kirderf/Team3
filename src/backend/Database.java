package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

//TODO: add javadoc

/**
 * @author Fredrik Julsen
 */
public class Database {
    private Random random = new Random();
    private int upperBound = 10000000;
    private String table = "fredrjul_" + random.nextInt(upperBound);
    private Connection con = null;
    private ResultSet resultSet = null;
    private PreparedStatement statement = null;
    private Statement stmt = null;

    /**
     * Constructor
     */
    public Database() {
        Log.logNewInfo("Database : Creating Database object");
        try {
            Log.logNewInfo("OpenConnection is "+ openConnection());
            Log.logNewInfo("Create table is "+ createTable());
            close();
        } catch (SQLException e) {
            Log.logNewFatalError("Database : " + e.getLocalizedMessage());
            System.exit(0);
        }
    }

    /**
     * This method is going to create a new database table and declare a variable to the rest of the class
     */
    private boolean regTable() throws SQLException {
        Log.logNewInfo("Database : " + table);
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
     * Adds the image path and the related data to a new row in the table
     * @param path Local path to image
     * @param tags This will be empty, as tags are empty when first registering
     * @param fileSize bytes of the image
     * @param date date the image was taken, or if that is not available, the last time it was edited
     * @param imageHeight height of the image in pixels
     * @param imageWidth Width of image in pixels
     * @param gpsLatitude latitude in decimals
     * @param gpsLongitude longitude in decimals
     * @return boolean if registration was successful
     * @throws SQLException
     */
    public boolean addImageToTable(String path, String tags, int fileSize, Long date, int imageHeight, int imageWidth, double gpsLatitude, double gpsLongitude) throws SQLException {
        Log.logNewInfo("Added Image to path" + path);
        String sql1 = "Insert into " + table + " Values(?,?,?,?,?,?,?,?,?)";
        statement = con.prepareStatement(sql1);
        statement.setNull(1, 0);
        statement.setString(2, path.replaceAll("\\\\", "/"));
        statement.setString(3, tags);
        statement.setInt(4, fileSize);
        statement.setLong(5, date);
        statement.setInt(6, imageHeight);
        statement.setInt(7, imageWidth);
        statement.setDouble(8, gpsLatitude);
        statement.setDouble(9, gpsLongitude);
        boolean result = !statement.execute();
        statement.close();
        return result;

    }

    /**
     * Gets all the tags related to the image path given
     * @param path the path to the image you want to get the tags from
     * @return StringBuilder with all the tags
     */
    public StringBuilder getTags(String path) throws SQLException {
        Log.logNewInfo("Database : " + "Getting Tags");
        if (!isPathInDatabase(path)) return null;
        String sql = "SELECT * FROM " + table + " WHERE " + table + ".ImageID = " + findImage(path);
        stmt = con.createStatement();
        resultSet = stmt.executeQuery(sql);
        if (resultSet.next()) {
            return new StringBuilder(resultSet.getString(3));
        }
        return new StringBuilder("");
    }

    /**
     * adds all all the tags to the specified picture
     *
     * @param path the picture that the tags are to be added to
     * @param tags arraylist with tags, the tags may not contain a comma
     * @return true if the tags were added successfully
     * @throws SQLException
     */
    public boolean addTags(String path, String[] tags) throws SQLException {
        if (!isPathInDatabase(path)) throw new IllegalArgumentException("Path is not in database");
        //tags are seperated by commas in the database, it is therefore not allowed to add a tag that contains a comma
        for (String s : tags) {
            if (s == null) throw new IllegalArgumentException("The string may not be null");
            if (s.replaceAll(" ", "").equals(""))
                throw new IllegalArgumentException("The string may not consist of only spaces");
            if (s.contains(",")) throw new IllegalArgumentException("The tag contains a comma, this is not allowed");
        }

        Log.logNewInfo("Database : " + "Adding Tags");
        StringBuilder oldtags = getTags(path);
        String tagTest = oldtags.toString().toLowerCase();
        for (String string : tags) {
            if (tagTest.contains(string.toLowerCase())){
                System.out.println("The image already has the tag: " + string);
                continue;
            }
            oldtags.append(",").append(string);
        }
        statement = con.prepareStatement("UPDATE fredrjul_ImageApp." + table + " SET fredrjul_ImageApp." + table + ".Tags = '" + oldtags + "' WHERE fredrjul_ImageApp." + table + ".ImageID = " + findImage(path));
        return !statement.execute();
    }

    /**
     * checks whether path is in database
     *
     * @param path the path you are searching for
     * @return boolean
     * @throws SQLException
     * @author Ingebrigt Hovind
     */
    public boolean isPathInDatabase(String path) throws SQLException {
        if (path == null) return false;
        String sql = "SELECT Path FROM " + table + " WHERE " + table + ".Path" + " LIKE '%" + path.replaceAll("\\\\", "/") + "%' LIMIT 1";
        statement = con.prepareStatement(sql);
        try (ResultSet rs = statement.executeQuery()) {
            return rs.next();
        } catch (Exception e) {
            Log.logNewFatalError(e.getLocalizedMessage());
        }
        return false;
    }

    /**
     * Reads the database table and finds the column with columnName. Then return a ArrayList with the elements.
     *
     * @param columnName
     * @return
     * @throws SQLException
     */
    public ArrayList getColumn(String columnName) throws SQLException {
        Log.logNewInfo("Database : " + "Getting Column");
        try {
            String sql = "Select " + columnName + " from " + table;
            statement = con.prepareStatement(sql);
            resultSet = statement.executeQuery();
            ArrayList arrayList = new ArrayList();
            while (resultSet.next()) {
                arrayList.add(resultSet.getObject(columnName));
            }
            return arrayList;
        } catch (Exception e) {
            Log.logNewFatalError(e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Get image metadata from path
     *
     * @param path Path to image
     * @return A String array of all parameters except ImageID
     * @throws SQLException
     */
    public String[] getImageMetadata(String path) throws SQLException {
        Log.logNewInfo("Database : " + "Getting ImageMetaData from " + path);
        String sql = "SELECT * FROM " + table + " WHERE " + table + ".Path" + " LIKE '%" + path.replaceAll("\\\\", "/") + "%' LIMIT 1";
        statement = con.prepareStatement(sql);
        try (ResultSet rs = statement.executeQuery()) {
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
    private boolean isTableInDatabase() throws SQLException {
        Log.logNewInfo("Database : " + "Checking if table is in the database");
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
        Log.logNewInfo("Database : " + "Checking database connection");
        if (con == null) {
            Log.logNewInfo("Database : " + "There is no connection");
            return false;
        }
        return !con.isClosed();
    }

    /**
     * @return
     * @throws SQLException
     */
    private boolean createTable() throws SQLException {
        Log.logNewInfo("Database : " + "Creating table");
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
        Log.logNewInfo("Database : " + "Deleting table");
        String sql = "DROP TABLE " + table;
        statement = con.prepareStatement(sql);
        return statement.execute();
    }

    /**
     * deletes a specified row from the database
     * @param path image that you want to delete
     * @return boolean if deletion was successful
     * @throws SQLException
     */
    public boolean deleteFromDatabase(String path) throws SQLException {
        Log.logNewWarning("Database : " + "Deleting image from database from " + path);
        String sql = "DELETE FROM " + table + " WHERE " + table + ".ImageID=" + findImage(path);
        statement = con.prepareStatement(sql);
        return !statement.execute();
    }

    /**
     * Finds index of an image in the database
     * @param path path of the image you want to find
     * @return index of the given image, -1 if it's not found
     * @throws SQLException
     */
    public int findImage(String path) throws SQLException {
        Log.logNewInfo("Database : " + "Finding image from " + path);
        String sql = "SELECT * FROM " + table + "\n" +
                "WHERE " + table + ".Path" + " LIKE '%" + path + "%'";
        stmt = con.createStatement();
        resultSet = stmt.executeQuery(sql);
        if (!resultSet.next()) {
            return -1;
        } else {
            do {
                if (resultSet.getString(2).equalsIgnoreCase(path)) {
                    return resultSet.getInt(1);
                }
            } while (resultSet.next());
        }
        return -1;
    }

    /**
     * Must be called before calling any other method
     * @return boolean if the opening was succesful
     * @throws SQLException
     */
    public boolean openConnection() throws SQLException {
        Log.logNewInfo("Database : " + "Opening connection to database..");
        if (!isConnection()) {
            con = DataSource.getConnection();
            return true;
        }
        return false;

    }

    /**
     * Closes connection to database
     * Must be called last after openConnection and any other method
     * @return boolean if closing was succesful
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
     * @return boolean if deletion was successful
     * @throws SQLException
     */
    public boolean closeDatabase() throws SQLException {
        Log.logNewInfo("Database : " + "Closing database");
        if (openConnection()) {
            try {
                deleteTable();
            } catch (SQLException e) {
                Log.logNewFatalError("Database : " + e.getLocalizedMessage());
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
        ArrayList<String> validColumns = new ArrayList<>();
        validColumns.add("Path");
        validColumns.add("tags");
        validColumns.add("File_size");
        validColumns.add("Date");
        validColumns.add("Heigth");
        validColumns.add("Width");
        validColumns.add("GPS_Latitude");
        validColumns.add("GPS_Longitude");
        if (!validColumns.contains(columnName)) {
            throw new IllegalArgumentException("Invalid column name");
        }
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
            Log.logNewFatalError("Database : " + e.getLocalizedMessage());
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
        Log.logNewInfo("Database : " + "Removing tags from " + path);
        if (!isPathInDatabase(path)) throw new IllegalArgumentException("The specified path is not in the databse");
        for (int i = 0; i < tags.length; i++) {
            if (tags[i] == null) throw new IllegalArgumentException("The string may not be null");
            if (tags[i].contains(","))
                throw new IllegalArgumentException("The tag contains a comma, this is not allowed");
            if (getTags(path).indexOf(tags[i]) < 0) {
                tags[i] = "";
            }

        }
        //gets all the tags for the specific path
        StringBuilder oldtags = getTags(path);
        boolean validTag = false;
        //iterates through tags for the given image
        for (String string : tags) {
            if (!string.equals("")) {
                validTag = true;
                //TODO fix bug where substrings of tags are also removed, e.g if carpet is a tag and you attempt to remove a "car" tag, then you will be left with "pet"
                int index = oldtags.toString().toLowerCase().indexOf(string.toLowerCase());
                //the conditionals are here to ensure that a single comma is left between each word
                //if the first word is selected, then no comma is removed
                //if the last word is selected then the last comma is selected
                //if a word between two others is selected then a single comma is removed
                oldtags.replace((index == 0) ? index : index - 1, (index == 0) ? index + string.length() + 1 : index + string.length(), "");
            }
        }
        statement = con.prepareStatement("UPDATE fredrjul_ImageApp." + table + " SET fredrjul_ImageApp." + table + ".Tags = '" + oldtags + "' WHERE fredrjul_ImageApp." + table + ".ImageID = " + findImage(path));
        return !statement.execute() && validTag;
    }

    /**
     * Searches for a specific term in database
     * @param searchFor the term you want to search for
     * @param searchIn the area you want to search in, either path or tags or metadata
     * @return arraylist with results, is empty in the case of no result or invalid inputdata
     */
    public ArrayList<String> search(String searchFor, String searchIn) {
        ArrayList<String> searchResults = new ArrayList<>();
        ArrayList<String> validColumns = new ArrayList<>();
        validColumns.add("Path");
        validColumns.add("Tags");
        validColumns.add("Metadata");
        if (!validColumns.contains(searchIn) || searchFor == null) return new ArrayList<>();
        try {
            Log.logNewInfo("Database : " + "Searching for matching values");
            //select paths where the search term is present in any column
            String sql = "SELECT * FROM " + table + " WHERE " + searchIn + " LIKE " + "'%" + searchFor + "%'";
            if (searchIn.equals("Metadata")) {
                sql = "SELECT * FROM " + table + " WHERE File_size LIKE " + "'%" + searchFor + "%' or DATE LIKE " + "'%" + searchFor + "%' or Height LIKE " + "'%" + searchFor + "%' or Width LIKE " + "'%" + searchFor + "%' or GPS_Longitude LIKE '%" + searchFor + "%' or GPS_Latitude LIKE '%" + searchFor + "%'";
            } else if (searchIn.equals("Tags")) {
                sql = "SELECT * FROM " + table + " WHERE Tags LIKE " + "'%" + searchFor + "%'";
            }
            statement = con.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                searchResults.add((String) resultSet.getObject("Path"));
            }
            return searchResults;
        } catch (Exception e) {
            Log.logNewFatalError("Database : " + e.getLocalizedMessage());
        }
        return null;
    }

}