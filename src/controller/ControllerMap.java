package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;


public class ControllerMap implements Initializable {
    @FXML
    private ImageView world;
    @FXML
    private StackPane stackPane;

    public void initialize(URL location, ResourceBundle resources) {
        /*
        File file = new File("worldmap.png");
        Image image = new Image(file.toURI().toString());
        world.setImage(image);*/
    }
    public void updateImage(){

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException exc) {
                throw new Error("Unexpected interruption", exc);
            }
            Platform.runLater(() -> stackPane.getChildren().add(1,new Button("hei")));
        });
        thread.setDaemon(true);
        thread.start();

    }

}
