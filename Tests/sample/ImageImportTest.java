package sample;

import backend.ImageImport;
import com.drew.imaging.ImageProcessingException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ImageImportTest {
    private ImageImport imageImport;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        imageImport = new ImageImport();
    }
    @Test
    void isImage() throws IOException{
        File testFile = new File(getClass().getResource("/IMG_0963.JPG").getPath());
        File testFile2 = new File(getClass().getResource("/samplephoto.jpg").getPath());
        System.out.println(testFile2.exists());
        assertFalse(imageImport.isImage(testFile));
        assertTrue(imageImport.isImage(testFile2));
    }
    @Test
    void getMetaData() throws IOException, ImageProcessingException {
        File testFile = new File("C:/Users/ingebrigt Hovind/Downloads/IMG_3405.JPG");
        for(String s : imageImport.getMetaData(testFile)){

            System.out.println(s);
        }
    }
    @Test
    void conMinutesToDecimal(){
        int degrees = 46;
        int minutes = 36;
        int seconds = 48;
        double dDegrees = degrees;
        double dMinutes = minutes;
        double dSeconds = seconds;
        String formattedMinutes = degrees + "° "+minutes+"' "+seconds + '"';
        //System.out.println(imageCentral.conMinutesToDecimal("46° 36' 48"));
        assertEquals(imageImport.conMinutesToDecimal(formattedMinutes), dDegrees + (dMinutes*60+dSeconds)/3600);
    }
    @Test
    void getExtensionFromFile(){
        File testFile = new File("C:/Users/Ingebrigt/Documents/adolf-hitler-biography.docx");
        File testFile2 = new File("C:/Users/Ingebrigt/Pictures/ong.jpg");
        assertEquals(imageImport.getExtensionFromFile(testFile),".docx");
        assertEquals(imageImport.getExtensionFromFile(testFile2),".jpg");
    }
}