package backend;

import backend.util.Log;
import javafx.scene.control.CheckBox;

import java.util.logging.Logger;

/**
 * The type Album row. used for rows in addToAlbum
 */
public class AlbumRow {
    private static final Log logger = new Log();

    int id;
    String tagName;
    CheckBox checkBox;

    /**
     * Initializes a new AlbumRow, i.e. creates a new row in the table.
     *
     * @param id       the row's id
     * @param tagName  the row's corresponding tag
     * @param checkBox the row's checkbox
     */
    public AlbumRow(int id, String tagName, CheckBox checkBox) {
        logger.logNewInfo("new albumRow" + "id: " + id + ", tagname: " + tagName + ", Checkbox: " + checkBox.toString());
        this.id = id;
        this.tagName = tagName;
        this.checkBox = checkBox;
    }

    /**
     * Gets the row id.
     *
     * @return the row id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the row id.
     *
     * @param id the row id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the row's corresponding tag.
     *
     * @return the tag
     */
    public String getName() {
        return tagName;
    }

    /**
     * Sets the row's corresponding tag.
     *
     * @param name the new tag
     */
    public void setName(String name) {
        this.tagName = tagName;
    }

    /**
     * Gets the row's check box.
     *
     * @return the checkbox
     */
    public CheckBox getCheckBox() {
        return checkBox;
    }

    /**
     * Sets the row's checkbox.
     *
     * @param checkBox the new checkbox
     */
    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

}
