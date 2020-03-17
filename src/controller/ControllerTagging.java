package controller;

import backend.TagTableRow;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControllerTagging implements Initializable {
    @FXML
    private Button taggingAdd;

    @FXML
    private Button taggingRemove;

    @FXML
    private Button taggingCancel;

    @FXML
    private Button taggingDone;

    @FXML
    TableView<TagTableRow> taggingTable;
    @FXML
    TableColumn<TagTableRow, Integer> id;
    @FXML
    TableColumn<TagTableRow, CheckBox> select;


    public void initialize(URL location, ResourceBundle resources) {
        insertTags();
    }

    ObservableList<TagTableRow> observeList = FXCollections.observableArrayList();

    /**
     * Finds all available tags, assigns their labels and checkboxes and adds them to an observable
     * list, which is then inserted into a table list that's presented to the user.
     */
    @FXML
    protected void insertTags(){
        //  Use this when adding tags has been implemented
        //  ArrayList<String> tagList = ControllerMain.databaseClient.getData("Tags");
        ArrayList<String> tagList = new ArrayList<>();
        tagList.add("Tag1");
        tagList.add("Tag2");
        tagList.add("Tag3");

        for (int i = 0; i < tagList.size(); i++) {
            String t = tagList.get(i);
            CheckBox ch = new CheckBox(""+t);
            observeList.add(new TagTableRow(i, "", ch));
        }


        taggingTable.setItems(observeList);
        id.setCellValueFactory(new PropertyValueFactory<TagTableRow, Integer>("id"));
        select.setCellValueFactory(new PropertyValueFactory<TagTableRow, CheckBox>("checkBox"));
    }

}
