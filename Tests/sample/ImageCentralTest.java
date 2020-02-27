package sample;

import backend.ImageCentral;
import com.drew.imaging.ImageProcessingException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageCentralTest {
    private ImageCentral imageCentral;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        imageCentral = new ImageCentral();
    }

    @org.junit.jupiter.api.Test
    void getAllImagesTest() throws IOException {
        ArrayList<File> files = new ArrayList<>(Arrays.asList(new File("./Tests/sample/ImageCentralTest.java")));
        //assertEquals(files.get(0), imageCentral.getAllImages("./Tests/sample").get(0));
    }
    @Test
    void getMetaData() throws IOException, ImageProcessingException {
        File testFile = new File("C:/Users/Ingebrigt/Downloads/IMG_3605.JPG");
        for(String s : imageCentral.getMetaData(testFile)){
            System.out.println(s);
        }
    }
    @Test
    void UTM2deg(){
        System.out.println(imageCentral.minutesToDecimal("46° 36' 47,98", "13° 50' 39,76")[1]);
    }

}