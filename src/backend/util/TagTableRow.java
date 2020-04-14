package backend.util;

import javafx.scene.control.CheckBox;

/**
 * This class is used to create objects to occupy a TableView, in our case
 * each object of this class represents a row in a table of tags. Each row
 * has it's own tag and checkbox.
 */
public class TagTableRow {
    private static final Log logger = new Log();

    int id;
    String tagName;
    CheckBox checkBox;

    /**
     * Initializes a new Tag table row.
     *
     * @param id       the row's id
     * @param tagName  the row's tag
     * @param checkBox the row's checkbox
     */
    public TagTableRow(int id, String tagName, CheckBox checkBox) {
        logger.logNewInfo("new TagTableRow" + " id: " + id + ", tag name: " + tagName + ", checkbox: " + checkBox.toString());
        this.id = id;
        this.tagName = tagName;
        this.checkBox = checkBox;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the tag.
     *
     * @return the tag
     */
    public String getName() {
        return tagName;
    }

    /**
     * Sets the tag
     *
     * @param name the tag
     */
    public void setName(String name) {
        this.tagName = name;
    }

    /**
     * Gets checkbox.
     *
     * @return the checkbox
     */
    public CheckBox getCheckBox() {
        return checkBox;
    }

    /**
     * Sets checkbox.
     *
     * @param checkBox the checkbox
     */
    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }
}
