package controller;

import backend.util.TagTableRow;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ControllerSearch implements Initializable {
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
    TableColumn<TagTableRow, Integer> id;
    @FXML
    TableColumn<TagTableRow, CheckBox> select;
    @FXML
    TableView<TagTableRow> tagTable;
    ObservableList<TagTableRow> observeList = FXCollections.observableArrayList();

    public static ArrayList<String> getSearchResults() {
        return searchResults;
    }

    public static boolean isSearchSucceed() {
        return searchSucceed;
    }

    public static void setSearchSucceed(boolean searchSucceed) {
        ControllerSearch.searchSucceed = searchSucceed;
    }

    private ChangeListener<Boolean> enableSearchField() {
        return ((observable, oldValue, newValue) -> {
            if (newValue) {
                searchField.setDisable(false);
            } else {
                if ((!pathCheck.isSelected() && !metaCheck.isSelected() && !filenameCheck.isSelected())) {
                    searchField.setDisable(true);
                }
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchField.setDisable(true);
        pathCheck.selectedProperty().addListener(enableSearchField());
        metaCheck.selectedProperty().addListener(enableSearchField());
        filenameCheck.selectedProperty().addListener(enableSearchField());
        insertTags();
    }

    /**
     * if the close button is clicked
     */
    @FXML
    private void cancel() {
        ((Stage) searchField.getScene().getWindow()).close();
    }

    /**
     * if the search button is clicked
     */
    @FXML
    private void searchAction() {
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
        cancel();
    }


    /**
     * Finds all available tags, assigns their labels and checkboxes and adds them to an observable
     * list, which is then inserted into a table list that's presented to the user.
     */
    @FXML
    protected void insertTags() {
        ArrayList<String> tagList = ControllerTagging.getAllTags();

        for (int i = 0; i < tagList.size(); i++) {
            String t = tagList.get(i);
            CheckBox ch = new CheckBox("" + t);
            observeList.add(new TagTableRow(i, "", ch));
        }


        tagTable.setItems(observeList);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        select.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
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
