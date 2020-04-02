package controller;

import backend.AlbumRow;
import backend.TagTableRow;
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
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

public class ControllerAddToAlbum implements Initializable {
    @FXML
    private TableView<AlbumRow> albumTable;
    @FXML
    private TableColumn<TagTableRow, CheckBox> select;
    @FXML
    private TableColumn<TagTableRow, Integer> id;
    ObservableList<AlbumRow> albumList = FXCollections.observableArrayList();

    public void searchAction(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            insertAlbums();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private Iterator getAlbumIterator(){
        return ControllerMain.getAlbums().entrySet().iterator();
    }
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
    private ArrayList<String> getCheckedBoxes(){
        ArrayList<String> tempAlbumList = new ArrayList<>();
        for(AlbumRow ar : albumTable.getItems()){
            if(ar.getCheckBox().isSelected()){
                tempAlbumList.add(ar.getCheckBox().getText());
            }
        }
        return tempAlbumList;
    }

    public void confirmAddToAlbum(ActionEvent actionEvent) {
        ArrayList<String> checkedBoxes = getCheckedBoxes();
        ArrayList<String> selectedImages = ControllerMain.getSelectedImages();
        int counter = 0;
        for(String s : checkedBoxes){
            for(String se : selectedImages){
                if(!ControllerMain.getAlbums().get(s).contains(se)){
                    ControllerMain.getAlbums().get(s).add(se);
                    counter++;
                }
            }
        }
        if(counter != 0){
            new Alert(Alert.AlertType.INFORMATION, "The images were added successfully to the album").showAndWait();
        }
        else{
            new Alert(Alert.AlertType.INFORMATION, "All the selected images were already present in the selected albums").showAndWait();
        }
        ((Stage) albumTable.getScene().getWindow()).close();
    }
}
