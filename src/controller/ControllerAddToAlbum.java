package controller;

import backend.AlbumRow;
import backend.util.Log;
import backend.util.TagTableRow;
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

public class ControllerAddToAlbum implements Initializable {
    private static final Log logger = new Log();

    @FXML
    private TableView<AlbumRow> albumTable;
    @FXML
    private TableColumn<TagTableRow, CheckBox> select;
    @FXML
    private TableColumn<TagTableRow, Integer> id;
    ObservableList<AlbumRow> albumList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        insertAlbums();
    }

    /**
     * gets an iterator that iterates over the albums in controllerMain
     * @return Iterator on ControllerMain albums
     */
    private Iterator getAlbumIterator(){
        return ControllerMain.getAlbums().entrySet().iterator();
    }

    /**
     * inserts the album into the list of checkboxes
     */
    private void insertAlbums(){
        Iterator albumIterator = getAlbumIterator();
        int counter = 1;
        while(albumIterator.hasNext()){
            Map.Entry album = (Map.Entry)albumIterator.next();
            CheckBox ch = new CheckBox(""+(String)album.getKey());
            albumList.add(new AlbumRow(counter, "",ch));
            counter++;
        }
        albumTable.setItems(albumList);
        id.setCellValueFactory(new PropertyValueFactory<TagTableRow,Integer>("ID"));
        select.setCellValueFactory(new PropertyValueFactory<TagTableRow, CheckBox>("checkBox"));
    }

    /**
     * gets all of the albums that are currently checked
     * @return Arraylist with the names of the albums that the images were added
     */
    private ArrayList<String> getCheckedBoxes(){
        ArrayList<String> tempAlbumList = new ArrayList<>();
        for(AlbumRow ar : albumTable.getItems()){
            if(ar.getCheckBox().isSelected()){
                tempAlbumList.add(ar.getCheckBox().getText());
            }
        }
        return tempAlbumList;
    }

    /**
     * when confirm is clicked
     * @param event confirm button clicked
     */
    @FXML
    public void confirmAddToAlbum(ActionEvent event) {
        Stage thisStage = ((Stage) albumTable.getScene().getWindow());
        ArrayList<String> checkedBoxes = getCheckedBoxes();
        ArrayList<String> selectedImages = ControllerMain.getSelectedImages();
        int counter = 0;
        if(!checkedBoxes.isEmpty()) {

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
                ControllerMain.addPathsToAlbum(s,newImages);
            }
            if(counter != 0){
                logger.logNewInfo("Images added successfully to album");
                Alert a = new Alert(Alert.AlertType.INFORMATION, "The images were added successfully to the album");
                a.initModality(Modality.APPLICATION_MODAL);
                a.initOwner(thisStage);
                a.showAndWait();
                thisStage.close();
            }
            else{
                logger.logNewInfo("All the selected images were already in the selected album");
                Alert b = new Alert(Alert.AlertType.WARNING, "All the selected images were already present in the selected albums");
                b.initModality(Modality.APPLICATION_MODAL);
                b.initOwner(thisStage);
                b.showAndWait();
            }
        } else {
            logger.logNewInfo("no checkboxes selected");
            Alert c = new Alert(Alert.AlertType.WARNING, "No checkboxes selected");
            c.initModality(Modality.APPLICATION_MODAL);
            c.initOwner(thisStage);
            c.showAndWait();
            event.consume();
        }
    }
}
