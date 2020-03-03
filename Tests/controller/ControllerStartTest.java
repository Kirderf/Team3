package controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class ControllerStartTest {
    String[] testPaths;
    @BeforeEach
    void setUp(){
        testPaths = new String[]{"C:/Users/Ingebrigt/Pictures/XenomorphXX121.png", "C:/Users/Ingebrigt/Pictures/15995674_606288529555941_2007948591_n.jpg"};
    }
    @Test
    void exportToPdf() {
        //exportToPdf("faen.pdf",testPaths);
    }

}