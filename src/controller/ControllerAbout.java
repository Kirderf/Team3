package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for the "about" stage
 */
public class ControllerAbout implements Initializable {

    @FXML
    private ImageView logo;

    /**
     * Upon initialization update the logo
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logo.setImage(ControllerMain.appIcon);
    }
}
