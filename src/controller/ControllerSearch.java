package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;

import java.awt.*;
import java.awt.event.ActionEvent;
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

    @FXML
    CheckBox tagCheck;
    @FXML
    CheckBox metaCheck;
    @FXML
    CheckBox pathCheck;
    @FXML
    TextField searchField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    private void searchAction(ActionEvent event){

    }
    @FXML
    private void searchAction(javafx.event.ActionEvent actionEvent) throws SQLException {
        if(tagCheck.isSelected()){
            ArrayList<String> tagResult = ControllerMain.databaseClient.search(searchField.toString(),"Tags");
            for(String s : tagResult){
                if(!searchResults.contains(s)){
                    searchResults.add(s);
                }
            }
        }
        if(metaCheck.isSelected()){
            ArrayList<String> metaResult = ControllerMain.databaseClient.search(searchField.toString(),"Metadata");
            for(String s : metaResult){
                if(!searchResults.contains(s)){
                    searchResults.add(s);
                }
            }
        }
        if(pathCheck.isSelected()){
            ArrayList<String> pathResult = ControllerMain.databaseClient.search(searchField.toString(),"Path");
            for(String s : pathResult){
                if(!searchResults.contains(s)){
                    searchResults.add(s);
                }
            }
        }
    }
}
