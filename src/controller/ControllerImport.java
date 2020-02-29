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
import java.util.List;
import java.util.ResourceBundle;

public class ControllerImport implements Initializable {

    @FXML
    private Button select;
    @FXML
    private Button startImport;
    @FXML
    private Button cancel;
    @FXML
    private TextField firstTextfield;
    @FXML
    private VBox pathVbox;
    @FXML
    private ScrollPane scrollPane;

    private int count = 0;
    private final FileChooser fc = new FileChooser();
    private List<File> list;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Select path for import
     * Currently able to create perfectly lined textfields
     * //TODO insert to database and select path from computer files
     *
     * @param event
     */
    @FXML
    private void select(ActionEvent event) {
        fc.setTitle("Open Resource File");
        list = fc.showOpenMultipleDialog(select.getScene().getWindow());
        if (list != null) { //if list is not empty, post result in a list in UI
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    firstTextfield.setVisible(true);
                    firstTextfield.setText(list.get(i).getAbsolutePath());
                } else {
                    generateTextField(list.get(i).getAbsolutePath());
                }
            }
        }
    }

    /**
     * Closes the window
     *
     * @param event
     */
    @FXML
    private void cancel(ActionEvent event) {
        ((Stage) cancel.getScene().getWindow()).close();
    }

    //Creates a duplicate of a textfield and insert into scrollpane
    @FXML
    private void generateTextField(String text) {
        TextField dupe = new TextField(text);
        dupe.setPrefHeight(firstTextfield.getHeight());
        dupe.setPrefWidth(firstTextfield.getWidth());
        dupe.setPadding(firstTextfield.getPadding());
        dupe.setBackground(firstTextfield.getBackground());
        dupe.setDisable(true);
        pathVbox.getChildren().add(dupe);
        scrollPane.setContent(pathVbox);
    }

    /**
     * Once all paths has been added to the list, add it to the database and display it in the MainView
     * @param event
     */
    @FXML
    private void importAction(ActionEvent event){
        try {
            for (File file: list) {
                ControllerMain.databaseClient.addImage(file);
            }
            ControllerMain.databaseClient.closeConnection();
        } catch (ImageProcessingException | IOException | SQLException e) {
            System.out.println("DEBUG importAction");
            e.printStackTrace();
        }

        cancel(event);
    }


}

