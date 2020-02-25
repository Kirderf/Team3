package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControllerImport {
    private int count = 0;
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

    /**
     * Select path for import
     * Currently able to create perfectly lined textfields
     * //TODO insert to database and select path from computer files
     * @param event
     */
    public void select(ActionEvent event) {
        if(count == 0) {
            firstTextfield.setVisible(true);
            firstTextfield.setText(""+count);
            count++;
        } else {
            generateTextField(""+count);
            count++;
        }
    }

    /**
     * Closes the window
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
}
