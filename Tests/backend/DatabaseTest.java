package backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {
    Database database;

    @BeforeEach
    void setUp() {
        database = new Database();
    }

    @Test
    void createTable() {
        try {
            database.openConnection();
            assertTrue(database.createTable());
            database.close();
        } catch (Exception e) {
            System.out.println("Error setting ut database");
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void writeToDatabase() {
        createTable();
        try {
            database.openConnection();
            //assertTrue(database.addImageToTable("path to file", "Tags", 1000, (long) 20200812, 2000, 2000, 11.02, 13.09));
           // assertTrue(database.addImageToTable("AAfekfj.jpg","Tags", 999,(long) 2015112,100,2555,11,13));
            database.closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    @Test
    void sort() {
        createTable();
        try {
            database.openConnection();
            assertTrue(database.addImageToTable("path to file", "Tags", 1000, (long) 20200812, 2000, 2000, 11.02, 13.09));
            assertTrue(database.addImageToTable("AAfekfj.jpg", "Tags", 999, (long) 2015112, 100, 2555, 11.5, 13.2));
            ArrayList<String> sortedImages = database.sortBy("Path", true);
            assertEquals(sortedImages.get(0), "AAfekfj.jpg");
            database.closeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

        @Test
    void readDatabase() {
        createTable();
        ArrayList list = null;
        try {
            database.openConnection();
            database.addImageToTable("path to file", "Tags", 1000, (long) 20200812, 2000, 2000, 11.02, 13.09);
            database.addImageToTable("path to file2", "Tags2", 2000, (long) 20210812, 2000, 2000, 11.02, 13.09);

            list = database.getColumn("File_size");
            for (Object obj :
                    list) {
                System.out.println(obj.toString());
            }
            database.closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        ArrayList testArray = new ArrayList<>();
        testArray.add("1000");
        testArray.add("2000");
        assertArrayEquals(testArray.toArray(), list.toArray());
    }

    @Test
    void findImage() {
        createTable();
        try {
            database.openConnection();
            database.addImageToTable("path to file", "Tags", 1000, (long) 20200812, 2000, 2000, 11.02, 13.09);
            database.addImageToTable("path to file2", "Tags2", 2000, (long) 20210812, 2000, 2000, 11.02, 13.09);
            System.out.println(database.findImage("path to file2"));
            database.closeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteFromDatabase() throws SQLException {
        createTable();
        try {
            database.openConnection();
            database.addImageToTable("path to file", "Tags", 1000, (long) 20200812, 2000, 2000, 11.02, 13.09);
            database.addImageToTable("path to file2", "Tags2", 2000, (long) 202812, 2000, 2000, 11.02, 13.09);
            System.out.println(database.findImage("path to file2"));
            System.out.println(database.deleteFromDatabase("path to file2"));
            database.closeDatabase();
        } catch (SQLException e) {
            database.closeDatabase();
            e.printStackTrace();
        }
    }

    @Test
    void getImageMetadata() throws SQLException {
        try {
            database.openConnection();
            database.createTable();
            database.addImageToTable("path to file", "Tags", 1000,20200812L, 2000, 2000, 11.02, 13.09);
            database.addImageToTable("path to file2", "Tags2", 2000, 20210812L, 2000, 2000, 11.02, 13.09);
            System.out.println(Arrays.toString(database.getImageMetadata("path to file2")));
            database.closeDatabase();
        } catch (SQLException e) {
            database.closeDatabase();
            e.printStackTrace();
        }
    }


    @Test
    void openConnection() {
        try {
            assertTrue(database.openConnection());
            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void closeConnection() {
        try {
            if (!database.isConnection()) {
                database.openConnection();
            }
            assertTrue(database.close());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void isConnection() {
        try {
            database.openConnection();
            assertTrue(database.isConnection());
            closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    void closeDatabase() {
    }

    @Test
    void addTags() {

        writeToDatabase();
        try {
            database.openConnection();
            assertTrue(database.addTags("path to file",new String[]{"String","test"}));
            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }
    @Test
    void removeTags() {
        writeToDatabase();
        try {
            database.openConnection();
            database.addTags("path to file",new String[]{"String","test","test5"});
            assertTrue(database.removeTag("path to file", new String[]{"test","test5"}));
            assertEquals(database.getTags("path to file").toString(),"Tags,String");
            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }
}