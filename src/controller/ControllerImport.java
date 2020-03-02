package controller;

import backend.DatabaseClient;
import com.drew.imaging.ImageProcessingException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerImport implements Initializable{
    /**
     * Container for textfields
     */
    @FXML
    private VBox pathVbox;
    /**
     * Scrollable container which includes the vbox
     */
    @FXML
    private ScrollPane scrollPane;
    /**
     * File explorer
     */
    private final FileChooser fc = new FileChooser();
    /**
     * List for containing file explorer results
     */
    private ArrayList<File> bufferList = new ArrayList<>();
    /**
     * List for containing temporary file explorer results
     */
    private List<File> list;
    private final double prefHeight = 27;
    private final double prefWidth = 330;
    public static boolean importSucceed = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setContent(pathVbox);
    }
    /**
     * Opens file chooser, and gets path, then displays it to the user.
     *
     * @param event button clicked
     */
    @FXML
    private void addImageFile(ActionEvent event) {
        fc.setTitle("Open Resource File");
        list = fc.showOpenMultipleDialog(scrollPane.getScene().getWindow());
        if (list != null) {
            list.forEach((x)->{
                if(!bufferList.contains(x)) bufferList.add(x);
            });
        }
        if (bufferList != null) {
            clearListView();
            for (File file : bufferList
            ) {
                generateTextField(file.getAbsolutePath());
            }
        }
    }

    /**
     * Closes the window
     *
     * @param event button clicked
     */
    @FXML
    private void cancel(ActionEvent event) {
        ((Stage) scrollPane.getScene().getWindow()).close();
    }

    @FXML
    private void clearAction(ActionEvent event) {
        clearListView();
        bufferList.clear();
    }

    /**
     * Creates a duplicate of a textfield and insert into scrollpane
     *
     * @param text input for textfields
     */
    @FXML
    private void generateTextField(String text) {
        TextField textField = new TextField(text);
        textField.setPrefHeight(prefHeight);
        textField.setPrefWidth(prefWidth);
        textField.setStyle("-fx-text-fill: black");
        textField.setDisable(true);
        pathVbox.getChildren().add(textField);
    }

    /**
     * Once all paths has been added to the list, add it to the database and display it in the MainView
     *
     * @param event button clicked
     */
    @FXML
    private void importAction(ActionEvent event) {
        if(bufferList != null) {
            for (File file : bufferList) {
                if (!ControllerMain.databaseClient.addedPathsContains(file.getPath())) {
                    ControllerMain.databaseClient.addImage(file);
                }
            }
            importSucceed = true;
        }
        cancel(event);
    }

    private void clearListView() {
        pathVbox.getChildren().clear();
    }
}

