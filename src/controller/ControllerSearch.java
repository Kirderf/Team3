package controller;

import backend.Log;
import backend.TagTableRow;
import backend.util.TagTableRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private static final Log logger = new Log();

    private List<File> list;
    private final double prefHeight = 27;
    private final double prefWidth = 330;
    private static ArrayList<String> searchResults = new ArrayList<>();
    private static boolean searchSucceed = false;

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
    ObservableList<TagTableRow> observeList = FXCollections.observableArrayList();
    private List<File> list;

    static void notNamed(ArrayList<String> tagList, ObservableList<TagTableRow> observeList, TableView<TagTableRow> tagTable, TableColumn<TagTableRow, Integer> id, TableColumn<TagTableRow, CheckBox> select) {
        for (int i = 0; i < tagList.size(); i++) {
            String t = tagList.get(i);
            CheckBox ch = new CheckBox("" + t);
            observeList.add(new TagTableRow(i, "", ch));
        }


        tagTable.setItems(observeList);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        select.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            insertTags();
        } catch (SQLException e) {
            logger.logNewFatalError("ControllerSearch initialize " + e.getLocalizedMessage());
        }
    }

    public static ArrayList<String> getSearchResults() {
        return searchResults;
    }

    public static boolean isSearchSucceed() {
        return searchSucceed;
    }

    public static void setSearchSucceed(boolean searchSucceed) {
        ControllerSearch.searchSucceed = searchSucceed;
    }


    /**
     * if the close button is clicked
     * @param event
     */
    @FXML
    private void cancel(ActionEvent event) {
        ((Stage) searchField.getScene().getWindow()).close();
    }

    /**
     * if the search button is clicked
     * @param event the eventlistener on the button that called this method
     */
    @FXML
    private void searchAction(ActionEvent event) {
        //clears static resultList
        searchResults.clear();

        // Tags

        ArrayList<String> tempTagList = getCheckedBoxes();
        if (!tempTagList.isEmpty()) {
            for (String tag : tempTagList) {
                ArrayList<String> tagResult = ControllerMain.getDatabaseClient().search(tag, "Tags");
                if (tagResult != null) {
                    for (String s : tagResult) {
                        if (!searchResults.contains(s)) {
                            searchResults.add(s);
                        }
                    }
                }
            }
        }

        if (metaCheck.isSelected()) {
            ArrayList<String> metaResult = ControllerMain.getDatabaseClient().search(searchField.getText(), "Metadata");
            if (metaResult != null) {
                for (String s : metaResult) {
                    if (!searchResults.contains(s)) {
                        searchResults.add(s);
                    }
                }
            }
        }
        if (pathCheck.isSelected()) {
            ArrayList<String> pathResult = ControllerMain.getDatabaseClient().search(searchField.getText(), "Path");
            if (pathResult != null) {
                for (String s : pathResult) {
                    if (!searchResults.contains(s)) {
                        searchResults.add(s);
                    }
                }
            }
        }

        if(filenameCheck.isSelected()){
            //finds the matching paths
            ArrayList<String> filenameResult = ControllerMain.getDatabaseClient().search(searchField.getText(),"Path");
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
    @SuppressWarnings("Duplicates")
    protected void insertTags() throws SQLException {
        ArrayList<String> tagList = ControllerTagging.getAllTags();

        for (int i = 0; i < tagList.size(); i++) {
            String t = tagList.get(i);
            CheckBox ch = new CheckBox(""+t);
            observeList.add(new TagTableRow(i, "", ch));
        }


        tagTable.setItems(observeList);
        id.setCellValueFactory(new PropertyValueFactory<TagTableRow, Integer>("id"));
        select.setCellValueFactory(new PropertyValueFactory<TagTableRow, CheckBox>("checkBox"));
    }

    protected ArrayList<String> getCheckedBoxes(){
        ArrayList<String> tempTagList = new ArrayList<>();
        for(TagTableRow tb : tagTable.getItems()){
            if(tb.getCheckBox().isSelected()){
                tempTagList.add(tb.getCheckBox().getText());
            }
        }
        return tempTagList;
    }
}
