package controller;

import backend.util.TagTableRow;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class is a controller that handles all actions made by the user
 * when interacting with the search stage.
 */
public class ControllerSearch implements Initializable {
    private static ArrayList<String> searchResults = new ArrayList<>();
    private static boolean searchSucceed = false;
    @FXML
    private CheckBox metaCheck;
    @FXML
    private CheckBox pathCheck;
    @FXML
    private CheckBox filenameCheck;
    @FXML
    private TextField searchField;
    @FXML
    private TableColumn<TagTableRow, Integer> id;
    @FXML
    private TableColumn<TagTableRow, CheckBox> select;
    @FXML
    private TableView<TagTableRow> tagTable;
    private ObservableList<TagTableRow> observeList = FXCollections.observableArrayList();

    /**
     * Gets the result of a search
     *
     * @return ArrayList with paths
     */
    static ArrayList<String> getSearchResults() {
        return searchResults;
    }

    /**
     * Used in order to know when to close the window
     *
     * @return  true if search has been completed, false if not
     */
    static boolean isSearchSucceed() {
        return searchSucceed;
    }

    /**
     * Sets the searchSucceed boolean that lets the program know whether
     * the stage can be closed or not.
     *
     * @param searchSucceed boolean
     */
    static void setSearchSucceed(boolean searchSucceed) {
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

    /**
     * This method is called when a scene is created
     * using this controller. It disables the searchField, adds listeners
     * to the checkboxes, and inserts tags into the TableView.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchField.setDisable(true);
        pathCheck.selectedProperty().addListener(enableSearchField());
        metaCheck.selectedProperty().addListener(enableSearchField());
        filenameCheck.selectedProperty().addListener(enableSearchField());
        insertTags();
    }

    /*
     * if the close button is clicked
     */
    @FXML
    private void cancel() {
        ((Stage) searchField.getScene().getWindow()).close();
    }

    /*
     * if the search button is clicked
     */
    @FXML
    private void searchAction() {
        if ((searchField.getText() == null || searchField.getText().equalsIgnoreCase("")) && getCheckedBoxes().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You have selected no criteria, cannot search!");
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(ControllerMain.appIcon);
            alert.showAndWait();
        } else {
            //clears static resultList
            searchResults.clear();

            // Tags

            ArrayList<String> tempTagList = getCheckedBoxes();
            //does not need to check if temptagList is empty
            //this for loop will not run if the tag list is empty
            for (String tag : tempTagList) {
                List<String> tagResult = ControllerMain.getDatabaseClient().search(tag, "Tags");
                if (tagResult != null) {
                    for (String s : tagResult) {
                        if (!searchResults.contains(s)) {
                            searchResults.add(s);
                        }
                    }
                }
            }


            if (metaCheck.isSelected()) {
                List<String> metaResult = ControllerMain.getDatabaseClient().search(searchField.getText(), "Metadata");
                if (metaResult != null) {
                    for (String s : metaResult) {
                        if (!searchResults.contains(s)) {
                            searchResults.add(s);
                        }
                    }
                }
            }
            if (pathCheck.isSelected()) {
                List<String> pathResult = ControllerMain.getDatabaseClient().search(searchField.getText(), "Path");
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
                List<String> filenameResult = ControllerMain.getDatabaseClient().search(searchField.getText(), "Path");
                if (filenameResult != null) {
                    for (String s : filenameResult) {
                        //specifies that we only want the ones where the actual filename contains the search term
                        if (s.substring(s.lastIndexOf(File.separator)).contains(searchField.getText())) {
                            searchResults.add(s);
                        }
                    }
                }
            }


            searchSucceed = true;
            cancel();
        }
    }


    /*
     * Finds all available tags, assigns their labels and checkboxes and adds them to an observable
     * list, which is then inserted into a table list that's presented to the user.
     */
    @FXML
    private void insertTags() {
        ArrayList<String> tagList = ControllerTagging.getAllTags();

        Collections.sort(tagList);

        for (int i = 0; i < tagList.size(); i++) {
            String t = tagList.get(i);
            CheckBox ch = new CheckBox("" + t);
            observeList.add(new TagTableRow(i, "", ch));
        }


        tagTable.setItems(observeList);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        select.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
    }

    private ArrayList<String> getCheckedBoxes() {
        ArrayList<String> tempTagList = new ArrayList<>();
        for (TagTableRow tb : tagTable.getItems()) {
            if (tb.getCheckBox().isSelected()) {
                tempTagList.add(tb.getCheckBox().getText());
            }
        }
        return tempTagList;
    }
}
