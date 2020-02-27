package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ControllerMain {
    private Stage importStage = new Stage();

    @FXML
    private Menu fileButton;

    @FXML
    private Menu importButton;

    @FXML
    private Menu openSearch;

    @FXML
    private Menu returnToLibrary;

    @FXML
    private Menu helpButton;

    @FXML
    private ScrollPane metadataScroll;

    @FXML
    private ScrollPane tagsScroll;

    @FXML
    private GridPane pictureGrid;

    @FXML
    private ComboBox<?> sortDropDown;

    @FXML
    private TextArea pathDisplay;

    @FXML
    private MenuItem quitItem;

    @FXML
    private void importAction(ActionEvent event) throws IOException {
        if(!importStage.isShowing()) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/Import.fxml"));
                importStage.setScene(new Scene(root));
                importStage.setTitle("Import");
                importStage.setResizable(false);
                importStage.show();
            } catch (Exception exception) {
                System.out.println(exception.getLocalizedMessage());
            }
        }
    }

    @FXML
    private void exportAction(ActionEvent event) {

    }

    @FXML
    private void quitAction(ActionEvent event) {
        Stage stage = (Stage) pathDisplay.getScene().getWindow();
        stage.close();
    }

}
