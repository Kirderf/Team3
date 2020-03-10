package controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.*;

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
        setBigImage(imageBuffer);
        if (pathBuffer != null) {
            showMetadata(pathBuffer);
        }
    }

    @FXML
    private void goToLibrary(ActionEvent event) throws IOException {
        loadedFromAnotherLocation = true;
        bigImage.getScene().setRoot(FXMLLoader.load(getClass().getResource("/Views/Main.fxml")));
    }

    private void setBigImage(Image image) {
        bigImage.setImage(image);
        bigImage.fitWidthProperty().bind(bigImageGrid.widthProperty());
        bigImage.fitHeightProperty().bind(imageVbox.heightProperty());
        textField.setText(pathBuffer);
    }
}
