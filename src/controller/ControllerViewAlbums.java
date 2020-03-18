package controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class ControllerViewAlbums implements Initializable {
    @FXML
    private MenuItem albumDelete;
    @FXML
    private TilePane albumTilePane;
    @FXML
    private VBox albumView;
    private static String albumToBeDeleted;
    EventHandler clickedOn;
    private static boolean albumSelected = false;
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
            BufferedImage bufferedImage = new BufferedImage(100,100,TYPE_INT_ARGB);
            for (int x = 0; x<bufferedImage.getWidth(); x++) {
                for (int y = 0; y<bufferedImage.getHeight(); y++) {
                    if((x>5*bufferedImage.getWidth()/6||x<bufferedImage.getWidth()/6)||(y>5*bufferedImage.getHeight()/6||y<bufferedImage.getHeight()/6)) {
                        Color black = new Color(0, 0, 0);
                        bufferedImage.setRGB(x, y, black.getRGB());
                    }
                }
            }
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            ImageView testView = new ImageView();
            testView.setImage(image);
            String key = (mapElement.getKey().toString());
            Text name = new Text(mapElement.getKey().toString());
            name.setId(mapElement.getKey().toString());
            name.setFill(Paint.valueOf("#000000"));
            name.setLayoutX(25);
            name.setLayoutY(35);
            pane.getChildren().add(name);
            pane.getChildren().add(testView);
            pane.setLayoutX(pane.getLayoutX());

            clickedOn = event -> showAlbum(key);
            pane.addEventHandler(MouseEvent.MOUSE_CLICKED,clickedOn);
            albumTilePane.getChildren().add(pane);
        }

    }
    public static boolean isAlbumSelected(){
        return albumSelected;
    }
    public static void setAlbumSelected(boolean b){
        albumSelected = b;
    }
    static String getAlbumToBeDeleted(){
        return albumToBeDeleted;
    }
    static void setAlbumToBeDeleted(String s){
        albumToBeDeleted = s;
    }
    private void showAlbum(String name){
        if(!ControllerMain.albums.isEmpty()) {
            if(ControllerMain.albums.get(name)!=null) {
                ControllerMain.selectedImages = ControllerMain.albums.get(name);
            }
            else{
                ControllerMain.selectedImages.clear();
            }
        }
        albumSelected = true;
        ((Stage) albumTilePane.getScene().getWindow()).close();
    }
    public void closeWindow(){
        ((Stage) albumTilePane.getScene().getWindow()).close();
    }
    //this is the event handler that makes shows the deletion prompt
    public EventHandler deletePane(Pane pane){
        return ((EventHandler<MouseEvent>) event -> {
            try {
                deleteConfirmation(pane);
            } catch (IOException e) {
                //TODO change to logger
                e.printStackTrace();
            }
        });
    }
    public void deleteAction(ActionEvent actionEvent) {
        //iterates through all albums
        for(int i = 0; i<albumTilePane.getChildren().size();i++){
            //if it is a pane it is an album
            if(albumTilePane.getChildren().get(i) instanceof Pane){
                //gets the corrensponding album
                Pane pane = (Pane) albumTilePane.getChildren().get(i);
                //removes the eventhandler that shows the album when it is clicked on
                albumTilePane.getChildren().get(i).removeEventHandler(MouseEvent.MOUSE_CLICKED,clickedOn);
                //ads the event handler that confirms that the user wants to delete the album
                albumTilePane.getChildren().get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, deletePane(pane));
            }
        }
    }

    /**
     * shows delete confirmation window
     * @param pane the pane that you want to confirm deletion off
     * @throws IOException input of confirmation.fxml
     */
    private void deleteConfirmation(Pane pane) throws IOException {
        setAlbumToBeDeleted(pane.getChildren().get(0).getId());
        Stage confirmStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/Views/DeleteConfirmation.fxml"));
        confirmStage.setScene(new Scene(root));
        confirmStage.setTitle("Import");
        confirmStage.setResizable(false);
        confirmStage.showAndWait();
        //if the stage is closed
        if(ControllerConfirmDeleteAlbum.isStageClosed()){
            //the normal events are added back
            addBackEvents();
        }
    }

    /**
     * removes the deletion event and adds back the view album event
     */
    public void addBackEvents(){
        for(int i = 0; i<albumTilePane.getChildren().size();i++){
            if(albumTilePane.getChildren().get(i) instanceof Pane){
                Pane albumPane = (Pane) albumTilePane.getChildren().get(i);
                albumTilePane.getChildren().get(i).removeEventHandler(MouseEvent.MOUSE_CLICKED,deletePane(albumPane));
                albumTilePane.getChildren().get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, clickedOn);
            }
        }

    }
    //TODO change this to boolean
    public static void deleteAlbum(String name){
        //deletes the selected album from the arraylist
        ControllerMain.albums.remove(name);
    }
}
