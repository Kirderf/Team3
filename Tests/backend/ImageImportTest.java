package backend;

import backend.ImageImport;
import com.drew.imaging.ImageProcessingException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ImageImportTest {
    //generally don't unit test private methods directly

    private ImageImport imageImport;
    private String pathToPhoto = this.getClass().getResource("/IMG_0963.JPG").getPath();
    private String pathToNonPhoto = this.getClass().getResource("/Views/Import.fxml").getPath();
    private File testImage1 = new File("resources/worldmap.png");
    private File testImage2 = new File("resources/samplephoto.jpg");
    private File testImage3 = new File("resources/flower.jpeg");
    private File gpsImage = new File("resources/images with gps data for testing/12382975864_09e6e069e7_o.jpg");
    private File nonPhoto = new File(pathToNonPhoto);


    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        imageImport = new ImageImport();
    }
    @Test
    void getMetaData() throws IOException, ImageProcessingException {
        assertNull(imageImport.getMetaData(new File(pathToNonPhoto)));
        assertNotNull(testImage1);
        assertNotNull(testImage2);
        assertNotNull(testImage3);
        for(String s : imageImport.getMetaData(testImage1)){
            assertNotNull(s);
        }
        for(String s : imageImport.getMetaData(testImage2)){
            assertNotNull(s);
        }
        for(String s : imageImport.getMetaData(testImage3)){
            assertNotNull(s);
        }
        //6 pieces of metadata
        assertEquals(imageImport.getMetaData(testImage1).length,6);
        assertEquals(imageImport.getMetaData(testImage2).length,6);
        assertEquals(imageImport.getMetaData(testImage3).length,6);
        //longitude
        assertEquals(imageImport.getMetaData(gpsImage)[5],String.valueOf(0.13679166666666667));
        //latitude
        assertEquals(imageImport.getMetaData(gpsImage)[4],String.valueOf(50.81905277777778));
    }
}