package backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseClientTest {
    private DatabaseClient databaseClient = new DatabaseClient();
    private String pathToPhoto = this.getClass().getResource("/IMG_0963.JPG").getPath();
    private String pathToNonPhoto = this.getClass().getResource("/Views/Import.fxml").getPath();
    private String testPath1 = new String("resources/worldmap.png");
    private String testPath2 = new String("resources/samplephoto.jpg");
    private String testPath3 = new String("resources/flower.jpeg");
    private File testImage1 = new File(testPath1);
    private File testImage2 = new File(testPath2);
    private File testImage3 = new File(testPath3);
    private File gpsImage = new File("resources/images with gps data for testing/12382975864_09e6e069e7_o.jpg");
    private File nonPhoto = new File(pathToNonPhoto);

    @BeforeEach
    void setUp() throws SQLException {
        databaseClient.addImage(testImage1);
        databaseClient.addImage(testImage2);
        databaseClient.addImage(testImage3);
    }

    @Test
    void getColumn() throws SQLException {
        ArrayList<String> pathArrayList = new ArrayList<>();
        pathArrayList.add(testPath1);
        pathArrayList.add(testPath2);
        pathArrayList.add(testPath3);
        //sorts because order matters when comparing arraylists
        Collections.sort(pathArrayList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        assertEquals(databaseClient.getColumn("Path"),pathArrayList);
        assertEquals(databaseClient.getColumn("GPS_Longitude").size(),3);
        assertNull(databaseClient.getColumn("not a column"));
    }

    @Test
    void addImage() throws SQLException {
        //already contains this path
        assertFalse(databaseClient.addImage(testImage3));
        assertTrue(databaseClient.addImage(gpsImage));
        assertFalse(databaseClient.addImage(nonPhoto));
    }

    @Test
    void getMetaDataFromDatabase() {
        assertNull(databaseClient.getMetaDataFromDatabase(pathToNonPhoto));
        assertEquals(databaseClient.getMetaDataFromDatabase(testPath1).length,8);
        //checks file size
        assertEquals(databaseClient.getMetaDataFromDatabase(testPath1)[2],String.valueOf(124715));
        //this image is not in the database
        assertNull(databaseClient.getMetaDataFromDatabase(gpsImage.getPath().replaceAll("\\\\","/")));
    }
    @Test
    void getTag(){
        
    }

    @Test
    void addTag() throws SQLException {
        assertTrue(databaseClient.addTag(testPath1,new String[]{"home","away", "rockstar","last tag"}));
        assertFalse(databaseClient.addTag(testPath1,new String[]{"test tag 2", "tag with, comma"}));
        assertFalse(databaseClient.addTag(testPath2,new String[]{null}));
        assertFalse(databaseClient.addTag(testPath2,new String[]{"test tag 2","        "}));
        assertFalse(databaseClient.addTag(testPath2,new String[]{"test tag 2",""}));
        //this photo has not been added
        assertFalse(databaseClient.addTag(pathToPhoto,new String[]{"this", "photo"}));
        assertFalse(databaseClient.addTag(pathToNonPhoto,new String[]{"test tags","ajfjsa"}));
    }

    @Test
    void removeTag() throws SQLException {
        databaseClient.addTag(testPath1,new String[]{"home","away", "rockstar","last tag"});
        assertTrue(databaseClient.removeTag(testPath1,new String[]{"rockstar","last tag"}));
        assertEquals(databaseClient.getTags(testPath1),"home,away");
        assertTrue(databaseClient.removeTag(testPath1,new String[]{"home"}));
        assertEquals(databaseClient.getTags(testPath1),"away");
        //this tag has already been removed
        assertFalse(databaseClient.removeTag(testPath1,new String[]{"last tag"}));
        //testpath2 has no tags
        assertFalse(databaseClient.removeTag(testPath2,new String[]{"this image has no tags"}));
        assertFalse(databaseClient.removeTag(testPath2, new String[]{""}));
        //away should not be removed as the second tag is null
        assertFalse(databaseClient.removeTag(testPath1,new String[]{"away",null}));
        assertEquals(databaseClient.getTags(testPath1),"away");
    }

    @Test
    void search() {
    }

    @Test
    void sort() {
    }
}