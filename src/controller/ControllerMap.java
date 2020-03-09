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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;


public class ControllerMap implements Initializable {
    @FXML
    private ImageView world;
    @FXML
    private StackPane mapStackPane;

    public void initialize(URL location, ResourceBundle resources) {
        try {
            updateImage();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void updateImage() throws FileNotFoundException {

        Iterator hmIterator = ControllerMain.locations.entrySet().iterator();
        Stage stage = new Stage();
        Double[] latLong = new Double[2];
        while(hmIterator.hasNext()){
            Map.Entry mapElement = (Map.Entry)hmIterator.next();
            String latLongString =(String)mapElement.getValue();
            latLong[0] = Double.parseDouble(latLongString.split(",")[0]);
            latLong[1] = Double.parseDouble(latLongString.split(",")[1]);

            Image image = new Image(new FileInputStream((String)mapElement.getKey()));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(20);
            imageView.setFitHeight(20);
            mapStackPane.getChildren().add(imageView);
        }

        //stage.setScene(mapStackPane.getScene());
        //stage.show();
    }

}
