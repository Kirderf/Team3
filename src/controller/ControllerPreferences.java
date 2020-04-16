package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ControllerPreferences implements Initializable {

    private static boolean colourChecked = false;
    private static boolean ttsChecked = false;
    @FXML
    private CheckBox ttsCheck;
    @FXML
    private CheckBox colourCheck;

    /**
     * returns whether or not the colour has been checked
     *
     * @return boolean
     */
    protected static boolean isColourChecked() {
        return colourChecked;
    }

    /**
     * returns whether or not the colour has been checked
     *
     * @return boolean
     */
    protected static boolean isTtsChecked() {
        return ttsChecked;
    }

    /**
     * when the prefrence window is open
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //shown as selected if they have been selected earlier
        colourCheck.setSelected(colourChecked);
        ttsCheck.setSelected(ttsChecked);
    }

    /**
     * if the checkbox is selected, then the class variable is changed
     */
    protected void setColourBlind() {
        colourChecked = colourCheck.isSelected();
    }

    /**
     * if the checkbox is selected, then the class variable is changed
     */
    protected void setTTS() {
        ttsChecked = ttsCheck.isSelected();
    }

}
