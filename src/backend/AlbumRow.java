package backend;

import javafx.scene.control.CheckBox;

public class AlbumRow {
    int id;
    String tagName;
    CheckBox checkBox;

    public AlbumRow(int id, String tagName, CheckBox checkBox) {
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
