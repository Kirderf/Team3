package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerAlbumNamePicker implements Initializable {
    @FXML
    private TextField inputText;
    /**
     * The name that the user picked
     */
    public static String savedName = "";

    /**
     * closes the current window
     */
    public void closeWindow(){
        ((Stage) inputText.getScene().getWindow()).close();
    }

    /**
     * gets the album name that was inputted
     * @return
     */
    public static String getSavedName(){
        return savedName;
    }
    public static void setSavedName(String s){
        savedName = s;
    }
    /**
     * if the cancel button is clicked
     */
    public void cancelExport(ActionEvent actionEvent) {
        closeWindow();
        savedName = "";
    }

    @Override
    /**
     * when the stage opens
     */
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * If the save album button is clicked
     */
    public void saveAlbum(ActionEvent actionEvent) {
        setSavedName(inputText.getText());
        closeWindow();
    }
}
