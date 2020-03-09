package backend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseClientTest {
    private DatabaseClient database;
    @BeforeEach
    void setUp() {
        database = new DatabaseClient();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void closeApplication() {
    }

    @Test
    void openApplication() {
    }

    @Test
    void addImage() {
    }

    @Test
    void removeImage() {
    }

    @Test
    void getMetaDataFromDatabase() {
        database.addImage(new File("C:/Users/Ingebrigt/Pictures/christ.jpg"));
        System.out.println(database.getMetaDataFromDatabase("sloth.png"));

    }

    @Test
    void addTag() {
    }
}