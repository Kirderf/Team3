package controller;

import backend.util.Log;
import backend.util.TagTableRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class ControllerTagging implements Initializable {
    private static final Log logger = new Log();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            logger.logNewInfo("Initializing controllerTagging");
            insertTags();
        } catch (Exception e) {
            logger.logNewFatalError(e.getLocalizedMessage());
        }
    }

    private ObservableList<TagTableRow> observeList = FXCollections.observableArrayList();

    /**
     * Finds all available tags, assigns their labels and checkboxes and adds them to an observable
     * list, which is then inserted into a table list that's presented to the user.
     */
    @FXML
    @SuppressWarnings("Duplicates")
    private void insertTags() {
        logger.logNewInfo("Inserting tags in ControllerTagging");

        ArrayList<String> tagList = getAllTags();

        ArrayList<String> newTagList;
        Set<String> set = new LinkedHashSet<>(tagList);

        if (bufferTags.isEmpty()) {
            newTagList = tagList;
        } else {
            set.addAll(bufferTags);
            newTagList = new ArrayList<>(set);
        }
        Collections.sort(newTagList);

        String[] alreadySelected = ControllerMain.getDatabaseClient().getTags(ControllerMain.getPathBuffer()).split(",");

        for (int i = 0; i < newTagList.size(); i++) {
            String s = newTagList.get(i);
            boolean exists = false;
            for (TagTableRow t : observeList) {
                if (t.getName().equals(s)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                CheckBox ch = new CheckBox("" + s);
                //automatically selects the new tags
                if (bufferTags.contains(s)) ch.setSelected(true);
                if (Arrays.asList(alreadySelected).contains(s)) ch.setSelected(true);
                observeList.add(new TagTableRow(observeList.size()+i, s, ch));
            }
        }

        taggingTable.setItems(observeList);
        select.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
    }

    /**
     * Activates when the user presses the "Done" button.
     * Goes through each checkbox, checking which ones are selected, and sends a string array with
     * all the selected tags to the database.
     *
     */
    @FXML
    private void doneAction() {
        ArrayList<String> tempUnchecked = getUncheckedBoxes();
        String[] uncheckedTags = tempUnchecked.toArray(new String[0]);
        //prompts the user that tags will not be saved
        for (String s : tempUnchecked) {
            if (bufferTags.contains(s.toLowerCase())) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Please be advised that tags that are not applied to an image will not be saved");
                Optional<ButtonType> option = alert.showAndWait();
                if (option.isPresent() && option.get() != ButtonType.OK) {
                    return;
                }
            }
        }
        bufferTags.clear();
        ArrayList<String> tempTagList = getCheckedBoxes();
        String[] tagList = tempTagList.toArray(new String[0]);
        ControllerMain.getDatabaseClient().addTag(ControllerMain.getPathBuffer(), tagList);
        ControllerMain.getDatabaseClient().removeTag(ControllerMain.getPathBuffer(), uncheckedTags);
        ((Stage) taggingDone.getScene().getWindow()).close();
    }

    @FXML
    private void cancelAction() {
        ((Stage) taggingCancel.getScene().getWindow()).close();
    }

    /**
     * Activates when the user presses the "New Tag" button.
     * Opens a new window where the user can create a new tag.
     *
     */
    @FXML
    private void newTagAction() {
        //if any of the tags added this session have been unchecked, they are removed from the arraylist
        //the tagsAddedThisSession arraylist decides whether or not the tags are checked when inserting
        getUncheckedBoxes().forEach(bufferTags::remove);
        TextInputDialog d = new TextInputDialog();
        d.setTitle("New tag");
        d.setContentText("Tag:");
        d.setHeaderText(null);
        d.setGraphic(null);

        Optional<String> input = d.showAndWait();

        if (input.isPresent()) {
            String tag = input.get().substring(0, 1).toUpperCase() + input.get().substring(1).toLowerCase();
            if (tag.contains(",")) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText(null);
                a.setContentText("The tag cannot contain ','!");
                a.showAndWait();
                logger.logNewWarning("User attempted to create a tag containing ','");
                return;
            }

            if (!tag.equals("")) {
                if (tagInTable(tag)) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setHeaderText(null);
                    a.setContentText("The tag '" + tag + "' already exists!");
                    a.showAndWait();
                    return;
                }
                bufferTags.add(tag);
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("404: Tag name not found");
                a.setHeaderText(null);
                a.setContentText("Please enter a tag name!");
                a.showAndWait();
                logger.logNewWarning("Input was empty, no tag was added!");
            }
        }

        insertTags();
    }

    private boolean tagInTable(String inTag) {
        if (bufferTags.contains(inTag)) {
            return true;
        }
        for (TagTableRow t : observeList) {
            if (t.getName().equals(inTag)) {
                return true;
            }
        }
        return false;
    }

    static ArrayList<String> getAllTags() {
        ArrayList<String> tagStrings = (ArrayList<String>) ControllerMain.getDatabaseClient().getColumn("Tags");
        LinkedHashSet<String> hashSet = new LinkedHashSet<>();
        for (Object s : tagStrings) {
            hashSet.addAll(Arrays.asList(s.toString().split(",")));
        }
        ArrayList<String> tagList = new ArrayList<>(hashSet);
        tagList.removeAll(Arrays.asList("", null));
        return tagList;
    }

    private ArrayList<String> getCheckedBoxes() {
        ArrayList<String> tempTagList = new ArrayList<>();
        for (TagTableRow tb : taggingTable.getItems()) {
            if (tb.getCheckBox().isSelected()) {
                tempTagList.add(tb.getCheckBox().getText());
            }
        }
        return tempTagList;
    }

    private ArrayList<String> getUncheckedBoxes() {
        ArrayList<String> tempTagList = new ArrayList<>();
        for (TagTableRow tb : taggingTable.getItems()) {
            if (!tb.getCheckBox().isSelected()) {
                tempTagList.add(tb.getCheckBox().getText());
            }
        }
        return tempTagList;
    }
}
