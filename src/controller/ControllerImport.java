package controller;

import backend.util.Log;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerImport implements Initializable {
    private static final Log logger = new Log();

    /**
     * Boolean for import status
     */
    private static boolean importSucceed = false;
    /**
     * File explorer
     */
    private final FileChooser fc = new FileChooser();
    /**
     * Container for textfields
     */
    @FXML
    private VBox pathVbox;
    /**
     * Scrollable container which includes the vbox
     */
    @FXML
    private ScrollPane scrollPane;
    /**
     * List for containing file explorer results
     */
    private ArrayList<File> bufferList = new ArrayList<>();

    /**
     * Set container content and alignment of elements
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setContent(pathVbox);
        pathVbox.setPadding(new Insets(5, 0, 0, 5));
        pathVbox.setSpacing(7);
    }


    public static void setImportSucceed(boolean b){
        importSucceed = b;
    }

    public static boolean isImportSucceed(){
        return importSucceed;
    }

    /**
     * Opens file chooser, and gets path, then displays it to the user.
     * @param event button clicked
     */
    @FXML
    private void addImageFile(ActionEvent event) {
        fc.setTitle("Open Resource File");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pictures", "*.png", "*.jpg", "*.jpeg", "*.PNG", "*.JPG", "*.JPEG"));
        /**
         * List for containing temporary file explorer results
         */
        List<File> list = fc.showOpenMultipleDialog(scrollPane.getScene().getWindow());
        if (list != null) {
            list.forEach((x) -> {
                if (!bufferList.contains(x)) bufferList.add(x);
            });
        }
        if (bufferList != null) {
            clearListView();
            for (File file : bufferList) {
                generateTextField(file.getAbsolutePath());
            }
        }
    }

    /**
     * Closes the window
     * @param event button clicked
     */
    @FXML
    private void cancel(ActionEvent event) {
        ((Stage) scrollPane.getScene().getWindow()).close();
    }

    /**
     * Clear the buffer list and view buffer
     * @param event
     */
    @FXML
    private void clearAction(ActionEvent event) {
        clearListView();
        bufferList.clear();
    }

    /**
     * Creates a duplicate of a textfield and insert into scrollpane
     * @param text input for textfields
     */
    @FXML
    private void generateTextField(String text) {
        Text textElement = new Text(text);
        textElement.setFont(Font.font("Montserrat"));
        textElement.setDisable(true);
        pathVbox.getChildren().addAll(textElement);
    }

    /**
     * Once all paths has been added to the list, add it to the database and display it in the MainView
     *
     * @param event button clicked
     */
    @FXML
    private void importAction(ActionEvent event) throws SQLException {
        if (bufferList != null) {
            for (File file : bufferList) {
                ControllerMain.getDatabaseClient().addImage(file);
            }
            setImportSucceed(true);
        }
        cancel(event);
    }

    /**
     * Clears the view buffer
     */
    private void clearListView() {
        pathVbox.getChildren().clear();
    }
}

