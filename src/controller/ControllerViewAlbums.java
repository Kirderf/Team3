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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

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
            Text name = new Text(mapElement.getKey().toString());
            name.setFill(Paint.valueOf("#FFFFFF"));
            name.setLayoutX(25);
            name.setLayoutY(25);
            pane.getChildren().add(name);
            pane.setLayoutX(pane.getLayoutX());
            pane.setBackground(new Background(new BackgroundFill(Paint.valueOf("#000000"),CornerRadii.EMPTY, Insets.EMPTY)));
            EventHandler<MouseEvent> clickedOn = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    showAlbum(name.toString());
                }
            };
            pane.addEventHandler(MouseEvent.MOUSE_CLICKED,clickedOn);
            albumTilePane.getChildren().add(pane);
        }

    }
    /*
    //TODO check if scaling the image before adding it makes it look less pixelated
    public void updateImage() throws FileNotFoundException {
        //used to iterate through the images
        //TODO does this need to be a static hashmap? can it be a parameter in some way
        Iterator hmIterator = ControllerMain.locations.entrySet().iterator();
        //double array with longitude first, then latitude
        Double[] longLat = new Double[2];
        //iterates through hashmap with pictures that have valid gps data
        while(hmIterator.hasNext()){
            Map.Entry mapElement = (Map.Entry)hmIterator.next();
            String longLatString =(String)mapElement.getValue();
            //splits the string into its respective values
            longLat[0] = Double.parseDouble(longLatString.split(",")[0]);
            longLat[1] = Double.parseDouble(longLatString.split(",")[1]);
            //gets the image
            Image image = new Image(new FileInputStream((String)mapElement.getKey()));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(20);
            imageView.setFitHeight(20);
            //if this is one, then the placement is right on the edge of the map
            //TODO check edge cases when xration and yratio = 1
            double yRatio = longLat[1]/90;
            double xRatio = longLat[0]/180;
            //latitude, north south
            //the y value in javafx images works the opposite way of latitude, therefore the minus
            imageView.setTranslateY(-(HEIGHT/2*yRatio));
            //longitude, east west
            imageView.setTranslateX((WIDTH/2*xRatio));
            imageView.setId((String) mapElement.getKey());
            EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    Stage stage = (Stage) mapStackPane.getScene().getWindow();
                    clickedImage = (ImageView) e.getSource();
                    stage.close();

                }
            };
            EventHandler<MouseEvent> mouseOn = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    ((ImageView) e.getSource()).setFitHeight(60);
                    ((ImageView) e.getSource()).setFitWidth(60);
                }
            };
            EventHandler<MouseEvent> mouseOff = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    ((ImageView) e.getSource()).setFitHeight(20);
                    ((ImageView) e.getSource()).setFitWidth(20);

                }
            };

            imageView.addEventHandler(MouseEvent.MOUSE_ENTERED,mouseOn);
            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED,eventHandler);
            imageView.addEventHandler(MouseEvent.MOUSE_EXITED,mouseOff);
            //adds the image to where it is to be placed
            mapStackPane.getChildren().add(imageView);
        }
    }

     */
    private void showAlbum(String name){
        ControllerMain.selectedImages = ControllerMain.albums.get(name);
        albumSelected = true;
        ((Stage) albumTilePane.getScene().getWindow()).close();
    }
}
