package controller;

import backend.TagTableRow;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.lang.reflect.Array;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class ControllerTagging implements Initializable {
    @FXML
    private Button newTagButton;
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

    protected static ArrayList<String> bufferTags = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            insertTags();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    ObservableList<TagTableRow> observeList = FXCollections.observableArrayList();

    /**
     * Finds all available tags, assigns their labels and checkboxes and adds them to an observable
     * list, which is then inserted into a table list that's presented to the user.
     */
    @FXML
    @SuppressWarnings("Duplicates")
    protected void insertTags() throws SQLException {
        taggingTable.getItems().clear();

        ArrayList tagList = getAllTags();
        tagList.removeAll(Arrays.asList("", null));

/*
        ArrayList<String> tagList = new ArrayList<>();
        tagList.add("Tag1");
        tagList.add("Tag2");
        tagList.add("Tag3");

 */

        ArrayList<String> newTagList;
        Set<String> set = new LinkedHashSet<>(tagList);

        if(bufferTags.isEmpty()){
            newTagList = tagList;
        }else{
            set.addAll(bufferTags);
            newTagList = new ArrayList<>(set);
        }


        for (int i = 0; i < newTagList.size() ; i++) {
            String t = newTagList.get(i);
            CheckBox ch = new CheckBox(""+t);
            observeList.add(new TagTableRow(i, "", ch));
        }

        taggingTable.setItems(observeList);
        id.setCellValueFactory(new PropertyValueFactory<TagTableRow, Integer>("id"));
        select.setCellValueFactory(new PropertyValueFactory<TagTableRow, CheckBox>("checkBox"));
    }

    /**
     * Activates when the user presses the "Done" button.
     * Goes through each checkbox, checking which ones are selected, and sends a string array with
     * all the selected tags to the database.
     * @param ae
     * @throws SQLException
     */
    @FXML
    private void doneAction(ActionEvent ae) throws SQLException {
        bufferTags.clear();
        ArrayList<String> tempTagList = new ArrayList<>();
        for(TagTableRow tb : taggingTable.getItems()){
            if(tb.getCheckBox().isSelected()){
                tempTagList.add(tb.getCheckBox().getText());
            }
        }
        String[] tagList = tempTagList.toArray(new String[tempTagList.size()]);
        ControllerMain.getDatabaseClient().addTag(ControllerMain.getPathBuffer(), tagList);

        ((Stage) taggingDone.getScene().getWindow()).close();
    }

    @FXML
    private void cancelAction(ActionEvent ae){
        bufferTags.clear();
        ((Stage) taggingCancel.getScene().getWindow()).close();
    }

    /**
     * Activates when the user presses the "New Tag" button.
     * Opens a new window where the user can create a new tag.
     * @param ae
     */
    @FXML
    private void newTagAction(ActionEvent ae) throws SQLException {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("New tag");
        d.setContentText("Tag:");
        d.setHeaderText(null);
        d.setGraphic(null);

        Optional<String> input = d.showAndWait();

        if(!input.get().equals("")) {
           bufferTags.add(input.get());
        }else{
            System.out.println("Please enter a tag name!");
        }

        insertTags();
    }

    protected ArrayList<String> getAllTags() throws SQLException {
        ArrayList tagStrings = ControllerMain.databaseClient.getColumn("Tags");
        ArrayList<String> tagList = new ArrayList<>();
        for (Object s : tagStrings) {
            tagList.addAll(Arrays.asList(s.toString().split(",")));
        }

        return tagList;
    }

}
