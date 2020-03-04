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

class ControllerMainTest {
    String[] testPaths;
    @BeforeEach
    void setUp(){
        testPaths = new String[]{"C://Users/Ingebrigt/Pictures/lengede.PNG","C://Users/Ingebrigt/Pictures/doomer.jpg"};
    }
    @Test
    void exportToPdf() {
        exportToPdf("faen.pdf",testPaths);
    }
    public boolean exportToPdf(String name, String[] paths){
        System.out.println("test");
        PDDocument document = new PDDocument();
        try{
            for(String s : paths){
                System.out.println("inne i for løkke");
                InputStream in = new FileInputStream(s);
                System.out.println("fileinput");
                BufferedImage bimg = ImageIO.read(in);
                float width = bimg.getWidth();
                float height = bimg.getHeight();
                PDPage page = new PDPage(new PDRectangle(width, height));
                document.addPage(page);
                PDImageXObject img = PDImageXObject.createFromFile(s,document);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.drawImage(img, 0f, 0f);
                contentStream.close();
                in.close();

            }
            document.save("C://Users/Ingebrigt/Pictures/helloworld.pdf");
            document.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


}