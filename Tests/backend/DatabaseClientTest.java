package backend;

import javafx.stage.FileChooser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseClientTest {
    private DatabaseClient databaseClient;
    @BeforeEach
    void setUp() {
        databaseClient = new DatabaseClient();
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
        assertTrue(databaseClient.addImage(file));
    }

    @Test
    void removeImage() {
    }

    @Test
    void getMetaDataFromDatabase() {
        try {
            File file = new File("resources/worldmap.png");
            databaseClient.addImage(file);
            System.out.println(Arrays.toString(databaseClient.getMetaDataFromDatabase(file.getPath())));
            System.out.println((databaseClient.getColumn("File_size")).toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void addTag() {
    }

}