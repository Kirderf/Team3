package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
    TextField searchField;
    @FXML
    ListView tagsListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
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

    @FXML
    public void insertTags(){
        //  Use this when adding tags has been implemented
        //  ArrayList<String> tagList = ControllerMain.databaseClient.getData("Tags");
        ArrayList<String> tagList = new ArrayList<>();
        tagList.add("Tag1");
        tagList.add("Tag2");
        tagList.add("Tag3");

        //  TagList for testing
        ObservableList<String> observeList = FXCollections.observableArrayList(tagList);

        tagsListView = new ListView<String>(observeList);

                //Don't Like ListView, gonna experiment with checkboxes later|
    }
}
