package sample;

import backend.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.SQLOutput;

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
            database.regTable();
            database.openConnection();
        } catch (Exception e) {
            System.out.println("Error setting ut database");
            e.printStackTrace();
        }
    }

    @Test
    void writeToDatabase() {
    }

    @Test
    void readDatabase() {
    }
}