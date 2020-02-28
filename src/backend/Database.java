package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

//TODO: add javadoc

public class Database {
    Random random = new Random();
    int upperBound = 10000000;
    String table = "fredrjul_" + random.nextInt(upperBound);
    Connection con;

    /**
     * This method is going to create a new database table and declare a variable to the rest of the class
     */
    private boolean regTable() throws SQLException {
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
        return stmt.execute();
    }

    /**
     * This method is going to write data to the database
     * //TODO: Add parameter to method
     *
     * @param // data to add
     */
    public boolean writeToDatabase(String path, String tags, int file_size, Date date, int image_height, int image_wight, double GPS_Latitude, double GPS_Longitude) throws SQLException {
        openConnection();
        String sql1 = "Insert into " + table + " Values(?,?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql1);
        preparedStatement.setNull(1, 0);
        preparedStatement.setString(2, path);
        preparedStatement.setString(3, tags);
        preparedStatement.setInt(4, file_size);
        preparedStatement.setDate(5, date);
        preparedStatement.setInt(6, image_height);
        preparedStatement.setInt(7, image_wight);
        preparedStatement.setDouble(8, GPS_Latitude);
        preparedStatement.setDouble(9, GPS_Longitude);
        return preparedStatement.execute();

    }

    /**
     * TODO: fix method
     * @return
     * @throws SQLException
     */
    @Deprecated
    public String readDatabase() throws SQLException {
        openConnection();
        //TODO: Add method functionality
        String sql = "Select * from " + table;
        Statement stmt = con.createStatement();
        ResultSet result = stmt.executeQuery(sql);
        return result.toString();
    }

    /**
     * Reads the database table and finds the column with columnName. Then return a ArrayList with the elements.
     * @param columnName
     * @return
     * @throws SQLException
     */
    public ArrayList readDatabase(String columnName) throws SQLException {
        openConnection();
        String sql = "Select " + columnName + " from " + table;
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet result = stmt.executeQuery();
        ArrayList<Object> arrayList = new ArrayList();
        while (result.next()) {
            arrayList.add(result.getObject(columnName));
        }
        return arrayList;
    }
    public void getImageData(String path) throws SQLException{
        openConnection();
        String sql = "SELECT * FROM "+table+"\n" +
                "WHERE "+ table + ".Path" +" LIKE '%"+path+"%' LIMIT 1";
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        String[] returnValues = new String[8];
        returnValues[0] = rs.getString(2);
        returnValues[1] = rs.getString(3);
        returnValues[2] = String.valueOf(rs.getInt(4));
        returnValues[3] = rs.getDate(5).toString();
        returnValues[4] = String.valueOf(rs.getInt(6));



    }

    /**
     * Checks if a table is in a database
     *
     * @param tableName table to check
     * @return boolean
     * @throws SQLException
     */
    private boolean isTableInDatabase(String tableName) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM information_schema.tables WHERE table_schema = 'fredrjul_ImageApp' AND table_name = " + "\'" + table + "\'" +
                "");
        return stmt.executeQuery().next();
    }

    /**
     *
     * @return
     * @throws SQLException
     */
    public boolean createTable() throws SQLException {
        openConnection();
        while (isTableInDatabase(table)) {
            table = "fredrjul_" + random.nextInt(upperBound);
        }
        return regTable();

    }

    /**
     * Deletes a the current table from database
     * @return
     * @throws SQLException
     */
    public boolean deleteTable() throws SQLException {
        openConnection();
        String sql = "DROP TABLE "+ table;
        PreparedStatement stmt = con.prepareStatement(sql);
        return stmt.execute();
    }
    public boolean deleteFromDatabase(String path) throws SQLException {
        openConnection();
        String sql ="DELETE FROM "+table+" WHERE "+table+".ImageID="+findImage(path);
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        return !preparedStatement.execute();
    }
    public int findImage(String path) throws SQLException {
        openConnection();
        String sql = "SELECT * FROM "+table+"\n" +
                "WHERE "+ table + ".Path" +" LIKE '%"+path+"%'";
        Statement statement = con.createStatement();
        ResultSet rs =statement.executeQuery(sql);
        if (!rs.next()){
            return 0;
        }else{
            do {
                if(rs.getString(2).equalsIgnoreCase(path)){
                    return rs.getInt(1);
                }
            }while (rs.next());
        }
        return 0;
    }

    /**
     * Must be called before calling any other method
     *
     * @throws SQLException
     */
    public boolean openConnection() throws SQLException {
        if (con == null) {
            con = DriverManager.getConnection("jdbc:mysql://mysql.stud.ntnu.no:3306/fredrjul_ImageApp", "fredrjul_Image", "Password123");
        }else if (con.isClosed()){
            con = DriverManager.getConnection("jdbc:mysql://mysql.stud.ntnu.no:3306/fredrjul_ImageApp", "fredrjul_Image", "Password123");
        }
        return !con.isClosed();
    }

    /**
     * Must be called last after openConnection and any other method
     *
     * @throws SQLException
     */
    public boolean closeConnection() throws SQLException {
        con.close();
        return con.isClosed();
    }

    /**
     * This method closes the database and deletes the current table to free up space.
     * @return
     * @throws SQLException
     */
    public boolean closeDatabase() throws SQLException {
        if (con.isClosed()){
            return true;
        }
        deleteTable();
        closeConnection();
        return closeConnection();
    }

}