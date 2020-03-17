package controller;

import backend.TagTableRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.awt.Button;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerSearch implements Initializable {
    private List<File> list;
    private final double prefHeight = 27;
    private final double prefWidth = 330;
    public static ArrayList<String> searchResults = new ArrayList<>();
    public static boolean searchSucceed = false;

    @FXML
    CheckBox tagCheck;
    @FXML
    CheckBox metaCheck;
    @FXML
    CheckBox pathCheck;
    @FXML
    CheckBox filenameCheck;
    @FXML
    TextField searchField;
    @FXML
    TextField addTagField;
    @FXML
    Button addDone;
    @FXML
    TableColumn<TagTableRow, Integer> id;
    @FXML
    TableColumn<TagTableRow, CheckBox> select;
    @FXML
    TableView<TagTableRow> tagTable;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        insertTags();
    }

    @FXML
    private void cancel(ActionEvent event) {
        ((Stage) searchField.getScene().getWindow()).close();
    }

    @FXML
    private void searchAction(ActionEvent event) throws SQLException {
        //clears static resultList
        searchResults.clear();

        if(metaCheck.isSelected()){
            ArrayList<String> metaResult = ControllerMain.databaseClient.search(searchField.getText(),"Metadata");
            if (metaResult!=null) {
                for(String s : metaResult){
                    if(!searchResults.contains(s)){
                        searchResults.add(s);
                    }
                }
            }
        }
        if(pathCheck.isSelected()){
            ArrayList<String> pathResult = ControllerMain.databaseClient.search(searchField.getText(),"Path");
            if(pathResult!= null) {
                for (String s : pathResult) {
                    if (!searchResults.contains(s)) {
                        searchResults.add(s);
                    }
                }
            }
        }

        if(filenameCheck.isSelected()){
            //finds the matching paths
            ArrayList<String> filenameResult = ControllerMain.databaseClient.search(searchField.getText(),"Path");
            if(filenameResult!= null) {
                for (String s : filenameResult) {
                    //specifies that we only want the ones where the actual filename contains the search term
                    if (s.substring(s.lastIndexOf("/")).contains(searchField.getText())) {
                        searchResults.add(s);
                    }
                }
            }
        }


        searchSucceed = true;
        cancel(event);
    }

    ObservableList<TagTableRow> observeList = FXCollections.observableArrayList();

    /**
     * Finds all available tags, assigns their labels and checkboxes and adds them to an observable
     * list, which is then inserted into a table list that's presented to the user.
     */
    @FXML
    public void insertTags(){
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


        tagTable.setItems(observeList);
        id.setCellValueFactory(new PropertyValueFactory<TagTableRow, Integer>("id"));
        select.setCellValueFactory(new PropertyValueFactory<TagTableRow, CheckBox>("checkBox"));
    }
}
