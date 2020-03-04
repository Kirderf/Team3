package backend;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class ImageExport {
    /**
     * prints out an pdf of all the images in the path array
     * @param name the location, including the name, of where uou want to save it, in the format "C://Users/Ingebrigt/Pictures/helloworld.pdf"
     * @param paths array with the path that you want to print to a pdf
     * @return supposed to be true if successful, but this is not implemented yet
     * @author Ingebrigt Hovind
     */
    //stolen from https://stackoverflow.com/questions/22358478/java-create-pdf-pages-from-images-using-pdfbox-library
    public static boolean exportToPdf(String name, ArrayList<String> paths){
        PDDocument document = new PDDocument();
        try{
            for(String s : paths){
                InputStream in = new FileInputStream(s);
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
            document.save(name);
            document.close();
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
