package controller;

import backend.DatabaseClient;
import com.drew.imaging.ImageProcessingException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ControllerImport {

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
    /**
     * Select path for import
     * Currently able to create perfectly lined textfields
     * //TODO insert to database and select path from computer files
     *
     * @param event
     */
    public void select(ActionEvent event) {
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
    public void cancel(ActionEvent event) {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    //Creates a duplicate of a textfield and insert into scrollpane
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
    @FXML
    public void importAction(ActionEvent event){
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

