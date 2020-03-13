package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerAlbumNamePicker implements Initializable {
    @FXML
    javafx.scene.control.TextField inputText;
    public static String savedName = "";
    public void closeWindow(){
        ((Stage) inputText.getScene().getWindow()).close();
    }
    /**
     * if the cancel button is clicked
     */
    public void cancelExport(ActionEvent actionEvent) {
        closeWindow();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void saveAlbum(ActionEvent actionEvent) {
        savedName = inputText.getText();
        closeWindow();
    }
}
