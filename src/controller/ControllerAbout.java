package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class i a controller that handles actions made by
 * the user when interacting with the about stage.
 */
public class ControllerAbout implements Initializable {

    @FXML
    private ImageView logo;

    /**
     * Updates the logo when a stage using this controller is created
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logo.setImage(ControllerMain.appIcon);
    }
}
