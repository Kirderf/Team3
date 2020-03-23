package controller;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerBigImage extends ControllerMain implements Initializable {

    @FXML
    private ImageView bigImage;

    @FXML
    private GridPane bigImageGrid;

    @FXML
    private TextField textField;

    @FXML
    private VBox imageVbox;

    /**
     * Run 1 time once the window opens
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setBigImage(getImageBuffer());
        if (getPathBuffer() != null) {
            showMetadata(getPathBuffer());
        }
        textField.setEditable(false);
        addToSelectedImages(getPathBuffer());
    }

    @FXML
    /**
     * when go to library is pressed
     */
    private void goToLibrary(ActionEvent event) throws IOException {
        voice.speak("Going to library");
        bigImage.getScene().setRoot(FXMLLoader.load(getClass().getResource("/Views/Main.fxml")));
    }

    @FXML
    /**
     * add tag is clicked
     */
    private void addTagAction(){
        if(!addTagStage.isShowing()){
            try{
                Parent root = FXMLLoader.load(getClass().getResource("/Views/Tagging.fxml"));
                addTagStage.setScene(new Scene(root));
                addTagStage.setTitle("Tagging");
                addTagStage.setResizable(false);
                addTagStage.showAndWait();
            }catch (Exception exception){
                exception.printStackTrace();
            }
        }
    }

    @Override
    @FXML
    /**
     * when import image is clicked
     */
    protected void importAction(ActionEvent event) throws IOException {
        voice.speak("Importing");
        if (!importStage.isShowing()) {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Import.fxml"));
            importStage.setScene(new Scene(root));
            importStage.setTitle("Import");
            importStage.setResizable(false);
            importStage.showAndWait();
        }
    }

    /**
     * opens a image to fullscreen view
     * @param image the image that you want to show in fullscreen
     */
    private void setBigImage(Image image) {
        bigImage.setImage(image);
        bigImage.fitWidthProperty().bind(bigImageGrid.widthProperty());
        bigImage.fitHeightProperty().bind(imageVbox.heightProperty());
        textField.setText(getPathBuffer());
    }

    public void bigImageExport(ActionEvent actionEvent) {
        voice.speak("Exporting");
        Stage exportStage = new Stage();
        if (!exportStage.isShowing()) {
            exportStage.initModality(Modality.APPLICATION_MODAL);
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/Export.fxml"));
                exportStage.setScene(new Scene(root));
                exportStage.setTitle("Export");
                exportStage.setResizable(false);
                exportStage.showAndWait();
                //exportSucceed is a static variable in controllerExport
                if (ControllerExport.isExportSucceed()) {
                    ControllerExport.setExportSucceed(false);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
