package controller;

import backend.util.AlbumRow;
import backend.util.Log;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

/**
 * This class is a controller that handles actions made by the user
 * when adding images to an album in the add to album stage.
 */
public class ControllerAddToAlbum implements Initializable {
    private static final Log logger = new Log();
    private ObservableList<AlbumRow> albumList = FXCollections.observableArrayList();
    @FXML
    private TableView<AlbumRow> albumTable;
    @FXML
    private TableColumn<AlbumRow, CheckBox> select;

    /**
     * This method is called when a scene is created using this controller.
     * In this case, it finds all existing albums and creates an {@link AlbumRow} object
     * for each one, which is then inserted into a TableView.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        insertAlbums();
    }

    private Iterator<Map.Entry<String, List<String>>> getAlbumIterator() {
        return ControllerMain.getAlbums().entrySet().iterator();
    }

    private void insertAlbums() {
        Iterator<Map.Entry<String, List<String>>> albumIterator = getAlbumIterator();
        int counter = 1;
        while (albumIterator.hasNext()) {
            Map.Entry<String, List<String>> album = albumIterator.next();
            CheckBox ch = new CheckBox("" + album.getKey());
            albumList.add(new AlbumRow(counter, "", ch));
            counter++;
        }
        albumTable.setItems(albumList);
        select.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
    }

    private ArrayList<String> getCheckedBoxes() {
        ArrayList<String> tempAlbumList = new ArrayList<>();
        for (AlbumRow ar : albumTable.getItems()) {
            if (ar.getCheckBox().isSelected()) {
                tempAlbumList.add(ar.getCheckBox().getText());
            }
        }
        return tempAlbumList;
    }

    /**
     * This method is called when the confirm button is pressed.
     * It first checks if there are any checked boxes in the album TableView,
     * then iterates through these and adds the new images to each album.
     * If no checkboxes are checked, an alert is presented to the user instead.
     *
     * @param event confirm button clicked
     */
    @FXML
    public void confirmAddToAlbum(ActionEvent event) {
        Stage thisStage = ((Stage) albumTable.getScene().getWindow());
        ArrayList<String> checkedBoxes = getCheckedBoxes();
        ArrayList<String> selectedImages = ControllerMain.getSelectedImages();
        int counter = 0;
        int albumCounter = 0;
        if (!checkedBoxes.isEmpty()) {

            //iterates through albums
            for (String s : checkedBoxes) {
                ArrayList<String> newImages = new ArrayList<>();
                for (String se : selectedImages) {
                    if (!ControllerMain.getAlbums().get(s).contains(se)) {
                        newImages.add(se);
                        //counts the number of images that are added to albums
                        counter++;
                    }
                }
                if (counter == 0) {
                    albumCounter++;
                }
                counter = 0;
                ControllerMain.addPathsToAlbum(s, newImages);
            }
            if (albumCounter < checkedBoxes.size()) {
                logger.logNewInfo("Images added successfully to album");
                Alert a = new Alert(Alert.AlertType.INFORMATION, "The images were added successfully to the album");
                ((Stage)a.getDialogPane().getScene().getWindow()).getIcons().add(ControllerMain.appIcon);
                a.initModality(Modality.APPLICATION_MODAL);
                a.initOwner(thisStage);
                a.showAndWait();
                thisStage.close();
            } else {
                logger.logNewInfo("All the selected images were already in the selected album");
                Alert b = new Alert(Alert.AlertType.WARNING, "All the selected images were already present in the selected albums");
                ((Stage)b.getDialogPane().getScene().getWindow()).getIcons().add(ControllerMain.appIcon);
                b.initModality(Modality.APPLICATION_MODAL);
                b.initOwner(thisStage);
                b.showAndWait();
            }
        } else {
            logger.logNewInfo("No checkboxes selected");
            Alert c = new Alert(Alert.AlertType.WARNING, "No checkboxes selected");
            ((Stage)c.getDialogPane().getScene().getWindow()).getIcons().add(ControllerMain.appIcon);
            c.initModality(Modality.APPLICATION_MODAL);
            c.initOwner(thisStage);
            c.showAndWait();
            event.consume();
        }
    }
}
