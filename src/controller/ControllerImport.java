package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ControllerImport {

    @FXML
    private Button select;

    @FXML
    private Button startImport;

    @FXML
    private HBox pathHbox;
    @FXML
    private TextField firstTextfield;
    @FXML
    private VBox pathVbox;

    public void select(ActionEvent event) {
        generateTextField("");
    }

    private void generateTextField(String text) {
        System.out.println("i work");
        TextField dupe = new TextField(text);
        dupe.setPrefHeight(firstTextfield.getHeight());
        dupe.setPrefWidth(firstTextfield.getWidth());
        dupe.setPadding(firstTextfield.getPadding());
        dupe.setDisable(true);
        pathVbox.getChildren().add(dupe);
    }
}
