package controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerMap {
    @FXML
    private ImageView world;

    public void initialize(URL location, ResourceBundle resources) {
        File file = new File("worldmap.png");
        Image image = new Image(file.toURI().toString());
        world.setImage(image);
    }

}
