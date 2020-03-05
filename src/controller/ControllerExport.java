package controller;

import backend.ImageExport;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.DirectoryChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ControllerExport {
    @FXML
    TextField inputText;

    public static boolean exportSucceed = false;

    private void exportPDF() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Views/Export.fxml"));
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose folder for album");
        File defaultDirectory = new File("C:/");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(null);
        if(ImageExport.exportToPdf(selectedDirectory.getPath() + inputText.getText() + ".pdf",ControllerMain.selectedImages)){
            exportSucceed = true;
        }

    }
}
