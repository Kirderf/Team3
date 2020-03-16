package controller;

import com.drew.metadata.heif.HeifBoxHandler;
import javafx.application.Platform;
import javafx.event.EventHandler;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.WildcardType;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

//TODO add eventlisteners to each image so that they can be clicked on
public class ControllerMap implements Initializable {
    @FXML
    private ImageView world;
    @FXML
    private StackPane mapStackPane;
    //width and height of the world stage
    //these start at 0, but longitude and latitude start at -180 and -90
    private final int HEIGHT = 340;
    private final int WIDTH = 616;
    public static ImageView clickedImage;
    public void initialize(URL location, ResourceBundle resources) {
        try {
            updateImage();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

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
                    ((ImageView) e.getSource()).toFront();
                    ((ImageView) e.getSource()).setFitHeight(60);
                    ((ImageView) e.getSource()).setFitWidth(60);
                }
            };
            EventHandler<MouseEvent> mouseOff = new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    ((ImageView) e.getSource()).toBack();
                    world.toBack();
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
}
