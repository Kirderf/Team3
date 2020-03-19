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

    /**
     * when the prefrence window is open
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colourCheck.setSelected(colourChecked);
    }

    /**
     * returns whether or not the colour has been checked
     * @return boolean
     */
    static boolean isColourChecked(){
        return colourChecked;
    }

    /**
     * if the checkbox is selected, then the class variable is changed
     * @param actionEvent auto-generated
     */
    public void setColourBlind(ActionEvent actionEvent) {
        colourChecked = colourCheck.isSelected();
    }
}
