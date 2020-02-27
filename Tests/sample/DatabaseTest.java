package sample;

import backend.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

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
            database.closeConnection();
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
            database.writeToDatabase("path to file","Tags",1000, Date.valueOf(LocalDate.of(2020,8,12)),2000,2000,11.02,13.09);
            database.closeConnection();
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
            database.writeToDatabase("path to file","Tags",1000, Date.valueOf(LocalDate.of(2020,8,12)),2000,2000,11.02,13.09);
            database.writeToDatabase("path to file2","Tags2",2000, Date.valueOf(LocalDate.of(2021,8,12)),2000,2000,11.02,13.09);

            list = database.readDatabase("File_size");
            for (Object obj :
                    list) {
                System.out.println(obj.toString());
            }
            database.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        ArrayList testArray = new ArrayList<>();
        testArray.add("1000");
        testArray.add("2000");
        assertArrayEquals(testArray.toArray(),list.toArray());
    }
}