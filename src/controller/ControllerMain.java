package controller;

import backend.DatabaseClient;
import backend.ImageExport;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.security.Key;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


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
    public static Stage exportStage = new Stage();
    public static Stage worldStage = new Stage();
    public static Image imageBuffer;
    private int photoCount = 0;
    private int rowCount = 0;
    private int columnCount = 0;
    private final double initialGridHeight = 185;
    public static boolean loadedFromAnotherLocation = false;
    public static ArrayList<String> selectedImages = new ArrayList<String>();
    private final FileChooser fc = new FileChooser();
    private static boolean ascending = true;
    long time1 = 0;
    long time2 = 0;
    long diff = 0;


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
        gridVbox.setStyle("-fx-border-color: black");
        if (loadedFromAnotherLocation) {
            databaseClient.clearPaths();
            refreshImages();
            loadedFromAnotherLocation = false;
        }
    }

    //for every 5th picture the row will increase in value
    private int getNextRow() {
        if (photoCount == 0) {
            return rowCount;
        }
        if ((photoCount) % 5 == 0) {
            rowCount++;
            if (pictureGrid.getRowConstraints().size() <= rowCount) {
                addEmptyRow();
            }
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
                    clearView();
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
                    logger.log(Level.INFO, "Refreshing");
                    refreshImages();
                    ControllerImport.importSucceed = false;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @FXML
    /**
     * sort the pictures based on the selected value in the drop down

     */
    private void sortAction(ActionEvent event) throws SQLException, FileNotFoundException {
        //if size is selected
        if (sortDropDown.getValue().toString().equalsIgnoreCase("Size")) {

            ArrayList<String> sortedList = databaseClient.sort("File_size", ascending);
            clearView();
            for (String s : sortedList) {
                insertImage(s);
            }
        }
        //if location is selected
        else if (sortDropDown.getValue().toString().equalsIgnoreCase("Location")) {
            //TODO make this work
            //I am thinking that we will sort based on the sum of latitude and longitude for the moment
        }
        //if path or date is selected
        else {
            //ascending changes value every time this function is called
            ArrayList<String> sortedList = databaseClient.sort(sortDropDown.getValue().toString(), ascending);
            clearView();
            for (String s : sortedList) {
                insertImage(s);
            }
        }
    }

    //TODO Export to pdf
    @FXML
    private void exportAction(ActionEvent event) throws IOException {
        if (!exportStage.isShowing()) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/Export.fxml"));
                exportStage.setScene(new Scene(root));
                exportStage.setTitle("Export");
                exportStage.setResizable(false);
                exportStage.showAndWait();
                //exportSucceed is a static variable in controllerExport
                if (ControllerExport.exportSucceed) {
                    System.out.println("Exporting ");
                    refreshImages();
                    ControllerExport.exportSucceed = false;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        clearSelection();
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
    private void goToLibrary() throws IOException {
        //pictureGrid.getScene().setRoot(FXMLLoader.load(getClass().getResource("/Views/Main.fxml")));
        //clears the selected images when you press the library button
        selectedImages.clear();
        refreshImages();
    }

    /**
     * Clears all rows on the gridView and creates a new one.
     */
    @FXML
    private void clearView() {
        rowCount = 0;
        columnCount = 0;
        photoCount = 0;
        pictureGrid.getChildren().clear();
        pictureGrid.setGridLinesVisible(true);
        databaseClient.clearPaths();
    }

    /**
     * Refresh Start UI
     */
    private void refreshImages() {
        try {
            clearView();
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
     */
    private void addEmptyRow() {
        double gridHeight = gridVbox.heightProperty().divide(pictureGrid.getRowConstraints().size()).getValue();
        RowConstraints con = new RowConstraints();
        con.setPrefHeight(gridHeight);
        gridVbox.setPrefHeight(gridVbox.getPrefHeight() + gridHeight);
        pictureGrid.getRowConstraints().add(con);
    }

    /**
     * tints the selected images blue
     *
     * @param image the image that you want to tint
     * @param color colour, this should be blue
     */
    private static void tint(BufferedImage image, Color color) {
        //stolen from https://stackoverflow.com/a/36744345
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color pixelColor = new Color(image.getRGB(x, y), true);
                int r = (pixelColor.getRed() + color.getRed()) / 2;
                int g = (pixelColor.getGreen() + color.getGreen()) / 2;
                int b = (pixelColor.getBlue() + color.getBlue()) / 2;
                int a = pixelColor.getAlpha();
                int rgba = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, rgba);
            }
        }
    }

    //removes the selected images
    private void clearSelection() {
        refreshImages();
        selectedImages.clear();
    }

    /**
     * Take a path, and create a ImageView that fits to a space in the grid
     *
     * @param path to image
     */
    private ImageView importImage(String path) throws FileNotFoundException {
        ImageView imageView = new ImageView();
        Image image = new Image(new FileInputStream(path));
        imageView.setImage(image);
        imageView.fitHeightProperty().bind(gridVbox.heightProperty().divide(pictureGrid.getRowConstraints().size()));
        imageView.fitWidthProperty().bind(gridVbox.widthProperty().divide(pictureGrid.getColumnConstraints().size()));
        imageView.setPreserveRatio(true);
        imageView.setOnMouseClicked(event -> {
            //first click in a series of 2 clicks
            if (time1 == 0) {
                time1 = System.currentTimeMillis();
                setSelectedImages(imageView, image, path);
            } else {
                time2 = System.currentTimeMillis();
                diff = time2-time1;
                //Checks for time between first click and second click, if time< 250 millis, it is a doubleclick
                if(diff < 250 && diff > 0) {
                    try {
                        time1 = 0;
                        clearSelection();
                        imageView.setImage(image);
                        showBigImage(imageView);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //otherwise the second click is registered as a single click
                    time1 = 0;
                    setSelectedImages(imageView, image, path);
                }
            }
        });
        return imageView;
    }

    private void setSelectedImages(ImageView imageView, Image image, String path) {
        if (!selectedImages.contains(path)) {
            selectedImages.add(path);
            //buff is the tinted
            BufferedImage buff = SwingFXUtils.fromFXImage(image, null);
            tint(buff, Color.blue);
            imageView.setImage(SwingFXUtils.toFXImage(buff, null));
        } else {
            selectedImages.remove(path);
            imageView.setImage(image);
        }
    }

    private void showBigImage(ImageView imageView) throws IOException {
        clearSelection();
        imageBuffer = imageView.getImage();
        Scene scene = pictureGrid.getScene();
        scene.setRoot(FXMLLoader.load(getClass().getResource("/Views/BigImage.fxml")));
    }

    private void getMetadata(Image image) {
        System.out.println(image.impl_getUrl());
        String[] metadata = databaseClient.getMetaDataFromDatabase(image.impl_getUrl());
        for (String string : metadata) {
            metadataVbox.getChildren().add(new Label(string));

        }
    }

    public void goToMap(ActionEvent actionEvent) throws IOException {
        Scene scene = pictureGrid.getScene();
        Parent root = FXMLLoader.load(getClass().getResource("/Views/World.fxml"));
        worldStage.setScene(new Scene(root));
        worldStage.setTitle("map of photos");
        worldStage.setResizable(false);
        worldStage.showAndWait();
    }
}
