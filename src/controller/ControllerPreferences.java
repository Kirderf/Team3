package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is a controller that handles all actions made by the user
 * when interacting with the preferences stage.
 */
public class ControllerPreferences implements Initializable {

    private static boolean colourChecked = false;
    private static boolean ttsChecked = false;
    @FXML
    private CheckBox ttsCheck;
    @FXML
    private CheckBox colourCheck;

    /**
     * Returns true if the colorblind-mode-checkbox has been checked, false if not
     *
     * @return boolean
     */
    static boolean isColourChecked() {
        return colourChecked;
    }

    /**
     * Returns true if the text-to-speech-checkbox been checked, false if not
     *
     * @return boolean
     */
    public static boolean isTtsChecked() {
        return ttsChecked;
    }

    /**
     * This method is called when a scene is created using this controller. It
     * checks whether or not the checkboxes should be checked or not, and sets them as such.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //shown as selected if they have been selected earlier
        colourCheck.setSelected(colourChecked);
        ttsCheck.setSelected(ttsChecked);
    }

    /**
     * Changes the colourblind class variable if the checkbox is checked
     */
    @FXML
    protected void setColourBlind() {
        colourChecked = colourCheck.isSelected();
    }

    /**
     * Changes the text-to-speech class variable if the checkbox is checked
     */
    @FXML
    protected void setTTS() {
        ttsChecked = ttsCheck.isSelected();
    }

}
