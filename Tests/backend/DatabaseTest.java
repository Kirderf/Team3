package backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
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
    void init() {
        try {
            database.openConnection();
            database.createTable();
            database.closeDatabase();
        } catch (Exception e) {
            System.out.println("Error setting ut database");
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void writeToDatabase() {
        try {
            database.openConnection();
            database.createTable();
          //  database.addImageToTable("path to file", "Tags", 1000, Long.valueOf(LocalDate.of(2020, 8, 12)), 2000, 2000, 11.02, 13.09);
            database.closeDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void readDatabase() {
        ArrayList list = null;
        try {
            database.openConnection();
            database.createTable();
          //  database.addImageToTable("path to file", "Tags", 1000, Date.valueOf(LocalDate.of(2020, 8, 12)), 2000, 2000, 11.02, 13.09);
          //  database.addImageToTable("path to file2", "Tags2", 2000, Date.valueOf(LocalDate.of(2021, 8, 12)), 2000, 2000, 11.02, 13.09);

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
        try {
            database.openConnection();
            database.createTable();
         //   database.addImageToTable("path to file", "Tags", 1000, Date.valueOf(LocalDate.of(2020, 8, 12)), 2000, 2000, 11.02, 13.09);
       //     database.addImageToTable("path to file2", "Tags2", 2000, Date.valueOf(LocalDate.of(2021, 8, 12)), 2000, 2000, 11.02, 13.09);
            System.out.println(database.findImage("path to file2"));
            database.closeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteFromDatabase() throws SQLException {
        try {
            database.openConnection();
            database.createTable();
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
         //   database.addImageToTable("path to file", "Tags", 1000, Date.valueOf(LocalDate.of(2020, 8, 12)), 2000, 2000, 11.02, 13.09);
        //    database.addImageToTable("path to file2", "Tags2", 2000, Date.valueOf(LocalDate.of(2021, 8, 12)), 2000, 2000, 11.02, 13.09);
            System.out.println(Arrays.toString(database.getImageMetadata("path to file2")));
            database.closeDatabase();
        } catch (SQLException e) {
            database.closeDatabase();
            e.printStackTrace();
        }
    }

    @Test
    void isConnection() {
    }

    @Test
    void openConnection() {
        try {
            assertTrue(database.openConnection());
        }catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void closeConnection() {
    }
}