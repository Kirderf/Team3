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

public class ControllerImport {
    /**
     * Textfield for showing path
     */
    @FXML
    private TextField firstTextfield;
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
    private List<File> list;

    /**
     * Opens filechooser, and gets path, then displays it to the user.
     *
     * @param event button clicked
     */
    @FXML
    private void select(ActionEvent event) {
        fc.setTitle("Open Resource File");
        list = fc.showOpenMultipleDialog(scrollPane.getScene().getWindow());
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
     * @param event button clicked
     */
    @FXML
    private void cancel(ActionEvent event) {
        ((Stage) scrollPane.getScene().getWindow()).close();
    }

    /**
     * Creates a duplicate of a textfield and insert into scrollpane
     *
     * @param text input for textfields
     */
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
     *
     * @param event button clicked
     */
    @FXML
    private void importAction(ActionEvent event) {
        for (File file : list) {
                ControllerMain.databaseClient.addImage(file);
        }

        cancel(event);
    }

}

