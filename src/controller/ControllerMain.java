package controller;

import backend.DatabaseClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

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

    public static DatabaseClient databaseClient = new DatabaseClient();
    public static Stage importStage = new Stage();
    private int photoCount = 0;
    private int rowCount = 0;
    private int columnCount = 0;


    /**
     * Run 1 time once the window opens
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pictureGrid.setGridLinesVisible(false);
        importStage.initStyle(StageStyle.UNDECORATED);
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

    //TODO make search function work
    @FXML
    private void searchAction(ActionEvent event) {
        //TODO When clicked expand gridpane by a row
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
                refreshImages();
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
                System.out.println("Could not close application / delete table");
            }
            Stage stage = (Stage) pathDisplay.getScene().getWindow();
            stage.close();
            System.exit(0);
        }
    }

    /**
     * Gets images from database and inserts into UI.
     *
     * @param event
     */
    @FXML
    private void showImages(ActionEvent event) {
        refreshImages();
    }

    /**
     * Refresh Main UI
     */
    private void refreshImages() {
        try {
            for (Object obj : databaseClient.getData("Path")) {
                if (obj != null) {
                        insertImage((String) obj);
                }
            }
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert image into the gridpane
     *
     * @param path to image object
     */
    public void insertImage(String path) throws FileNotFoundException {
        pictureGrid.add(importImage(path), getNextColumn(), getNextRow());
        photoCount++;
    }

    /**
     * Add rows on the bottom of the gridpane
     *
     * @param numOfRows Number of rows added
     */
    private void addEmptyRow(int numOfRows) {
        double gridHeight = 0;
        for (int i = 0; i < numOfRows; i++) {
            gridHeight = (pictureGrid.heightProperty().divide(pictureGrid.getRowConstraints().size())).getValue();
            RowConstraints con = new RowConstraints();
            con.setPrefHeight(gridHeight);
            gridVbox.setPrefHeight(gridVbox.getHeight() + con.getPrefHeight());
            pictureGrid.getRowConstraints().add(con);
        }
    }

    /**
     * Take a path, and create a ImageView that fits to a space in the grid
     *
     * @param path to image
     */
    private ImageView importImage(String path) throws FileNotFoundException {
        ImageView imageView = new ImageView();
        imageView.setImage(new Image(new FileInputStream(path)));
        imageView.fitHeightProperty().bind(pictureGrid.heightProperty().divide(pictureGrid.getRowConstraints().size()));
        imageView.fitWidthProperty().bind(pictureGrid.widthProperty().divide(pictureGrid.getColumnConstraints().size()));
        imageView.setPreserveRatio(true);
        return imageView;
    }

}
