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
import java.sql.SQLException;
import java.util.List;
import java.util.*;


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

    protected Stage searchStage = new Stage();
    @FXML
    protected SplitPane imgDataSplitPane;
    @FXML
    protected Label homeLabel;
    //Stages
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

    private Text_To_Speech voice = Text_To_Speech.getInstance();
    private int photoCount = 0;
    private int rowCount = 0;
    private int columnCount = 0;

    /**
     * returns instance of databaseclient to be used when adding images to database
     *
     * @return DatabaseClient instance
     */
    static DatabaseClient getDatabaseClient() {
        return databaseClient;
    }

    /**
     * Get current selected path
     *
     * @return pathBuffer string with path
     */
    static String getPathBuffer() {
        return pathBuffer;
    }

    /**
     * Set current selected path
     *
     * @param pathBuffer string with path
     */
    private static void setPathBuffer(String pathBuffer) {
        ControllerMain.pathBuffer = pathBuffer;
    }

    /**
     * Set login status
     *
     * @param b login status
     */
    static void setLoggedin(boolean b) {
        loggedin = b;
    }

    /**
     * Get split pane position
     *
     * @return double position
     */
    static double getSplitPanePos() {
        return splitPanePos;
    }

    /**
     * Set split pane posistion
     *
     * @param pos position
     */
    static void setSplitPanePos(double pos) {
        splitPanePos = pos;
    }

    /**
     * gets the albums currently saved
     *
     * @return hashmap with saved albums
     */
    static Map<String, List<String>> getAlbums() {
        return databaseClient.getAllAlbums();
    }

    /**
     * gets the current image buffer
     *
     * @return Image in image buffer
     */
    static Image getImageBuffer() {
        return imageBuffer;
    }

    /**
     * sets the imageBuffer
     *
     * @param imageBuffer image you want to set it to
     */
    private static void setImageBuffer(Image imageBuffer) {
        ControllerMain.imageBuffer = imageBuffer;
    }

    /**
     * @return returns a hashmap with all the images that have valid g
     */
    static HashMap<String, String> getLocations() {
        return locations;
    }

    /**
     * adds an album to the albums hashmap
     *
     * @param key    the name of the album
     * @param images arraylist containing the path to teh images
     */
    private static void newAlbum(String key, ArrayList<String> images) {
        databaseClient.addAlbum(key, images);
    }

    /**
     * adds the following paths to an existing album
     *
     * @param name   the name of the existing album
     * @param images Arraylist with the path to the images you want to add
     */
    static void addPathsToAlbum(String name, ArrayList<String> images) {
        databaseClient.addPathToAlbum(name, images);
    }

    /**
     * removes an album from the saved albums
     *
     * @param key the name of the album
     */
    static void removeAlbum(String key) {
        databaseClient.removeAlbum(key);
    }

    /**
     * gets all selected images
     *
     * @return returns all selected images
     */
    static ArrayList<String> getSelectedImages() {
        return selectedImages;
    }

    /**
     * adds a path to the selectedimages
     *
     * @param s the path that you want to add to the image
     */
    static void addToSelectedImages(String s) {
        selectedImages.add(s);
    }

    /**
     * clears the selected images
     */
    static void clearSelectedImages() {
        selectedImages.clear();
    }

    /**
     * removes a specific image from the selected images
     *
     * @param path the path to the image you want to remove
     */
    private static void removeFromSelectedImages(String path) {
        selectedImages.remove(path);
    }

    /**
     * tints the selected images blue
     *
     * @param imageInput the image that you want to tint
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
     * Run 1 time once the window opens
     *
     * @param location  auto generated
     * @param resources auto generated
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
     * When the search button is clicked
     */
    @FXML
    protected void searchAction(ActionEvent event) {
        logger.logNewInfo("SearchAction");
        voice.speak("Searching");
        if (!searchStage.isShowing()) {
            if (!searchStage.getModality().equals(Modality.APPLICATION_MODAL))
                searchStage.initModality(Modality.APPLICATION_MODAL);
            if (!searchStage.getStyle().equals(StageStyle.UTILITY)) searchStage.initStyle(StageStyle.UTILITY);
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/ScrollSearch.fxml"));
                searchStage.setScene(new Scene(root));
                searchStage.setTitle("Search");
                searchStage.setResizable(false);
                searchStage.showAndWait();
                if (ControllerSearch.isSearchSucceed()) {
                    //clears the view
                    clearView();
                    clearSelectedImages();
                    //clears metadata and tags
                    showMetadata();
                    showTags();
                    for (String s : ControllerSearch.getSearchResults()) {
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
     * Remove images from view and database
     *
     * @param event
     * @return true if something is deleted or false if nothing is deleted.
     * @throws SQLException
     * @throws IOException
     */
    @FXML
    protected boolean removeAction(ActionEvent event) throws SQLException, IOException {
        voice.speak("Removing images");
        try {
            if (getSelectedImages().isEmpty()) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("404: Image(s) not found");
                error.setHeaderText(null);
                error.setContentText("You need to select at least one image to remove");
                error.showAndWait();
                return false;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation Dialog");
            confirm.setHeaderText("Are you sure you want to remove " + getSelectedImages().size() + " images?");
            confirm.setContentText("This action is not revertible!");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() == ButtonType.OK) {
                Iterator<Map.Entry<String, List<String>>> albumIterator = getAlbums().entrySet().iterator();
                ArrayList<String> emptyAlbums = new ArrayList<>();
                //iterates through albums to find empty ones
                for (String path : getSelectedImages()) {
                    databaseClient.removeImage(path);
                }

                while (albumIterator.hasNext()) {
                    Map.Entry<String, List<String>> albumEntry = albumIterator.next();
                    //removes images, this deletion is cascaded to albums as well
                    //removes the images from the path
                    //needs to do this in order to make sure to delete the images that are empty
                    albumEntry.getValue().removeAll(selectedImages);
                    //if the last image was just removed, then the album is deleted
                    if (albumEntry.getValue().isEmpty()) {
                        //can't edit hashmap while iterating over it, so the albums to be removed are saved for later
                        emptyAlbums.add(albumEntry.getKey());
                    }
                }

                emptyAlbums.forEach(ControllerMain::removeAlbum);

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
     * Opens import window, once window closes, all pictures from database will get inserted into the UI
     */
    @FXML
    protected void importAction(ActionEvent event) throws IOException {
        voice.speak("Importing");
        if (!importStage.isShowing()) {
            if (importStage.getModality() != Modality.APPLICATION_MODAL)
                importStage.initModality(Modality.APPLICATION_MODAL);
            if (!importStage.getStyle().equals(StageStyle.UTILITY)) importStage.initStyle(StageStyle.UTILITY);
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

    /**
     * sort the pictures based on the selected value in the drop down
     */
    @FXML
    private void sortAction() throws FileNotFoundException {
        //if size is selected
        voice.speak("Sorting");
        if (sortDropDown.getValue().toString().equalsIgnoreCase("Size")) {
            ArrayList<String> sortedList = (ArrayList<String>) getDatabaseClient().sort("File_size");
            clearView();
            for (String s : sortedList) {
                insertImage(s);
            }
        }
        //if filename is selected
        else if (sortDropDown.getValue().toString().equalsIgnoreCase("Filename")) {
            //this is just a way to get an arraylist with the paths, theres no use for the sort function here
            ArrayList<String> sortedList = (ArrayList<String>) getDatabaseClient().sort("File_size");
            sortedList.sort(Comparator.comparing(o -> o.substring(o.lastIndexOf(File.separator))));
            clearView();
            for (String s : sortedList) {
                insertImage(s);
            }
        }
        //if path or date is selected
        else {
            List<String> sortedList = getDatabaseClient().sort(sortDropDown.getValue().toString());
            clearView();
            for (String s : sortedList) {
                insertImage(s);
            }
        }
    }

    /**
     * when the export button is clicked
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
            new Alert(Alert.AlertType.WARNING, "You need to select some images to export").showAndWait();
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
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "To the directory" + selectedDirectory.getPath() + "\n" + "With the filename: " + inputText + ".pdf");
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle("Success");
                alert.setHeaderText("Your album was exported successfully");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Something went wrong when attempting to save your selected images");
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle("Something went wrong");
                alert.setHeaderText("Unfortunately we were unable to export your album");
                alert.showAndWait();
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.WARNING, "You need to pick a valid filename for your album").showAndWait();
        }
    }

    /**
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
     * when go library is clicked, refreshes the images
     */
    @FXML
    protected void goToLibrary() throws IOException {
        voice.speak("Going to library");
        clearSelectedImages();
        refreshImages();

    }

    /**
     * Shows the about stage
     *
     * @throws IOException if About.fxml cannot be found
     */
    @FXML
    public void helpAction() throws IOException {
        voice.speak("Help");
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

    /**
     * Clears all rows on the gridView
     */
    private void clearView() {
        rowCount = 0;
        columnCount = 0;
        photoCount = 0;
        pictureGrid.getChildren().clear();
    }

    /**
     * Refresh home UI. Clear, then gets images from database into the view.
     */
    protected void refreshImages() {
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
        showMetadata();
        showTags();
    }

    /**
     * Insert image into the gridpane
     *
     * @param path to image object
     */
    void insertImage(String path) throws FileNotFoundException {
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

    /**
     * Add rows on the bottom of the gridpane
     */
    private void addEmptyRow() {
        RowConstraints con = new RowConstraints();
        con.setPrefHeight(185);
        pictureGrid.getRowConstraints().add(con);
    }

    /**
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

    /**
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
                    showMetadata();
                    showTags();
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

    /**
     * select the image and tints it, if it is already selected then the tint is removed
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
     * shows a image in fullscreen mode
     *
     * @param imageView the image that is to be shown
     * @param path      the path to the image
     * @throws IOException if BigImage.fxml cannot be found
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

    /**
     * gets rowcount, is used to facilitate increasing the number of rows when adding pictures
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


    /**
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
     * displays the metadata of the most recently selected image in the sidebar
     */
    void showMetadata() {
        if (selectedImages.isEmpty()) {
            metadataVbox.getChildren().clear();
            return;
        }
        String path = getSelectedImages().get(getSelectedImages().size() - 1);
        metadataVbox.getChildren().clear();
        String[] metadata = getDatabaseClient().getMetaDataFromDatabase(path);
        textField.setText("Path :" + metadata[0]);
        metadataVbox.getChildren().add(new Label("File size :" + metadata[2]));
        metadataVbox.getChildren().add(new Label("Date :" + metadata[3].substring(0, 4) + "/" + metadata[3].substring(4, 6) + "/" + metadata[3].substring(6)));
        metadataVbox.getChildren().add(new Label("Height :" + metadata[4]));
        metadataVbox.getChildren().add(new Label("Width :" + metadata[5]));
        metadataVbox.getChildren().add(new Label("GPS Latitude :" + metadata[6]));
        metadataVbox.getChildren().add(new Label("GPS Longitude :" + metadata[7]));

    }

    void showTags() {
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

    /**
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
     * when save to album is clicked
     *
     * @param actionEvent auto-generated
     * @throws IOException
     */
    @FXML
    protected void saveAlbumAction(ActionEvent actionEvent) throws IOException {
        voice.speak("Creating album");
        try {
            if (!getSelectedImages().isEmpty()) {
                TextInputDialog dialog = new TextInputDialog("");
                dialog.setTitle("Name picker");
                dialog.setHeaderText("Enter name for album:");
                dialog.setContentText("Please enter name for album:");
                Optional<String> result = dialog.showAndWait();

                if (result.isPresent()) {
                    if (!result.get().trim().equals("")) {
                        if (getAlbums().containsKey(result.get())) {
                            new Alert(Alert.AlertType.WARNING, "That album name already exists").showAndWait();
                        } else {
                            //the amount of characters that will fit inside each album icon
                            if(result.get().trim().length()>24){
                                new Alert(Alert.AlertType.WARNING, "Cannot save an album with more than 24 characters in the name").showAndWait();
                            }
                            else {
                                ArrayList<String> tempArray = new ArrayList<>(getSelectedImages());
                                newAlbum(result.get().trim(), tempArray);
                                new Alert(Alert.AlertType.INFORMATION, "Images were successfully added to the album [" + result.get().trim() + "]").showAndWait();
                                refreshImages();
                            }
                        }
                    } else {
                        new Alert(Alert.AlertType.WARNING, "You cant save an album using a blank name").showAndWait();
                        refreshImages();
                    }
                } else {
                    refreshImages();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "You need to select some images to save to an album").showAndWait();
            }
            clearSelectedImages();
        } catch (Exception exception) {
            logger.logNewFatalError("ControllerMain saveAlbumAction " + exception.getLocalizedMessage());
        }
        refreshImages();
    }

    /**
     * When view albums is clicked
     *
     * @param actionEvent auto-generated
     * @throws IOException
     */
    @FXML
    protected void viewAlbums(ActionEvent actionEvent) throws IOException {
        refreshImages();
        voice.speak("View albums");
        Parent root = FXMLLoader.load(getClass().getResource("/Views/ViewAlbums.fxml"));
        Stage albumStage = new Stage();
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

    /**
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
                    logger.logNewFatalError("Controllermain loadfromSelectedImages" + e.getLocalizedMessage());
                }
            });
            clearSelectedImages();
            return true;
        } else return false;
    }

    /**
     * speaks when hovering over the menu
     *
     * @param event event that led to this being called, e.g hovering over or clicking on menu
     */
    @FXML
    private void TextToSpeakOnMenu(Event event) {
        voice.speak(((MenuItem) event.getTarget()).getText());
    }

    /**
     * opens prefrences window
     *
     * @param actionEvent auto-generated
     * @throws IOException
     */
    public void preferencesAction(ActionEvent actionEvent) throws IOException {
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
     * when add to album is clicked
     */
    public void addToAlbumAction() {
        logger.logNewInfo("adding to album");
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
                    addToAlbumStage.setTitle("Search");
                    addToAlbumStage.setResizable(false);
                    addToAlbumStage.showAndWait();
                } catch (Exception e) {
                    logger.logNewFatalError("ControllerMain addToAlbumAction " + e.getLocalizedMessage());
                }
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "You need to select some images to add to the albums").showAndWait();
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

