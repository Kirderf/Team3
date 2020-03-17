package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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
            String key = (mapElement.getKey().toString());
            Text name = new Text(mapElement.getKey().toString());
            name.setId(mapElement.getKey().toString());
            name.setFill(Paint.valueOf("#FFFFFF"));
            name.setLayoutX(25);
            name.setLayoutY(25);
            pane.getChildren().add(name);
            pane.setLayoutX(pane.getLayoutX());
            pane.setBackground(new Background(new BackgroundFill(Paint.valueOf("#000000"),CornerRadii.EMPTY, Insets.EMPTY)));
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
        ControllerMain.selectedImages = ControllerMain.albums.get(name);
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
