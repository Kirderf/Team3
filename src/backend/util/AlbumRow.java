package backend.util;

import javafx.scene.control.CheckBox;

/**
 * This class is used to generate objects of the AlbumRow class, that is rows
 * of albums that will be added into a TableView in {@link controller.ControllerAddToAlbum}
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
        this.tagName = name;
    }

    /**
     * Gets the row's check box.
     *
     * @return the checkbox
     */
    public CheckBox getCheckBox() {
        return checkBox;
    }

}
