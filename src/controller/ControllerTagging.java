package controller;

import backend.util.Log;
import backend.util.TagTableRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class ControllerTagging implements Initializable {
    private static final Log logger = new Log();
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


    static ArrayList<String> bufferTags = new ArrayList<>();
    ArrayList<String> newTagNames = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            logger.logNewInfo("Initializing controllerTagging");
            insertTags();
        } catch (SQLException e) {
            logger.logNewFatalError(e.getLocalizedMessage());
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
        logger.logNewInfo("Inserting tags in ControllerTagging");
        taggingTable.getItems().clear();

        ArrayList tagList = getAllTags();

        ArrayList<String> newTagList;
        Set<String> set = new LinkedHashSet<>(tagList);

        if(bufferTags.isEmpty()){
            newTagList = tagList;
        }else{
            set.addAll(bufferTags);
            newTagList = new ArrayList<>(set);
        }
        String[] alreadySelected = ControllerMain.getDatabaseClient().getTags(ControllerMain.getPathBuffer()).split(",");
        for (int i = 0; i < newTagList.size(); i++) {
            String t = newTagList.get(i);
            CheckBox ch = new CheckBox(""+t);
            //automatically selects the new tags
            if(newTagNames.contains(t)) ch.setSelected(true);
            if(Arrays.asList(alreadySelected).contains(t)) ch.setSelected(true);
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
        ArrayList<String> tempUnchecked = getUncheckedBoxes();
        String[] uncheckedTags = tempUnchecked.toArray(new String[tempUnchecked.size()]);
        //used in contains later
        bufferTags.forEach(String::toLowerCase);
        //prompts the user that tags will not be saved
        for(int i = 0;i<tempUnchecked.size(); i++) {
            if (bufferTags.contains(tempUnchecked.get(i).toLowerCase())) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Please be advised that tags that are not applied to an image will not be saved");
                Optional<ButtonType> option = alert.showAndWait();
                if (option.get() != ButtonType.OK) {
                    //if they are not ok with saving their tags
                    return;
                }
                else{
                    i = tempUnchecked.size();
                }
            }
        }
        bufferTags.clear();
        ArrayList<String> tempTagList = getCheckedBoxes();
        String[] tagList = tempTagList.toArray(new String[tempTagList.size()]);
        ControllerMain.getDatabaseClient().addTag(ControllerMain.getPathBuffer(), tagList);
        ControllerMain.getDatabaseClient().removeTag(ControllerMain.getPathBuffer(),uncheckedTags);
        ((Stage) taggingDone.getScene().getWindow()).close();
    }

    @FXML
    private void cancelAction(ActionEvent ae){
        //bufferTags.clear();
        ((Stage) taggingCancel.getScene().getWindow()).close();
    }

    /**
     * Activates when the user presses the "New Tag" button.
     * Opens a new window where the user can create a new tag.
     * @param ae
     */
    @FXML
    private void newTagAction(ActionEvent ae) throws SQLException {
        //if any of the tags added this session have been unchecked, they are removed from the arraylist
        //the tagsAddedThisSession arraylist decides whether or not the tags are checked when inserting
        getUncheckedBoxes().forEach(newTagNames::remove);
        TextInputDialog d = new TextInputDialog();
        d.setTitle("New tag");
        d.setContentText("Tag:");
        d.setHeaderText(null);
        d.setGraphic(null);

        Optional<String> input = d.showAndWait();

        if(input.isPresent()) {
            if(!input.get().equals("")) {
                newTagNames.add(input.get());
                bufferTags.add(input.get());
            }else{
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("404: Tag name not found");
                a.setHeaderText(null);
                a.setContentText("Please enter a tag name!");
                a.showAndWait();
                logger.logNewWarning("no tag name was added!");
            }
        }

        insertTags();
    }

    protected static ArrayList<String> getAllTags() throws SQLException {
        ArrayList tagStrings = ControllerMain.getDatabaseClient().getColumn("Tags");
        LinkedHashSet<String> hashSet = new LinkedHashSet<>();
        for (Object s : tagStrings) {
            hashSet.addAll(Arrays.asList(s.toString().split(",")));
        }
        ArrayList<String> tagList = new ArrayList<>(hashSet);
        tagList.removeAll(Arrays.asList("", null));
        return tagList;
    }

    protected ArrayList<String> getCheckedBoxes(){
        ArrayList<String> tempTagList = new ArrayList<>();
        for(TagTableRow tb : taggingTable.getItems()){
            if(tb.getCheckBox().isSelected()){
                tempTagList.add(tb.getCheckBox().getText());
            }
        }
        return tempTagList;
    }
    private ArrayList<String> getUncheckedBoxes(){
        ArrayList<String> tempTagList = new ArrayList<>();
        for(TagTableRow tb : taggingTable.getItems()){
            if(!tb.getCheckBox().isSelected()){
                tempTagList.add(tb.getCheckBox().getText());
            }
        }
        return tempTagList;
    }
}
