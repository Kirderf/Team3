package controller;

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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerMain implements Initializable {
    @FXML
    private Menu fileButton;

    @FXML
    private Menu importButton;

    @FXML
    private Menu openSearch;

    @FXML
    private MenuItem quitItem;

    @FXML
    private Menu returnToLibrary;

    @FXML
    private Menu helpButton;

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

    private Stage importStage = new Stage();
    private int photoCount = 0;
    private int rowCount = 0;
    private int columnCount = 0;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pictureGrid.setGridLinesVisible(false);
    }

    //TODO Make a method that creates a new Grid Pane every time the old one is full
    private int getNextRow(){
        if(photoCount == 0){
            return rowCount;
        }
        if((photoCount)%5 == 0){
            rowCount++;
            addEmptyRow(1);
        }
        return rowCount;
    }

    private int getNextColumn(){
        if(photoCount == 0){
            return columnCount++;
        }
        if(photoCount%5 == 0){
            columnCount = 0;
            return columnCount++;
        }
        return columnCount++;
    }

    @FXML
    private void searchAction(ActionEvent event) {
        //TODO When clicked expand gridpane by a row

    }
    @FXML
    private void importAction(ActionEvent event) throws IOException {
        if(!importStage.isShowing()) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/Import.fxml"));
                importStage.setScene(new Scene(root));
                importStage.setTitle("Import");
                importStage.setResizable(false);
                importStage.show();
            } catch (Exception exception) {
                System.out.println(exception.getLocalizedMessage());
            }
        }

        // sample photos for testing purposes
        String path = "/samplephoto.jpg";
        insertImage(importImage(path));
    }

    @FXML
    private void exportAction(ActionEvent event) {

    }

    @FXML
    private void quitAction(ActionEvent event) {
        Stage stage = (Stage) pathDisplay.getScene().getWindow();
        stage.close();
    }

    /**
     * Insert image into the gridpane
     * @param imageView ImageView object
     */
    private void insertImage(ImageView imageView) {
        pictureGrid.add(imageView, getNextColumn(), getNextRow());
        photoCount++;
        System.out.println(photoCount);
    }

    /**
     * Add rows on the bottom of the gridpane
     * @param numOfRows Number of rows added
     */
    private void addEmptyRow(int numOfRows) {
        double gridHeight = 0;
        for (int i = 0; i < numOfRows; i++) {
            gridHeight = (pictureGrid.heightProperty().divide(pictureGrid.getRowConstraints().size())).getValue();
            RowConstraints con = new RowConstraints();
            con.setPrefHeight(gridHeight);
            gridVbox.setPrefHeight(gridVbox.getHeight()+con.getPrefHeight());
            pictureGrid.getRowConstraints().add(con);
            System.out.println(gridHeight);
        }
    }

    /**
     * Take a path, and create a ImageView that fits to a space in the grid
     * @param path to image
     */
    private ImageView importImage(String path){
        ImageView imageView = new ImageView();
        imageView.setImage(new Image(getClass().getResourceAsStream(path)));
        imageView.fitHeightProperty().bind(pictureGrid.heightProperty().divide(pictureGrid.getRowConstraints().size()));
        imageView.fitWidthProperty().bind(pictureGrid.widthProperty().divide(pictureGrid.getColumnConstraints().size()));
        imageView.setPreserveRatio(true);
        return imageView;
    }


}
