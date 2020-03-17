package controller;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerConfirmDeleteAlbum implements Initializable {
    @FXML
    private TextArea albumToBeDeleted;
    @FXML
    private static boolean stageClosed = false;
    static boolean isStageClosed(){
        return stageClosed;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //sets the text equal to the title
        albumToBeDeleted.setText(ControllerViewAlbums.getAlbumToBeDeleted());
    }

    public void cancelDelete(ActionEvent actionEvent) {
        //closes stage
        ((Stage) albumToBeDeleted.getScene().getWindow()).close();
        //the album to be deleted is set to nothing
        ControllerViewAlbums.setAlbumToBeDeleted("");
        stageClosed = true;
    }

    public void confirmDelete(ActionEvent actionEvent) {
        //deletes the album
        ControllerViewAlbums.deleteAlbum(albumToBeDeleted.getText());
        ((Stage) albumToBeDeleted.getScene().getWindow()).close();
        stageClosed = true;
    }
}
