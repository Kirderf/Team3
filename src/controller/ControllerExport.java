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
    //the text field where the user enters their desired pdf name
    @FXML
    javafx.scene.control.TextField inputText;

    public static boolean exportSucceed = false;
    public static boolean isExportSucceed(){
        return exportSucceed;
    }
    public static void setExportSucceed(boolean b){
        exportSucceed = b;
    }

    @FXML
    /**
     * opens an directory chooser which allows th euser to choose the placement of their new pdf
     */
    private void exportPDF() throws IOException {
        //chooses album location after selecting name
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose folder for album");
        //the directory that the file chooser starts in
        File defaultDirectory = new File("/");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(null);
        //gets the filename from the user and formats it correctly
        if(ImageExport.exportToPdf(selectedDirectory.getPath() +"/"+ inputText.getText() + ".pdf",ControllerMain.getSelectedImages())){
            exportSucceed = true;
        }
        //closes window
        closeWindow();
    }

    /**
     * closes the window
     */
    public void closeWindow(){
        ((Stage) inputText.getScene().getWindow()).close();
    }
    /**
     * if the cancel button is clicked
     */
    public void cancelExport(ActionEvent actionEvent) {
        closeWindow();
    }
}
