package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class is a controller that handles actions made by the user when
 * interacting with the import stage, where they choose what
 * images to import to the program and database.
 */
public class ControllerImport implements Initializable {

    /*
     * Boolean for import status
     */
    private static boolean importSucceed = false;
    /*
     * File explorer
     */
    private final FileChooser fc = new FileChooser();
    /*
     * Container for textfields
     */
    @FXML
    private VBox pathVbox;
    /*
     * Scrollable container which includes the vbox
     */
    @FXML
    private ScrollPane scrollPane;
    /*
     * List for containing file explorer results
     */
    private ArrayList<File> bufferList = new ArrayList<>();

    /**
     * Used to check when window should be closed
     *
     * @return true if import succeeds and the window can be closed, or false if not
     */
    static boolean isImportSucceed() {
        return importSucceed;
    }

    /**
     * Sets the importSucceed boolean
     *
     * @param b boolean
     */
    static void setImportSucceed(boolean b) {
        importSucceed = b;
    }

    /**
     * This method is called when a stage is created using this
     * controller. It sets the container content and alignment of elements
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setContent(pathVbox);
        pathVbox.setPadding(new Insets(5, 0, 0, 5));
        pathVbox.setSpacing(7);

    }

    /*
     * Opens file chooser, gets the paths of the chosen images, then displays them to the user.
     */
    @FXML
    private void addImageFile() {

        fc.setTitle("Open Resource File");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pictures", "*.png", "*.jpg", "*.jpeg", "*.PNG", "*.JPG", "*.JPEG"));
        /*
         * List for containing temporary file explorer results
         */
        List<File> list = fc.showOpenMultipleDialog(scrollPane.getScene().getWindow());
        ArrayList<String> paths = (ArrayList<String>) ControllerMain.getDatabaseClient().getColumn("Path");
        ArrayList<File> presentFiles = new ArrayList<>();
        if (list != null) {
            paths.forEach(x -> {
                if (list.contains(new File(FilenameUtils.normalize(x)))) {
                    presentFiles.add(new File(x));
                }
            });
        }
        //if some images have already been added the user is prompted
        if (!presentFiles.isEmpty()) {
            StringBuilder alertString = new StringBuilder("The image(s)\n");
            for (File f : presentFiles) {
                alertString.append(f.getPath()).append("\n");
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION, alertString + "have already been added");
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(ControllerMain.appIcon);
            alert.showAndWait();
        }
        if (list != null) {
            list.forEach(x -> {
                if (!bufferList.contains(x) && !presentFiles.contains(x)) bufferList.add(x);
            });
        }
        if (bufferList != null) {
            clearListView();
            for (File file : bufferList) {
                generateTextField(file.getAbsolutePath());
            }
        }
    }

    /*
     * This method is called when the user presses the close button, it closes the window.
     */
    @FXML
    private void cancel() {
        ((Stage) scrollPane.getScene().getWindow()).close();
    }

    /*
     * Clear the buffer list and view buffer
     */
    @FXML
    private void clearAction() {
        clearListView();
        bufferList.clear();
    }

    /*
     * Creates a duplicate of a textfield and insert into scrollpane
     *
     * @param text input for textfields
     */
    @FXML
    private void generateTextField(String text) {
        Text textElement = new Text(text);
        textElement.setFont(Font.font("Montserrat"));
        textElement.setDisable(true);
        pathVbox.getChildren().addAll(textElement);
    }

    /*
     * Once all paths has been added to the list, add it to the database and display it in the MainView
     */
    @FXML
    private void importAction() {
        if (bufferList != null) {
            for (File file : bufferList) {
                ControllerMain.getDatabaseClient().addImage(file.getAbsolutePath());
            }
            setImportSucceed(true);
        }
        cancel();
    }

    /*
     * Clears the view buffer
     */
    private void clearListView() {
        pathVbox.getChildren().clear();
    }
}

