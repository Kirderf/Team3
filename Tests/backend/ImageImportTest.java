package backend;

import backend.util.ImageImport;
import com.drew.imaging.ImageProcessingException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ImageImportTest {
    //generally don't unit test private methods directly

    private ImageImport ImageImport;
    private String pathToNonPhoto = this.getClass().getResource("/Views/Import.fxml").getPath();
    private File testImage1 = new File("resources/worldmap.png");
    private File testImage2 = new File("resources/samplephoto.jpg");
    private File testImage3 = new File("resources/flower.jpeg");
    private File gpsImage = new File("resources/images with gps data for testing/12382975864_09e6e069e7_o.jpg");
    private File nonPhoto = new File(pathToNonPhoto);


    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }
    @Test
    void getMetaData() throws IOException, ImageProcessingException {
        assertNull(ImageImport.getMetaData((pathToNonPhoto)));
        assertNotNull(testImage1);
        assertNotNull(testImage2);
        assertNotNull(testImage3);
        for(String s : ImageImport.getMetaData(testImage1.getAbsolutePath())){
            assertNotNull(s);
        }
        for(String s : ImageImport.getMetaData(testImage2.getAbsolutePath())){
            assertNotNull(s);
        }
        for(String s : ImageImport.getMetaData(testImage3.getAbsolutePath())){
            assertNotNull(s);
        }
        //6 pieces of metadata
        assertEquals(ImageImport.getMetaData(testImage1.getAbsolutePath()).length,6);
        assertEquals(ImageImport.getMetaData(testImage2.getAbsolutePath()).length,6);
        assertEquals(ImageImport.getMetaData(testImage3.getAbsolutePath()).length,6);
        //longitude
        assertEquals(ImageImport.getMetaData(gpsImage.getAbsolutePath())[5],String.valueOf(0.13679166666666667));
        //latitude
        assertEquals(ImageImport.getMetaData(gpsImage.getAbsolutePath())[4],String.valueOf(50.81905277777778));
    }
}