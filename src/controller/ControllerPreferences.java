package controller;

import backend.util.Log;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerPreferences implements Initializable {
    private static final Log logger = new Log();

    @FXML
    private CheckBox ttsCheck;
    @FXML
    private CheckBox colourCheck;

    private static boolean colourChecked = false;
    private static boolean ttsChecked = false;

    /**
     * when the prefrence window is open
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //shown as selected if they have been selected earlier
        colourCheck.setSelected(colourChecked);
        ttsCheck.setSelected(ttsChecked);
    }

    /**
     * returns whether or not the colour has been checked
     * @return boolean
     */
    public static boolean isColourChecked(){
        return colourChecked;
    }
    /**
     * returns whether or not the colour has been checked
     * @return boolean
     */
    public static boolean isTtsChecked() {
        return ttsChecked;
    }

    /**
     * if the checkbox is selected, then the class variable is changed
     * @param actionEvent auto-generated
     */
    public void setColourBlind(ActionEvent actionEvent) {
        colourChecked = colourCheck.isSelected();
    }

    /**
     * if the checkbox is selected, then the class variable is changed
     * @param actionEvent auto-generated
     */
    public void setTTS(ActionEvent actionEvent) {
        ttsChecked = ttsCheck.isSelected();
    }

}
