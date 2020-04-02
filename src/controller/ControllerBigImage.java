package controller;


import backend.Text_To_Speech;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class ControllerBigImage extends ControllerMain implements Initializable {
    private Stage addTagStage = new Stage();
    private Stage importStage = new Stage();
    private Stage addToAlbumStage = new Stage();
    private Text_To_Speech voice = new Text_To_Speech();
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
                addTagStage.setOnCloseRequest(event -> ControllerTagging.bufferTags.clear());
                addTagStage.showAndWait();
            }catch (Exception exception){
                exception.printStackTrace();
            }
        }
    }


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
        bigImage.setPreserveRatio(true);
        bigImage.fitWidthProperty().bind(bigImageGrid.widthProperty());
        bigImage.fitHeightProperty().bind(imageVbox.heightProperty());
        bigImage.setImage(image);
        textField.setText(getPathBuffer());
    }
}
