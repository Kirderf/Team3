package backend.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * This class is used to export images in a pdf format.
 */
public final class ImageExport {
    private static final Log logger = new Log();


    private ImageExport(){
        //hide the implicit public constructor
    }
    /**
     * Saves all the given images in a pdf
     *
     * @param location where you want to save the pdf
     * @param paths List with the path that you want to save to the pdf
     * @return true if export is successful, false if not
     */
    public static boolean exportToPdf(String location, List<String> paths) throws IOException {
        logger.logNewInfo("ImageExport : " + "Exporting images to pdf");
        PDDocument document = new PDDocument();
        try {
            if (paths.isEmpty()) {
                //throw error here as it should not be possible for paths to be empty, as this is checked earlier
                throw new IllegalArgumentException("Empty list of paths");
            }
            //for the paths that are included in the album
            for (String s : paths) {
                InputStream in = new FileInputStream(s);
                //reads the image from disk
                BufferedImage bimg = ImageIO.read(in);
                float width = bimg.getWidth();
                float height = bimg.getHeight();
                //creates pdf page with the dimensions of the image
                PDPage page = new PDPage(new PDRectangle(width, height));
                //ads the page onto our pdf document
                document.addPage(page);
                //creates object to be drawn onto the pdf using our image and our pdf
                PDImageXObject img = PDImageXObject.createFromFile(s, document);
                //draws image onto the pdf page
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.drawImage(img, 0f, 0f);
                }
                //closes the inputstream for this photo
                in.close();
            }
            //saves the pdf document to the given location
            document.save(location);
            document.close();
            return true;
        } catch (IllegalArgumentException e) {
            document.close();
            logger.logNewWarning("illegalArgumentException in ImageExport " + e.getLocalizedMessage());
            return false;
        } catch (Exception e) {
            document.close();
            logger.logNewFatalError("ImageExport : " + e.getLocalizedMessage());
            return false;
        }
    }

}
