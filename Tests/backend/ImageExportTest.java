package backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import backend.util.ImageExport;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ImageExportTest {
    private String testPath1 = new String("resources/worldmap.png");
    private String testPath2 = new String("resources/samplephoto.jpg");
    private String testPath3 = new String("resources/flower.jpeg");
    private String gpsPath = "resources/images with gps data for testing/12382975864_09e6e069e7_o.jpg";
    private String pathToNonPhoto = this.getClass().getResource("/Views/Import.fxml").getPath();
    private ArrayList<String> nullList = new ArrayList<>();

    ArrayList<String> toBeExported = new ArrayList<>();
    ArrayList<String> emptyArrayList = new ArrayList<>();
    ArrayList<String> invalidPathList = new ArrayList<>();

    @BeforeEach
    void setUp(){
        toBeExported.add(testPath1);
        toBeExported.add(testPath2);
        toBeExported.add(testPath3);
        invalidPathList.add(testPath3);
        invalidPathList.add(pathToNonPhoto);
        nullList.add(null);
    }
    @Test
    void exportToPdf() throws IOException {
        String path = "resources/helloworld.pdf";
        String path2 = "resources/empty.pdf";
        String path3 = "resources/invalid.pdf";
        assertTrue(ImageExport.exportToPdf(path,toBeExported));
        assertTrue(new File(path).exists());
        new File(path).delete();
        assertThrows(ExceptionInInitializerError.class,()->ImageExport.exportToPdf(path2,emptyArrayList));
        assertFalse(new File(path2).exists());
        assertFalse(ImageExport.exportToPdf(path3,invalidPathList));
        assertFalse(new File(path3).exists());
        assertFalse(ImageExport.exportToPdf(path3,nullList));
        assertFalse(new File(path3).exists());
        assertFalse(ImageExport.exportToPdf(null,toBeExported));
        assertFalse(ImageExport.exportToPdf(null,null));
        assertFalse(ImageExport.exportToPdf(path3,null));

    }

}