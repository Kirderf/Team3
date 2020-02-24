package sample;

import backend.ImageCentral;
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
        assertEquals(files.get(0), imageCentral.getAllImages("./Tests/sample").get(0));
    }
}