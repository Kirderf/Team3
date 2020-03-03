package controller;

import backend.DatabaseClient;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;


public class ControllerMain implements Initializable {
    @FXML
    private ScrollPane metadataScroll;

    @FXML
    private ScrollPane tagsScroll;

    @FXML
    private GridPane pictureGrid;

    @FXML
    private ComboBox<?> sortDropDown;

    @FXML
    private TextArea pathDisplay;

    @FXML
    private VBox gridVbox;

    @FXML
    private VBox metadataVbox;

    private static final Logger logger = Logger.getLogger(ControllerMain.class.getName());
    public static DatabaseClient databaseClient = new DatabaseClient();
    public static Stage importStage = new Stage();
    public static Stage searchStage = new Stage();
    public static Image imageBuffer;
    private int photoCount = 0;
    private int rowCount = 0;
    private int columnCount = 0;
    private final double initialGridHeight = 150;
    private ArrayList<RowConstraints> rows = new ArrayList<>();


    /**
     * Run 1 time once the window opens
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.log(Level.INFO, "Initializing");
        pictureGrid.setGridLinesVisible(true);
        pictureGrid.setPrefHeight(initialGridHeight);
        gridVbox.setPrefHeight(initialGridHeight);
        if (pictureGrid.getRowConstraints().size() == 1) {
            rows.add(pictureGrid.getRowConstraints().get(0));
        }
        gridVbox.setStyle("-fx-border-color: black");
    }

    //for every 5th picture the row will increase in value
    private int getNextRow() {
        if (photoCount == 0) {
            return rowCount;
        }
        if ((photoCount) % 5 == 0) {
            rowCount++;
            addEmptyRow(1);
        }
        return rowCount;
    }

    //for every 5th picture, the coloumn will reset. Gives the coloumn of the next imageview
    private int getNextColumn() {
        if (photoCount == 0) {
            return columnCount++;
        }
        if (photoCount % 5 == 0) {
            columnCount = 0;
            return columnCount++;
        }
        return columnCount++;
    }

    @FXML
    private void searchAction(ActionEvent event) throws IOException {
        logger.log(Level.INFO, "SearchAction");
        if (!searchStage.isShowing()) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/Search.fxml"));
                searchStage.setScene(new Scene(root));
                searchStage.setTitle("Search");
                searchStage.setResizable(false);
                searchStage.showAndWait();
                if (ControllerSearch.searchSucceed) {
                    //TODO fix bug where clearView makes application crash when searching
                    //clearView();
                    for (String s : ControllerSearch.searchResults) {
                        insertImage(s);
                        ControllerSearch.searchSucceed = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Opens import window, once window closes, all pictures from database will get inserted into the UI
     *
     * @param event user has clicked this item
     * @throws IOException Bad path input
     */
    @FXML
    private void importAction(ActionEvent event) throws IOException {
        if (!importStage.isShowing()) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/Import.fxml"));
                importStage.setScene(new Scene(root));
                importStage.setTitle("Import");
                importStage.setResizable(false);
                importStage.showAndWait();
                if (ControllerImport.importSucceed) {
                    refreshImages();
                    ControllerImport.importSucceed = false;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    //TODO Export to pdf
    @FXML
    private void exportAction(ActionEvent event) {

    }

    /**
     * Closes application, and closes connections to database. Cannot close if other windows are open
     *
     * @param event item is clicked
     * @throws SQLException Database error
     */
    @FXML
    private void quitAction(ActionEvent event) throws SQLException {
        if (importStage.isShowing()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Remember to close all other windows before exiting UwU");
            alert.showAndWait();
        } else {
            try {
                databaseClient.closeApplication();
            } catch (SQLException e) {
                e.printStackTrace();
                logger.log(Level.WARNING, "Could not close application / delete table");
            }
            Stage stage = (Stage) pathDisplay.getScene().getWindow();
            stage.close();
            System.exit(0);
        }
    }

    @FXML
    protected void goToLibrary() {
        logger.log(Level.WARNING, "works");
        ((Stage) metadataScroll.getScene().getWindow()).setScene(metadataScroll.getScene());
    }

    /**
     * Clears all rows on the gridView and creates a new one.
     */
    @FXML
    private void clearView() {
        for (RowConstraints r : rows
        ) {
            pictureGrid.getRowConstraints().remove(r);
        }
        gridVbox.setPrefHeight(0);
        rows.clear();
        addEmptyRow(1);
    }

    /**
     * Refresh Main UI
     */
    private void refreshImages() {
        try {
            for (Object obj : databaseClient.getData("Path")) {
                if (obj != null && !databaseClient.addedPathsContains((String) obj)) {
                    DatabaseClient.getAddedPaths().add((String) obj);
                    insertImage((String) obj);
                }
            }
        } catch (FileNotFoundException | SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    /**
     * Insert image into the gridpane
     *
     * @param path to image object
     */
    private void insertImage(String path) throws FileNotFoundException {
        pictureGrid.add(importImage(path), getNextColumn(), getNextRow());
        photoCount++;
    }

    /**
     * Add rows on the bottom of the gridpane
     *
     * @param numOfRows Number of rows added
     */
    private void addEmptyRow(int numOfRows) {
        double gridHeight;
        for (int i = 0; i < numOfRows; i++) {
            if (pictureGrid.getRowConstraints().size() == 0) {
                gridHeight = 150;
            } else {
                gridHeight = (pictureGrid.heightProperty().divide(pictureGrid.getRowConstraints().size())).getValue();
            }
            RowConstraints con = new RowConstraints();
            con.setPrefHeight(gridHeight);
            gridVbox.setPrefHeight(gridVbox.getPrefHeight() + con.getPrefHeight());
            pictureGrid.getRowConstraints().add(con);
            rows.add(con);
        }
    }

    /**
     * Take a path, and create a ImageView that fits to a space in the grid
     *
     * @param path to image
     */
    private ImageView importImage(String path) throws FileNotFoundException {
        ImageView imageView = new ImageView();
        imageBuffer = new Image(new FileInputStream(path));
        imageView.setImage(imageBuffer);
        imageView.fitHeightProperty().bind(pictureGrid.heightProperty().divide(pictureGrid.getRowConstraints().size()));
        imageView.fitWidthProperty().bind(pictureGrid.widthProperty().divide(pictureGrid.getColumnConstraints().size()));
        imageView.setPreserveRatio(true);
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                showBigImage(imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return imageView;
    }

    private void showBigImage(ImageView imageView) throws IOException {
        imageBuffer = imageView.getImage();
        ((Stage) metadataScroll.getScene().getWindow()).setScene(new Scene(FXMLLoader.load(getClass().getResource("/Views/BigImage.fxml"))));
    }

    private void getMetadata(Image image) {
        System.out.println(image.impl_getUrl());
        String[] metadata = databaseClient.getMetaDataFromDatabase(image.impl_getUrl());
        for (String string : metadata) {
            metadataVbox.getChildren().add(new Label(string));

        }
    }
    //TODO this should probably be placed in another class as it seems to me that it is more backend than frontend
    //TODO this writes blank pages to a pdf, not sure how to fix
    //stolen from https://stackoverflow.com/questions/22358478/java-create-pdf-pages-from-images-using-pdfbox-library
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
            document.save(name);
            document.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


}
