package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerPreferences implements Initializable {

    @FXML
    private CheckBox ttsCheck;
    @FXML
    private CheckBox colourCheck;
    private static boolean colourChecked = false;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public static boolean isColourChecked(){
        return colourChecked;
    }

    public void setColourBlind(ActionEvent actionEvent) {
        colourChecked = colourCheck.isSelected();
    }
}
