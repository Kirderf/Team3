package backend;
import java.sql.*;
import java.util.Random;

//TODO: add javadoc

public class Database {
    Random random = new Random();
    String table = "fredrjul_"+random.nextInt(10000000);

    /**
     * This method is going to create a new database table and declare a variable to the rest of the class
     *
     */
    public void init() {
        try{
            Class.forName("com.mysql.jdbc.Driver"); // Java database driver for mySql
            // Url to database, username and password to setup connection to database
            Connection con=DriverManager.getConnection("jdbc:mysql://mysql.stud.ntnu.no:3306/fredrjul_ImageApp","fredrjul_Image","Password123");
            Statement stmt=con.createStatement();
            stmt.executeQuery("CREATE TABLE "+ table);
            con.close();
        }catch(Exception e){
            System.out.println("Something is wrong with database setup");
            e.printStackTrace();}
    }

    /** This method is going to write data to the database
     * //TODO: Add parameter to method
     * @param  // data to add
     */
    public void writeToDatabase(){


    }

    /**
     *
     */
    public void readDatabase(){
    //TODO: Add method functionality
    }
    private boolean isTableInDatabase(String databaseName){
        return false;
    }
}
