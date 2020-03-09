package backend;

import javafx.stage.FileChooser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

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
        File file = new File("resources/worldmap.png");
        assertTrue(database.addImage(file));
    }

    @Test
    void removeImage() {
    }

    @Test
    void getMetaDataFromDatabase() {

    }

    @Test
    void addTag() {
    }

}