package controller;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class ControllerViewAlbums implements Initializable {
    @FXML
    private TilePane albumTilePane;
    @FXML
    private VBox albumView;
    public static boolean albumSelected = false;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Iterator albumIterator = ControllerMain.albums.entrySet().iterator();
        // Iterate through the hashmap
        // and add some bonus marks for every student
        while(albumIterator.hasNext()){
            Map.Entry mapElement = (Map.Entry)albumIterator.next();
            Pane pane = new Pane();
            pane.setMaxHeight(100);
            pane.setMinWidth(100);
            pane.setMinHeight(100);
            pane.setMaxWidth(100);
            String key = (mapElement.getKey().toString());
            Text name = new Text(mapElement.getKey().toString());
            name.setFill(Paint.valueOf("#FFFFFF"));
            name.setLayoutX(25);
            name.setLayoutY(25);
            pane.getChildren().add(name);
            pane.setLayoutX(pane.getLayoutX());
            pane.setBackground(new Background(new BackgroundFill(Paint.valueOf("#000000"),CornerRadii.EMPTY, Insets.EMPTY)));
            EventHandler<MouseEvent> clickedOn = event -> showAlbum(key);
            pane.addEventHandler(MouseEvent.MOUSE_CLICKED,clickedOn);
            albumTilePane.getChildren().add(pane);
        }

    }
    //TODO check if scaling the image before adding it makes it look less pixelated
    private void showAlbum(String name){
        ControllerMain.selectedImages = ControllerMain.albums.get(name);
        albumSelected = true;
        ((Stage) albumTilePane.getScene().getWindow()).close();
    }
    public void closeWindow(){
        ((Stage) albumTilePane.getScene().getWindow()).close();
    }

}
