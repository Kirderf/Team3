package controller;

import backend.util.Text_To_Speech;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.awt.Color.white;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

/**
 * This class is a controller that handles actions made by the user
 * when interacting with the album stage.
 */
public class ControllerViewAlbums implements Initializable {
    private static String albumToBeDeleted;
    private static boolean albumSelected = false;
    private Text_To_Speech voice = Text_To_Speech.getInstance();

    @FXML
    private TilePane albumTilePane;
    private EventHandler<MouseEvent> clickedOn;

    /**
     * Whether or not a specific album has been selected
     *
     * @return boolean
     */
    static boolean isAlbumSelected() {
        return albumSelected;
    }

    /**
     * Is used to change whether or not an album is selected
     *
     * @param b boolean
     */
    static void setAlbumSelected(boolean b) {
        albumSelected = b;
    }

    /*
     * The name of the album that is to be deleted
     *
     * @return String
     */
    private static String getAlbumToBeDeleted() {
        return albumToBeDeleted;
    }

    /*
     * Changes the album that the user wants to delete
     *
     * @param s the name of the new album that is to be deleted
     */
    private static void setAlbumToBeDeleted(String s) {
        albumToBeDeleted = s;
    }

    /*
     * Removes the album from the hashmap in main
     *
     * @param name name of the album
     */
    private static void deleteAlbum(String name) {
        //deletes the selected album from the arraylist
        ControllerMain.removeAlbum(name);
    }

    /**
     * This method is called when a scene is created using this controller.
     * It adds all the albums into the tile pane.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Iterate through the hashmap
        // and add some bonus marks for every student
        for (Map.Entry<String, List<String>> mapElement : ControllerMain.getAlbums().entrySet()) {
            Pane pane = new Pane();
            pane.setMaxHeight(100);
            pane.setMinWidth(100);
            pane.setMinHeight(100);
            pane.setMaxWidth(100);
            BufferedImage bufferedImage = new BufferedImage(100, 100, TYPE_INT_ARGB);
            //adds black outline to each album
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                for (int y = 0; y < bufferedImage.getHeight(); y++) {
                    if ((x > 5 * bufferedImage.getWidth() / 6 || x < bufferedImage.getWidth() / 6) || (y > 5 * bufferedImage.getHeight() / 6 || y < bufferedImage.getHeight() / 6)) {
                        bufferedImage.setRGB(x, y, Color.black.getRGB());
                        //adds white outline
                        if (x == bufferedImage.getWidth() - 1 || y == bufferedImage.getHeight() - 1) {
                            bufferedImage.setRGB(x, y, white.getRGB());
                        }
                    }
                }
            }

            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            ImageView testView = new ImageView();
            testView.setImage(image);
            String key = (mapElement.getKey());
            //album name
            Text name = new Text(mapElement.getKey());
            //width before it goes to a new line
            name.setWrappingWidth(50);
            name.setId(mapElement.getKey());
            //black text
            name.setFill(Paint.valueOf("#000000"));
            //position
            name.setLayoutX(25);
            name.setLayoutY(35);
            //adds the outline and album name
            pane.getChildren().add(name);
            pane.getChildren().add(testView);
            pane.setLayoutX(pane.getLayoutX());

            clickedOn = event -> showAlbum(key);
            pane.addEventHandler(MouseEvent.MOUSE_CLICKED, clickedOn);
            albumTilePane.getChildren().add(pane);
        }

    }

    /*
     * When an album is clicked on, this shows it in the main view
     *
     * @param name name of the album that the user wants to view
     */
    private void showAlbum(String name) {
        ControllerMain.clearSelectedImages();
        if (!ControllerMain.getAlbums().isEmpty()) {
            if (ControllerMain.getAlbums().get(name) != null) {
                for (String s : ControllerMain.getAlbums().get(name)) {
                    ControllerMain.addToSelectedImages(s);
                }
            } else {
                ControllerMain.clearSelectedImages();
            }
        }
        albumSelected = true;
        ((Stage) albumTilePane.getScene().getWindow()).close();
    }

    private void closeWindow() {
        ((Stage) albumTilePane.getScene().getWindow()).close();
    }

    //this is the event handler that makes shows the deletion prompt
    private EventHandler<MouseEvent> deletePane(Pane pane) {
        return event -> deleteConfirmation(pane);
    }

    /**
     * This method is called when the user presses the 'Delete Album' button.
     * It deletes an album from application and database.
     */
    public void deleteAction() {
        voice.speak("Delete album");
        //iterates through all albums
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Select an album to delete");
        voice.speak("Click the album you want to delete");
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(ControllerMain.appIcon);
        alert.showAndWait();
        for (int i = 0; i < albumTilePane.getChildren().size(); i++) {
            //if it is a pane it is an album
            if (albumTilePane.getChildren().get(i) instanceof Pane) {
                //gets the corrensponding album
                Pane pane = (Pane) albumTilePane.getChildren().get(i);
                //removes the eventhandler that shows the album when it is clicked on
                albumTilePane.getChildren().get(i).removeEventHandler(MouseEvent.MOUSE_CLICKED, clickedOn);
                //ads the event handler that confirms that the user wants to delete the album
                albumTilePane.getChildren().get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, deletePane(pane));
            }
        }
    }

    /*
     * Shows delete confirmation window
     *
     * @param pane the pane that you want to confirm deletion off
     */
    private void deleteConfirmation(Pane pane) {
        setAlbumToBeDeleted(pane.getChildren().get(0).getId());

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(ControllerMain.appIcon);
        alert.setTitle("Confirm deletion");
        alert.setHeaderText("Are you sure you want to delete this album?");
        alert.setContentText(getAlbumToBeDeleted());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            voice.speak("Album was deleted");
            ControllerViewAlbums.deleteAlbum(getAlbumToBeDeleted());
            ControllerViewAlbums.setAlbumToBeDeleted("");
            addBackEvents();
        } else {
            voice.speak("Album was not deleted");
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Album was not deleted");
            ((Stage) alert1.getDialogPane().getScene().getWindow()).getIcons().add(ControllerMain.appIcon);
            alert.showAndWait();
            ControllerViewAlbums.setAlbumToBeDeleted("");
            addBackEvents();
            ControllerMain.clearSelectedImages();
            closeWindow();
        }
    }

    /*
     * removes the deletion event and adds back the view album event
     */
    private void addBackEvents() {
        for (int i = 0; i < albumTilePane.getChildren().size(); i++) {
            if (albumTilePane.getChildren().get(i) instanceof Pane) {
                Pane albumPane = (Pane) albumTilePane.getChildren().get(i);
                albumTilePane.getChildren().get(i).removeEventHandler(MouseEvent.MOUSE_CLICKED, deletePane(albumPane));
                albumTilePane.getChildren().get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, clickedOn);
            }
        }

    }
}
