package controller;

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

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerSearch implements Initializable {
    public static ArrayList<String> searchResults = new ArrayList<>();
    public static boolean searchSucceed = false;
    private final double prefHeight = 27;
    private final double prefWidth = 330;
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
        insertTags();
    }

    /**
     * if the close button is clicked
     *
     * @param event
     */
    @FXML
    private void cancel(ActionEvent event) {
        ((Stage) searchField.getScene().getWindow()).close();
    }

    /**
     * if the search button is clicked
     *
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

        if (filenameCheck.isSelected()) {
            //finds the matching paths
            ArrayList<String> filenameResult = ControllerMain.getDatabaseClient().search(searchField.getText(), "Path");
            if (filenameResult != null) {
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

    /**
     * Finds all available tags, assigns their labels and checkboxes and adds them to an observable
     * list, which is then inserted into a table list that's presented to the user.
     */
    @FXML
    protected void insertTags() {
        ArrayList<String> tagList = ControllerTagging.getAllTags();

        notNamed(tagList, observeList, tagTable, id, select);
    }

    protected ArrayList<String> getCheckedBoxes() {
        ArrayList<String> tempTagList = new ArrayList<>();
        for (TagTableRow tb : tagTable.getItems()) {
            if (tb.getCheckBox().isSelected()) {
                tempTagList.add(tb.getCheckBox().getText());
            }
        }
        return tempTagList;
    }
}
