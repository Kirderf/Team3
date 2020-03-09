package controller;

import backend.DatabaseClient;
import com.drew.lang.annotations.NotNull;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ControllerBigImage extends ControllerMain implements Initializable {

    @FXML
    private ImageView bigImage;

    @FXML
    private GridPane bigImageGrid;
    /**
     * Run 1 time once the window opens
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setBigImage(imageBuffer);
        if (pathBuffer != null){
            showMetadata(pathBuffer);
        }


    }

    private void setBigImage(Image image) {
        bigImage.fitHeightProperty().bind(bigImageGrid.getRowConstraints().get(1).prefHeightProperty());
        bigImage.setImage(image);
    }

    @FXML
    private void goToLibrary(ActionEvent event) throws IOException {
        loadedFromAnotherLocation = true;
        bigImage.getScene().setRoot(FXMLLoader.load(getClass().getResource("/Views/Main.fxml")));
    }
}
