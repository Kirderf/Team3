package controller;

import backend.ImageExport;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ControllerExport {
    @FXML
    javafx.scene.control.TextField inputText;

    public static boolean exportSucceed = false;

    @FXML
    private void exportPDF() throws IOException {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose folder for album");
        File defaultDirectory = new File("C:/");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(null);
        if(ImageExport.exportToPdf(selectedDirectory.getPath() +"/"+ inputText.getText() + ".pdf",ControllerMain.selectedImages)){
            exportSucceed = true;
        }
        closeWindow();
    }
    public void closeWindow(){
        ((Stage) inputText.getScene().getWindow()).close();

    }
    public void cancelExport(ActionEvent actionEvent) {
        closeWindow();
    }
}
