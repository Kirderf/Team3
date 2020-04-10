package backend.util;

import javafx.scene.control.CheckBox;

import java.util.logging.Logger;

/**
 * used for rows when adding tags.
 */
public class TagTableRow {
    private static final Log logger = new Log();

    /**
     * The Id.
     */
    int id;
    /**
     * The Tag name.
     */
    String tagName;
    /**
     * The Check box.
     */
    CheckBox checkBox;

    /**
     * Instantiates a new Tag table row.
     * @param id       the id
     * @param tagName  the tag name
     * @param checkBox the check box
     */
    public TagTableRow(int id, String tagName, CheckBox checkBox) {
        logger.logNewInfo("new TagTableRow" + " id: " + id + ", tag name: " + tagName + ", checkbox: " + checkBox.toString());
        this.id = id;
        this.tagName = tagName;
        this.checkBox = checkBox;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return tagName;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.tagName = tagName;
    }

    /**
     * Gets check box.
     *
     * @return the check box
     */
    public CheckBox getCheckBox() {
        return checkBox;
    }

    /**
     * Sets check box.
     *
     * @param checkBox the check box
     */
    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }
}
