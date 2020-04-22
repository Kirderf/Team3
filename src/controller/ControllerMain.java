package controller;

import backend.database.DatabaseClient;
import backend.util.ImageExport;
import backend.util.Log;
import backend.util.Text_To_Speech;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;

/**
 * This class is a controller that handles all actions made by the user when
 * interacting with the main window.
 */
public class ControllerMain implements Initializable {
    private static final Log logger = new Log();

    private static HashMap<String, String> locations = new HashMap<>();
    private static DatabaseClient databaseClient;
    private static String pathBuffer;
    private static ArrayList<String> selectedImages = new ArrayList<>();
    private static Image imageBuffer;
    private static double splitPanePos = 0.51;
    private static boolean loggedin = false;

    static {
        try {
            databaseClient = DatabaseClient.getInstance();
        } catch (Exception e) {
            logger.logNewFatalError("Initializing database client " + e.getLocalizedMessage());
        }
    }

    Stage searchStage = new Stage();
    @FXML
    protected SplitPane imgDataSplitPane;
    @FXML
    protected Label homeLabel;
    //Stages
    private Stage albumStage = new Stage();
    private Stage importStage = new Stage();
    private Stage aboutStage = new Stage();
    private Stage worldStage = new Stage();
    private Stage preferenceStage = new Stage();
    private Stage addToAlbumStage = new Stage();
    private Stage loginStage = new Stage();
    //Nodes
    @FXML
    private GridPane pictureGrid;
    @FXML
    private ComboBox<?> sortDropDown;
    @FXML
    private TextField textField;
    @FXML
    private VBox metadataVbox;
    @FXML
    private VBox tagVbox;
    @FXML
    private Menu buttonHome;

    protected static Image appIcon = new Image(ControllerMain.class.getClassLoader().getResourceAsStream("cleanLogo.png"));
    private Text_To_Speech voice = Text_To_Speech.getInstance();
    private int photoCount = 0;
    private int rowCount = 0;
    private int columnCount = 0;

    /**
     * Returns an instance of DatabaseClient to be used when adding images to the database
     *
     * @return DatabaseClient instance
     */
    static DatabaseClient getDatabaseClient() {
        return databaseClient;
    }

    /**
     * Gets the path of the most recently selected image
     *
     * @return path as a String
     */
    static String getPathBuffer() {
        return pathBuffer;
    }

    /*
     * Sets the pathBuffer to the most recently selected image
     *
     * @param pathBuffer path as a String
     */
    private static void setPathBuffer(String pathBuffer) {
        ControllerMain.pathBuffer = pathBuffer;
    }

    /**
     * Sets the login status
     *
     * @param b boolean
     */
    static void setLoggedin(boolean b) {
        loggedin = b;
    }

    /**
     * Gets the split pane position
     *
     * @return double percentage (0.0 - 1.0)
     */
    static double getSplitPanePos() {
        return splitPanePos;
    }

    /**
     * Sets the split pane posistion
     *
     * @param pos double percentage (0.0 - 1.0)
     */
    static void setSplitPanePos(double pos) {
        splitPanePos = pos;
    }

    /**
     * Gets all existing albums
     *
     * @return hashmap with albums
     */
    static Map<String, List<String>> getAlbums() {
        return databaseClient.getAllAlbums();
    }

    /**
     * Gets the imageBuffer, i.e. the most recently selected image
     *
     * @return the Image
     */
    static Image getImageBuffer() {
        return imageBuffer;
    }

    /*
     * Sets the imageBuffer, i.e. the most recently selected image
     *
     * @param imageBuffer the Image
     */
    private static void setImageBuffer(Image imageBuffer) {
        ControllerMain.imageBuffer = imageBuffer;
    }

    /**
     * Gets all images with geographical data
     *
     * @return a hash map with all the images that have valid geographical data
     */
    static HashMap<String, String> getLocations() {
        return locations;
    }

    /*
     * Adds an album to the albums hash map
     *
     * @param key    name of the album
     * @param images ArrayList containing the paths to the images
     */
    private static void newAlbum(String key, ArrayList<String> images) {
        databaseClient.addAlbum(key, images);
    }

    /**
     * Adds the given paths to an existing album
     *
     * @param name   name of the album
     * @param images ArrayList with paths to the images being added
     */
    static void addPathsToAlbum(String name, ArrayList<String> images) {
        databaseClient.addPathsToAlbum(name, images);
    }

    /**
     * Removes an album
     *
     * @param key name of the album
     */
    static void removeAlbum(String key) {
        databaseClient.removeAlbum(key);
    }

    /**
     * Gets the list of selected images
     *
     * @return ArrayList with paths to all the selected images
     */
    static ArrayList<String> getSelectedImages() {
        return selectedImages;
    }

    /**
     * Adds a path to the list of selected images
     *
     * @param s the path being added
     */
    static void addToSelectedImages(String s) {
        selectedImages.add(s);
    }

    /**
     * Clears the list of selected images
     */
    static void clearSelectedImages() {
        selectedImages.clear();
    }

    /*
     * Removes a specific path from the list of selected images
     *
     * @param path path to the image being removed
     */
    private static void removeFromSelectedImages(String path) {
        selectedImages.remove(path);
    }

    /**
     * This method is called when the main window is initialized, and makes sure
     * all stages have our logo set as their icon.
     *
     * @param icon the logo
     */
    void setAllIcons(Image icon) {
        albumStage.getIcons().add(icon);
        loginStage.getIcons().add(icon);
        searchStage.getIcons().add(icon);
        aboutStage.getIcons().add(icon);
        addToAlbumStage.getIcons().add(icon);
        importStage.getIcons().add(icon);
        preferenceStage.getIcons().add(icon);
        worldStage.getIcons().add(icon);
    }

    /*
     * Tints the selected images blue
     *
     * @param imageInput the image being tinted
     */
    private static BufferedImage tint(Image imageInput) {
        BufferedImage image = SwingFXUtils.fromFXImage(imageInput, null);
        //if colourblind
        if (ControllerPreferences.isColourChecked()) {
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    Color pixelColor = new Color(image.getRGB(x, y), true);
                    int r = (pixelColor.getRed() + Color.black.getRed()) / 2;
                    int g = (pixelColor.getGreen() + Color.black.getGreen()) / 2;
                    int b = (pixelColor.getBlue() + Color.black.getBlue()) / 2;
                    int a = pixelColor.getAlpha();
                    int rgba = (a << 24) | (r << 16) | (g << 8) | b;
                    image.setRGB(x, y, rgba);

                }
            }
        } else {
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    Color pixelColor = new Color(image.getRGB(x, y), true);
                    int r = (pixelColor.getRed() + Color.blue.getRed()) / 2;
                    int g = (pixelColor.getGreen() + Color.blue.getGreen()) / 2;
                    int b = (pixelColor.getBlue() + Color.blue.getBlue()) / 2;
                    int a = pixelColor.getAlpha();
                    int rgba = (a << 24) | (r << 16) | (g << 8) | b;
                    image.setRGB(x, y, rgba);
                }
            }
        }
        return image;
    }

    /**
     * This method is called whenever a stage is created using
     * this controller.
     *
     * @param location  auto generated
     * @param resources auto generated
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAllIcons(appIcon);
        if (!loggedin) {
            if (!loginStage.getModality().equals(Modality.APPLICATION_MODAL))
                loginStage.initModality(Modality.APPLICATION_MODAL);
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/Login.fxml"));
                loginStage.setScene(new Scene(root));
                loginStage.setTitle("Log in");
                loginStage.setResizable(false);
                loginStage.showAndWait();
            } catch (IOException e) {
                logger.logNewFatalError("Initialize IOException " + e.getLocalizedMessage());
            }
        }
        if (loggedin) {
            logger.logNewInfo("Initializing ControllerMain");
            pictureGrid.setAlignment(Pos.CENTER);
            imgDataSplitPane.setDividerPositions(splitPanePos);
            if (!loadFromSelectedImages()) refreshImages();
        } else {
            quitAction();
        }

    }

    /**
     * This method is called when the search button is clicked.
     * It creates and presents the user with the search stage.
     *
     * @param event button being pressed
     */
    @FXML
    protected void searchAction(ActionEvent event) {
        clearSelectedImages();
        logger.logNewInfo("SearchAction");
        voice.speak("Searching");
        if (!searchStage.isShowing()) {
            if (!searchStage.getModality().equals(Modality.APPLICATION_MODAL))
                searchStage.initModality(Modality.APPLICATION_MODAL);
            if (!searchStage.getStyle().equals(StageStyle.DECORATED)) searchStage.initStyle(StageStyle.DECORATED);
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/ScrollSearch.fxml"));
                searchStage.setScene(new Scene(root));
                searchStage.setTitle("Search");
                searchStage.setResizable(false);
                searchStage.showAndWait();
                if (ControllerSearch.isSearchSucceed()) {
                    //clears the view
                    clearView();
                    //clears metadata and tags
                    showData();
                    for (String s : ControllerSearch.getSearchResults()) {
                        addToSelectedImages(s);
                        insertImage(s);
                    }

                }
            } catch (Exception e) {
                logger.logNewFatalError("ControllerMain searchAction " + e.getLocalizedMessage());
            }
        }
        ControllerSearch.setSearchSucceed(false);
    }

    /**
     * This method is called when the user presses the remove button.
     * It removes images from the application and database
     *
     * @param event remove button being clicked
     * @return true if images are deleted, false if not
     */
    @FXML
    protected boolean removeAction(ActionEvent event) throws IOException {
        voice.speak("Removing images");
        try {
            if (getSelectedImages().isEmpty()) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                ((Stage) error.getDialogPane().getScene().getWindow()).getIcons().add(appIcon);
                error.setTitle("404: Image(s) not found");
                error.setHeaderText(null);
                error.setContentText("You need to select at least one image to remove");
                error.showAndWait();
                return false;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            ((Stage) confirm.getDialogPane().getScene().getWindow()).getIcons().add(appIcon);
            confirm.setTitle("Confirmation Dialog");
            confirm.setHeaderText("Are you sure you want to remove " + getSelectedImages().size() + " images?");
            confirm.setContentText("This action is not revertible!");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() == ButtonType.OK) {
                Iterator<Map.Entry<String, List<String>>> albumIterator = getAlbums().entrySet().iterator();
                ArrayList<String> emptyAlbums = new ArrayList<>();
                //iterates through albums to find empty ones
                while (albumIterator.hasNext()) {
                    Map.Entry<String, List<String>> albumEntry = albumIterator.next();
                    //deletes all the selcted images from every album
                    databaseClient.removePathsFromAlbum(albumEntry.getKey(), selectedImages);
                    //if the last image was just removed, then the album is deleted
                    if (getAlbums().get(albumEntry.getKey()).isEmpty()) {
                        //should not edit hashmap while iterating over it, so the albums to be removed are saved for later
                        emptyAlbums.add(albumEntry.getKey());
                    }
                }
                emptyAlbums.forEach(ControllerMain::removeAlbum);
                for (String path : getSelectedImages()) {
                    databaseClient.removeImage(path);
                }

                refreshImages();
                return true;
            } else {
                refreshImages();
                return false;
            }

        } catch (IllegalArgumentException e) {
            logger.logNewFatalError("ControllerMain removeImage " + e.getLocalizedMessage());
        }
        return false;
    }

    /**
     * This method is called when the user chooses the 'Import' item from the 'File' menu.
     * If the import stage returns true, the {@link ControllerMain#refreshImages()} method is called,
     * and all the new images are loaded into the grid.
     *
     * @param event 'Import' being pressed
     */
    @FXML
    protected void importAction(ActionEvent event) throws IOException {
        voice.speak("Importing");
        if (!importStage.isShowing()) {
            if (importStage.getModality() != Modality.APPLICATION_MODAL)
                importStage.initModality(Modality.APPLICATION_MODAL);
            if (!importStage.getStyle().equals(StageStyle.DECORATED)) importStage.initStyle(StageStyle.DECORATED);
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Import.fxml"));
            importStage.setScene(new Scene(root));
            importStage.setTitle("Import");
            importStage.setResizable(false);
            importStage.showAndWait();
            if (ControllerImport.isImportSucceed()) {
                if (ControllerPreferences.isTtsChecked()) voice.speak("Import succeeded");
                logger.logNewInfo("Refreshing images after import controllermain");
                refreshImages();
                ControllerImport.setImportSucceed(false);
            }

        }
    }

    /*
     * Sorts the images based on the selected value in the drop down menu
     */
    @FXML
    private void sortAction() throws FileNotFoundException {
        //if size is selected
        voice.speak("Sorting");
        //if path or date is selected
        List<String> sortedList = getDatabaseClient().sort(sortDropDown.getValue().toString());
        clearView();
        for (String s : sortedList) {
            insertImage(s);
        }

    }

    /*
     * When the export button is clicked
     */
    @FXML
    private void exportAction() {
        voice.speak("Exporting");
        if (!getSelectedImages().isEmpty()) {
            Stage exportStage = new Stage();
            if (!exportStage.isShowing()) {
                try {
                    TextInputDialog dialog = new TextInputDialog("");
                    dialog.setTitle("Name picker");
                    dialog.setHeaderText("Enter name for pdf:");
                    dialog.setContentText("Please enter name for pdf:");
                    Optional<String> result = dialog.showAndWait();
                    //this refers to the result
                    result.ifPresent(this::exportPDF);
                } catch (Exception exception) {
                    logger.logNewFatalError("ControllerMain ExportAction " + exception.getLocalizedMessage());
                }
            }
            refreshImages();
        } else {
            Alert exportAlert = new Alert(Alert.AlertType.WARNING, "You need to select some images to export");
            ((Stage) exportAlert.getDialogPane().getScene().getWindow()).getIcons().add(appIcon);
            exportAlert.showAndWait();

        }
    }

    private void exportPDF(String inputText) {
        try {
            if (inputText.trim().equals("")) throw new IOException("Invalid filename inputted");
            //used to check of the inputText is a valid name for a arbitrary file

            File f = new File(File.separator + inputText + FilenameUtils.EXTENSION_SEPARATOR + "txt");
            //this throws error is filename is invalid
            f.getCanonicalPath(); //this throws an error if the filename is invalid

            //chooses album location after selecting name
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choose folder for album");
            //the directory that the file chooser starts in
            File defaultDirectory = new File("/");
            chooser.setInitialDirectory(defaultDirectory);
            File selectedDirectory = chooser.showDialog(null);
            //gets the filename from the user and formats it correctly
            if (ImageExport.exportToPdf(selectedDirectory.getPath() + "/" + inputText + FilenameUtils.EXTENSION_SEPARATOR + "pdf", getSelectedImages())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "To the directory: " + selectedDirectory.getPath() + "\n" + "With the filename: " + inputText + ".pdf");
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(appIcon);
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle("Success");
                alert.setHeaderText("Your album was exported successfully");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Something went wrong when attempting to save your selected images");
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(appIcon);
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle("Something went wrong");
                alert.setHeaderText("Unfortunately we were unable to export your album");
                alert.showAndWait();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You need to pick a valid filename for your album");
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(appIcon);
            alert.showAndWait();
        }
    }

    /*
     * Closes application, and closes connections to database. Cannot close if other windows are open
     */
    @FXML
    private void quitAction() {
        try {
            voice.speak("Closing application");
            Thread.sleep(1500);
            logger.logNewWarning("Closing application");
            Platform.exit();
            System.exit(0);
        } catch (InterruptedException e) {
            logger.logNewWarning("Could not close application / delete table" + e.getLocalizedMessage());
        }
    }

    /**
     * This method is called when the Home button is pressed, it takes the user back to the main view
     * clears the list of selected images and refreshes the grid.
     */
    @FXML
    protected void goToLibrary() throws IOException {
        voice.speak("Going to library");
        clearSelectedImages();
        refreshImages();
    }

    /**
     * This method is called when the 'About' item is chosen from the 'About' menu.
     * It creates and displays the about stage to the user.
     *
     * @throws IOException if the fxml cannot be found
     */
    @FXML
    public void aboutAction() throws IOException {
        voice.speak("About");
        if (!aboutStage.isShowing()) {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/About.fxml"));
            if (aboutStage.getModality() != Modality.APPLICATION_MODAL)
                aboutStage.initModality(Modality.APPLICATION_MODAL);
            aboutStage.setScene(new Scene(root));
            aboutStage.setTitle("About");
            aboutStage.setResizable(false);
            aboutStage.showAndWait();
        }

    }

    /*
     * Clears all rows on the gridView
     */
    private void clearView() {
        rowCount = 0;
        columnCount = 0;
        photoCount = 0;
        pictureGrid.getChildren().clear();
    }

    /*
     * Refresh home UI. Clear, then gets images from database into the view.
     */
    private void refreshImages() {
        try {
            ArrayList<String> paths = (ArrayList<String>) getDatabaseClient().getColumn("Path");
            clearView();
            clearSelectedImages();
            for (String s : paths) {
                //the view is cleared, so there's no use checking if the image has been added as there are no added photos to start with
                if (s != null) {
                    //if the file doesn't exist, the image has been moved or deleted
                    if (new File(s).exists()) {
                        insertImage(s);
                    } else {
                        logger.logNewWarning("Image with path '" + s + "' is missing, removing from the database");
                        //if the image has been moved or deleted, then the image is removed from the database
                        databaseClient.removeImage(s);
                    }
                }
            }
        } catch (Exception e) {
            logger.logNewFatalError("ControllerMain refreshImages" + e.getLocalizedMessage());
        }
        //clears the metadata and tag boxes
        showData();
    }

    /*
     * Insert image into the gridpane
     *
     * @param path to image object
     */
    private void insertImage(String path) throws FileNotFoundException {
        int row = getNextRow();
        int column = getNextColumn();
        ImageView image = importImage(path);
        Pane p = new Pane();
        p.setStyle("-fx-border-color: black; -fx-background-color: white");
        pictureGrid.add(p, column, row);
        pictureGrid.add(image, column, row);
        GridPane.setHalignment(image, HPos.CENTER);
        photoCount++;
    }

    /*
     * Add rows on the bottom of the gridpane
     */
    private void addEmptyRow() {
        RowConstraints con = new RowConstraints();
        con.setPrefHeight(185);
        pictureGrid.getRowConstraints().add(con);
    }

    /*
     * Take a path, and create a ImageView that fits to a space in the grid
     *
     * @param path to image
     */
    private ImageView importImage(String path) throws FileNotFoundException {
        //the full resolution image
        Image fullImage = new Image(new FileInputStream(path));
        //the thumbnail that is shown in the grid view
        Image image = databaseClient.getThumbnail(path);
        //thumbnail
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.maxHeight(185);
        //If image height is greater than width, only lock the height to the grid
        if (image.getHeight() - image.getWidth() >= 0) {
            imageView.fitHeightProperty().bind(pictureGrid.getRowConstraints().get(0).prefHeightProperty());
        } else {
            imageView.fitWidthProperty().bind(pictureGrid.widthProperty().divide(5));
            imageView.fitHeightProperty().bind(pictureGrid.getRowConstraints().get(0).prefHeightProperty());
        }
        //adds event listener to image
        imageView.setOnMouseClicked(onImageClickedEvent(imageView, image, fullImage, path));
        //return thumbnail, this is added into the grid
        return imageView;
    }

    /*
     * EventHandler for mouseclicks on images
     * in the event of an control click then big image is shown
     * normal clicks tags the image and adds it to selected images
     *
     * @param imageView the image that you want to view or mark when clicked
     * @param thumbnail the image thumbnail scaled to a height of 185
     * @param image     the image that the imageview should display
     * @param path      the local path to the image on the user's machine
     * @return eventhandler that marks the images as directed
     */
    private javafx.event.EventHandler<? super javafx.scene.input.MouseEvent> onImageClickedEvent(ImageView imageView, Image thumbnail, Image image, String path) {
        return (EventHandler<MouseEvent>) event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                //Ctrl click
                if (event.isControlDown()) {
                    //ctrl is down
                    selectImage(imageView, thumbnail, path);
                    //if the last image is unselected
                    showData();
                } else {
                    //full resolution image
                    imageView.setImage(image);
                    try {
                        //shows the full resolution image
                        showBigImage(imageView, path);
                    } catch (IOException e) {
                        logger.logNewFatalError("ControllerMain onImageClickedEvent " + e.getLocalizedMessage());
                    }

                }

            }
        };
    }

    /*
     * Select the image and tints it, if it is already selected then the tint is removed
     *
     * @param imageView the imageview that you want to update with the new image
     * @param image     the image that you want to tint
     * @param path      the path to the photo
     */
    private void selectImage(ImageView imageView, Image image, String path) {
        if (!getSelectedImages().contains(path)) {
            addToSelectedImages(path);
            //sets the imageview of the image you clicked to be blue
            imageView.setImage(SwingFXUtils.toFXImage(tint(image), null));
        } else {
            removeFromSelectedImages(path);
            imageView.setImage(image);
        }
    }

    /**
     * Displays an image in the 'Big Image' view
     *
     * @param imageView the image
     * @param path      path to the image
     * @throws IOException if the FXML cannot be found
     */
    protected void showBigImage(ImageView imageView, String path) throws IOException {
        voice.speak("Magnifying image");
        clearSelectedImages();
        addToSelectedImages(path);
        setPathBuffer(path);
        setSplitPanePos(imgDataSplitPane.getDividerPositions()[0]);
        setImageBuffer(imageView.getImage());
        Scene scene = pictureGrid.getScene();
        scene.setRoot(FXMLLoader.load(getClass().getResource("/Views/BigImage.fxml")));
    }

    /*
     * Gets rowcount, is used to facilitate increasing the number of rows when adding pictures
     *
     * @return int with the number of rows
     */
    //for every 5th picture the row will increase in value
    private int getNextRow() {
        if (photoCount == 0) {
            return rowCount;
        }
        if ((photoCount) % 5 == 0) {
            rowCount++;
            if (pictureGrid.getRowConstraints().size() <= rowCount) {
                addEmptyRow();
            }
        }
        return rowCount;
    }


    /*
     * for every 5th picture the column will reset, therefore this is used to give the column of the next imageview
     *
     * @return colomnCount the integer-value for the next coloumn
     */
    private int getNextColumn() {
        if (photoCount == 0) {
            return columnCount++;
        }
        if (photoCount % 5 == 0) {
            columnCount = 0;
            return columnCount++;
        }
        return columnCount++;
    }

    /**
     * Displays the path, metadata and tags of the last selected image
     */
    void showData() {
        showTags();
        showMetadata();
        showPath();
    }

    /*
     * Displays the metadata of the most recently selected image in the sidebar
     */
    private void showMetadata() {
        if (selectedImages.isEmpty()) {
            metadataVbox.getChildren().clear();
            return;
        }
        String path = getSelectedImages().get(getSelectedImages().size() - 1);
        metadataVbox.getChildren().clear();
        String[] metadata = getDatabaseClient().getMetaDataFromDatabase(path);

        metadataVbox.getChildren().add(new Label("File size :" + metadata[2] + " bytes"));
        metadataVbox.getChildren().add(new Label("Date :" + metadata[3].substring(0, 4) + "/" + metadata[3].substring(4, 6) + "/" + metadata[3].substring(6)));
        metadataVbox.getChildren().add(new Label("Height :" + metadata[4]));
        metadataVbox.getChildren().add(new Label("Width :" + metadata[5]));
        metadataVbox.getChildren().add(new Label("GPS Latitude :" + metadata[6]));
        metadataVbox.getChildren().add(new Label("GPS Longitude :" + metadata[7]));

    }

    /*
     * Shows the tags for the most recently selected image in the sidebar
     */
    private void showTags() {
        if (selectedImages.isEmpty()) {
            tagVbox.getChildren().clear();
            return;
        }
        String path = getSelectedImages().get(getSelectedImages().size() - 1);
        tagVbox.getChildren().clear();
        String[] tags = getDatabaseClient().getTags(path).split(",");
        Arrays.sort(tags);
        for (String tag : tags) {
            tagVbox.getChildren().add(new Label(tag));
        }
    }

    private void showPath() {
        if (selectedImages.isEmpty()) {
            textField.clear();
        } else {
            textField.setText(getSelectedImages().get(getSelectedImages().size() - 1));
        }
    }

    /*
     * When the user clicks on goToMap under library
     * Checks all the added photos for valid gps data, and places the ones with valid data on the map
     *
     * @throws IOException if WorldMap.fxml cannot be found
     */
    @FXML
    private void goToMap() throws IOException {
        if (!worldStage.isShowing() && worldStage.getModality() != Modality.APPLICATION_MODAL) {
            worldStage.initModality(Modality.APPLICATION_MODAL);
        }
        voice.speak("Showing map");
        ArrayList<String> paths = (ArrayList<String>) getDatabaseClient().getColumn("Path");
        //do this by checking ration of long at latitiude according to image pixel placing
        //add them to the worldmap view with event listener to check when they're clicked
        for (int i = 0; i < getDatabaseClient().getColumn("GPS_Longitude").size(); i++) {
            double latitude = Double.parseDouble(getDatabaseClient().getMetaDataFromDatabase(paths.get(i))[6]);
            double longitude = Double.parseDouble(getDatabaseClient().getMetaDataFromDatabase(paths.get(i))[7]);
            //if both are not equal to zero, maybe this should be changed to an or
            if (longitude != 0 && latitude != 0) {
                getLocations().put(paths.get(i), "" + latitude + "," + longitude);
            }
        }
        Parent root = FXMLLoader.load(getClass().getResource("/Views/WorldMap.fxml"));
        worldStage.setScene(new Scene(root));
        worldStage.setTitle("Map");
        worldStage.setResizable(false);
        worldStage.showAndWait();

        for (String s : ControllerMap.getSavedToDisk()) {
            new File(s).delete();
        }
        ControllerMap.emptySavedToDisk();
        if (ControllerMap.getClickedImage() != null) {
            showBigImage(ControllerMap.getClickedImage(), ControllerMap.getClickedImage().getId());
        }
    }

    /**
     * This method is called when the 'Save to album' item is chosen in the 'File' menu.
     * It checks if the album name is valid, then creates a new album and adds all the chosen image to it.
     *
     * @param actionEvent auto-generated
     */
    @FXML
    protected void saveAlbumAction(ActionEvent actionEvent) {
        voice.speak("Creating album");
        try {
            if (!getSelectedImages().isEmpty()) {
                TextInputDialog dialog = new TextInputDialog("");
                ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(appIcon);
                dialog.setTitle("Name picker");
                dialog.setHeaderText("Enter name for album:");
                dialog.setContentText("Please enter name for album:");
                Optional<String> result = dialog.showAndWait();

                if (result.isPresent()) {
                    if (!result.get().trim().equals("")) {
                        if (getAlbums().containsKey(result.get())) {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "That album name already exists");
                            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(appIcon);
                            alert.showAndWait();
                        } else {
                            //the amount of characters that will fit inside each album icon
                            if (result.get().trim().length() > 24) {
                                Alert alert = new Alert(Alert.AlertType.WARNING, "Cannot save an album with more than 24 characters in the name");
                                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(appIcon);
                                alert.showAndWait();
                            } else {
                                ArrayList<String> tempArray = new ArrayList<>(getSelectedImages());
                                newAlbum(result.get().trim(), tempArray);
                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Images were successfully added to the album [" + result.get().trim() + "]");
                                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(appIcon);
                                alert.showAndWait();
                                refreshImages();
                            }
                        }
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "You cant save an album using a blank name");
                        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(appIcon);
                        alert.showAndWait();
                        refreshImages();
                    }
                } else {
                    refreshImages();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "You need to select some images to save to an album");
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(appIcon);
                alert.showAndWait();
            }
            clearSelectedImages();
        } catch (Exception exception) {
            logger.logNewFatalError("ControllerMain saveAlbumAction " + exception.getLocalizedMessage());
        }
        refreshImages();
    }

    /**
     * This method is called when the 'View Albums' item is chosen from the 'Library' menu.
     * It presents the user with all their existing albums in a grid view, where they can
     * also choose to delete them.
     *
     * @param actionEvent 'View Albums' being pressed
     * @throws IOException thrown if the FXML can't be found
     */
    @FXML
    protected void viewAlbums(ActionEvent actionEvent) throws IOException {
        refreshImages();
        voice.speak("View albums");
        Parent root = FXMLLoader.load(getClass().getResource("/Views/ViewAlbums.fxml"));
        if (!albumStage.isShowing()) {
            if (albumStage.getModality() != Modality.APPLICATION_MODAL)
                albumStage.initModality(Modality.APPLICATION_MODAL);
            albumStage.setScene(new Scene(root));
            albumStage.setTitle("Albums");
            albumStage.setResizable(false);
            albumStage.showAndWait();
        }
        if (ControllerViewAlbums.isAlbumSelected()) {
            if (!getSelectedImages().isEmpty()) {
                ControllerViewAlbums.setAlbumSelected(false);
                loadFromSelectedImages();
            } else {
                ControllerViewAlbums.setAlbumSelected(false);
            }
        }
    }

    /*
     * Loads images from selected paths
     *
     * @return true if images are selected, else false
     */
    private boolean loadFromSelectedImages() {
        if (!getSelectedImages().isEmpty()) {
            clearView();
            getSelectedImages().forEach(path -> {
                try {
                    insertImage(path);
                } catch (FileNotFoundException e) {
                    logger.logNewFatalError("ControllerMain loadFromSelectedImages" + e.getLocalizedMessage());
                }
            });
            clearSelectedImages();
            return true;
        } else return false;
    }

    /*
     * Speaks when hovering over the menu
     *
     * @param event event that led to this being called, e.g hovering over or clicking on menu
     */
    @FXML
    private void TextToSpeakOnMenu(Event event) {
        voice.speak(((MenuItem) event.getTarget()).getText());
    }

    /**
     * This method is called when the user presses the 'Preferences' item in the
     * 'File' menu. It opens creates and displays the preference stage to the
     * user, where they can choose to enable/disable certain settings.
     *
     * @param actionEvent the button being pressed
     * @throws IOException thrown if the FXML can't be found
     */
    public void preferencesAction(ActionEvent actionEvent) throws IOException {
        voice.speak("Preference settings");
        if (!preferenceStage.isShowing()) {
            if (preferenceStage.getModality() != Modality.APPLICATION_MODAL)
                preferenceStage.initModality(Modality.APPLICATION_MODAL);
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Preferences.fxml"));
            preferenceStage.setScene(new Scene(root));
            preferenceStage.setTitle("Preferences");
            preferenceStage.setResizable(false);
            preferenceStage.showAndWait();
        }

    }

    /**
     * This method is called when user presses the 'Add to album' item from
     * the 'File' menu. It creates and displays the addToAlbum stage where the
     * user can select which albums to add the images to.
     */
    public void addToAlbumAction() {
        voice.speak("Adding to album");
        logger.logNewInfo("Adding to album");
        if (!getSelectedImages().isEmpty()) {
            voice.speak("Adding to album");
            if (!addToAlbumStage.isShowing()) {
                if (!addToAlbumStage.getModality().equals(Modality.WINDOW_MODAL))
                    addToAlbumStage.initModality(Modality.WINDOW_MODAL);
                if (!addToAlbumStage.getStyle().equals(StageStyle.UTILITY))
                    addToAlbumStage.initStyle(StageStyle.UTILITY);
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/Views/SelectAlbums.fxml"));
                    addToAlbumStage.setScene(new Scene(root));
                    addToAlbumStage.setTitle("Adding to album");
                    addToAlbumStage.setResizable(false);
                    addToAlbumStage.showAndWait();
                } catch (Exception e) {
                    logger.logNewFatalError("ControllerMain addToAlbumAction " + e.getLocalizedMessage());
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "You need to select some images to add to the albums");
            ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(appIcon);
            alert.showAndWait();
        }
        refreshImages();
    }

    @FXML
    private void tintHome() {
        voice.speak("Home");
        buttonHome.setStyle("-fx-background-color:#0096c9;");
        homeLabel.setOpacity(1);
    }

    @FXML
    private void untintHome() {
        buttonHome.setStyle("-fx-background-color:transparent;");
        homeLabel.setOpacity(0);
    }
}

