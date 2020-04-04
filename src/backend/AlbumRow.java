package backend;

import backend.util.Log;
import javafx.scene.control.CheckBox;

import java.util.logging.Logger;

public class AlbumRow {
    private static final Log logger = new Log();

    int id;
    String tagName;
    CheckBox checkBox;

    public AlbumRow(int id, String tagName, CheckBox checkBox) {
        logger.logNewInfo("new albumRow" + "id: " + id+ ", tagname: " + tagName + ", Checkbox: " + checkBox.toString());
        this.id = id;
        this.tagName = tagName;
        this.checkBox = checkBox;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return tagName;
    }

    public void setName(String name) {
        this.tagName = tagName;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

}
