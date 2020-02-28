package sample;

import backend.ImageCentral;
import com.drew.imaging.ImageProcessingException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ImageCentralTest {
    private ImageCentral imageCentral;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        imageCentral = new ImageCentral();
    }
    @Test
    void isImage() throws IOException{
        File testFile = new File("C:/Users/Ingebrigt/Documents/adolf-hitler-biograph.docx");
        File testFile2 = new File("C:/Users/Ingebrigt/Pictures/ong.jpg");
        System.out.println(testFile2.exists());
        assertFalse(imageCentral.isImage(testFile));
        assertTrue(imageCentral.isImage(testFile2));
    }
    @Test
    void getMetaData() throws IOException, ImageProcessingException {
        File testFile = new File("C:/Users/Ingebrigt/Downloads/IMG_3605.JPG");
        for(String s : imageCentral.getMetaData(testFile)){
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
        String formattedMinutes = degrees + "° "+minutes+"' "+seconds;
        //System.out.println(imageCentral.conMinutesToDecimal("46° 36' 48"));
        assertEquals(imageCentral.conMinutesToDecimal(formattedMinutes), dDegrees + (dMinutes*60+dSeconds)/3600);
    }

}