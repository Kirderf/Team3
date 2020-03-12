package controller;

import backend.TableGetterSetter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
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
    CheckBox nameCheck;
    @FXML
    TextField searchField;
    @FXML
    ListView tagsListView;
    @FXML
    TableColumn<TableGetterSetter , String> tagName;
    @FXML
    TableColumn<TableGetterSetter , Integer> id;
    @FXML
    TableColumn<TableGetterSetter , CheckBox> select;
    @FXML
    TableView<TableGetterSetter> tagTable;


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

       insertTags();    //  for ScrollSearch


/*              Old tag search, uses the searchField
        if(tagCheck.isSelected()){
            ArrayList<String> tagResult = ControllerMain.databaseClient.search(searchField.getText(),"Tags");
            if (tagResult!=null) {
                for(String s : tagResult){
                    if(!searchResults.contains(s)){
                        searchResults.add(s);
                    }
                }
            }
        }

 */
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
        searchSucceed = true;
        cancel(event);
    }

    ObservableList<TableGetterSetter> observeList = FXCollections.observableArrayList();

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
            observeList.add(new TableGetterSetter(i, "", ch));
        }


        tagTable.setItems(observeList);
        id.setCellValueFactory(new PropertyValueFactory<TableGetterSetter, Integer>("id"));
        tagName.setCellValueFactory(new PropertyValueFactory<TableGetterSetter, String>("tagName"));
        select.setCellValueFactory(new PropertyValueFactory<TableGetterSetter, CheckBox>("checkBox"));
    }
}
