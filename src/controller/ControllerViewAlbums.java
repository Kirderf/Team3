package controller;

import backend.util.Log;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
    private static final Log logger = new Log();

    @FXML
    private MenuItem albumDelete;
    @FXML
    private TilePane albumTilePane;
    @FXML
    private VBox albumView;
    private static String albumToBeDeleted;
    private EventHandler clickedOn;
    private static boolean albumSelected = false;

    /**
     * adds all the albums to the view when the window is first opened
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Iterator albumIterator = ControllerMain.getAlbums().entrySet().iterator();
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

    /**
     * whether or not a specific album has been selected
     * @return boolean
     */
    public static boolean isAlbumSelected(){
        return albumSelected;
    }

    /**
     * Is used to change whether or not an album is selected
     * @param b boolean
     */
    public static void setAlbumSelected(boolean b){
        albumSelected = b;
    }

    /**
     * the name of the album that is to be deleted
     * @return String
     */
    static String getAlbumToBeDeleted(){
        return albumToBeDeleted;
    }

    /**
     * Changes the album that the user wants to delete
     * @param s the name of the new album that is to be deleted
     */
    static void setAlbumToBeDeleted(String s){
        albumToBeDeleted = s;
    }

    /**
     * when an album is clicked on, this shows it in the main view
     * @param name name of the album that the user wants to view
     */
    private void showAlbum(String name) {
        ControllerMain.clearSelectedImages();
        if(!ControllerMain.getAlbums().isEmpty()) {
            if(ControllerMain.getAlbums().get(name)!=null) {
                for(String s : ControllerMain.getAlbums().get(name)){
                    ControllerMain.addToSelectedImages(s);
                }
            }
            else{
                ControllerMain.clearSelectedImages();
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
                logger.logNewFatalError("ControllerViewAlbums deletePane " + e.getLocalizedMessage());
            }
        });
    }

    /**
     * when delete album is selected
     */
    public void deleteAction() {
        //iterates through all albums
        new Alert(Alert.AlertType.INFORMATION, "Select an album to delete").showAndWait();
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

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm deletion");
        alert.setHeaderText("Are you sure you want to delete this album?");
        alert.setContentText(getAlbumToBeDeleted());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            ControllerViewAlbums.deleteAlbum(getAlbumToBeDeleted());
            ControllerViewAlbums.setAlbumToBeDeleted("");
            addBackEvents();
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Album was not deleted").showAndWait();
            ControllerViewAlbums.setAlbumToBeDeleted("");
            addBackEvents();
            ControllerMain.clearSelectedImages();
            closeWindow();
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

    /**
     * removes the album from the hashmap in main
     * @param name
     */
    public static void deleteAlbum(String name){
        //deletes the selected album from the arraylist
        ControllerMain.removeAlbum(name);
    }
}
