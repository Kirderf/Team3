package controller;

import backend.DatabaseClient;
import backend.ImageExport;
import backend.Text_To_Speech;
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
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ControllerMain implements Initializable {
    private static final Logger logger = Logger.getLogger(ControllerMain.class.getName());

    //Must be public static to get access from other places
    public static HashMap<String, String> locations = new HashMap<>();
    public static DatabaseClient databaseClient = new DatabaseClient();
    public static String pathBuffer;
    public static boolean ascending = true;
    public static ArrayList<String> selectedImages = new ArrayList<>();
    public static HashMap<String, ArrayList<String>> albums = new HashMap<>();
    public static Image imageBuffer;

    //Stages
    private Stage importStage = new Stage();
    private Stage albumNameStage = new Stage();
    private Stage searchStage = new Stage();
    private Stage aboutStage = new Stage();
    private Stage worldStage = new Stage();
    private Stage preferenceStage = new Stage();
    private Stage addToAlbumStage = new Stage();

    //Nodes
    @FXML
    private GridPane pictureGrid;
    @FXML
    private ComboBox<?> sortDropDown;
    @FXML
    private TextArea pathDisplay;
    @FXML
    private VBox metadataVbox;

    private Text_To_Speech voice;
    private int photoCount = 0;
    private int rowCount = 0;
    private int columnCount = 0;

    /**
     * Run 1 time once the window opens
     *
     * @param location  auto generated
     * @param resources auto generated
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //when i have the modality anywhere else i get an illegalstateexception
        voice = new Text_To_Speech();
        logger.log(Level.INFO, "Initializing");
        //this is required, as disabling the textfield in the fxml file made the path way too light to see
        pathDisplay.setEditable(false);
        pictureGrid.setAlignment(Pos.CENTER);
        refreshImages();
    }

    /**
     * returns instance of databaseclient to be used when adding images to database
     * @return DatabaseClient instance
     */
    public static DatabaseClient getDatabaseClient() {
        return databaseClient;
    }

    public static String getPathBuffer() {
        return pathBuffer;
    }

    public static void setPathBuffer(String pathBuffer) {
        ControllerMain.pathBuffer = pathBuffer;
    }

    /**
     * gets the albums currently saved
     * @return hashmap with saved albums
     */
    public static HashMap<String, ArrayList<String>> getAlbums() {
        return albums;
    }

    /**
     * gets the current image buffer
     * @return Image in image buffer
     */
    public static Image getImageBuffer() {
        return imageBuffer;
    }

    /**
     * sets the imageBuffer
     * @param imageBuffer image you want to set it to
     */
    public static void setImageBuffer(Image imageBuffer) {
        ControllerMain.imageBuffer = imageBuffer;
    }

    /**
     * returns a hasmap with all the images that have valid g
     * @return
     */
    public static HashMap<String, String> getLocations() {
        return locations;
    }

    public static void setLocations(HashMap<String, String> locations) {
        ControllerMain.locations = locations;
    }

    /**
     * adds an album to the albums hashmap
     * @param key the name of the album
     * @param images arraylist containing the path to teh images
     */
    public static void addToAlbums(String key, ArrayList<String> images) {
        ControllerMain.albums.put(key, images);
    }

    /**
     * removes an album from the saved albums
     * @param key the name of the album
     * @return arraylist with the removed images
     */
    public static ArrayList removeAlbum(String key) {
        return albums.remove(key);
    }

    /**
     * gets all selected images
     * @return returns all selected images
     */
    public static ArrayList<String> getSelectedImages() {
        return selectedImages;
    }

    /**
     * clears the selected images and sets it equal to the new arraylist
     * @param s the new arraylist with image paths
     */
    public static void setSelectedImages(ArrayList<String> s) {
        selectedImages.clear();
        selectedImages = s;
    }

    /**
     * adds a path to the selectedimages
     * @param s the path that you want to add to the image
     */
    public static void addToSelectedImages(String s) {
        selectedImages.add(s);
    }

    /**
     * clears the selected images
     */
    public static void clearSelectedImages() {
        selectedImages.clear();
    }

    /**
     * removes a specific image from the selected images
     * @param path the path to the image you want to remove
     * @return boolean whether or not the removal was successful
     */
    public static boolean removeFromSelectedImages(String path) {
        return selectedImages.remove(path);
    }

    /**
     * When the search button is clicked
     */
    @FXML
    private void searchAction() {
        logger.log(Level.INFO, "SearchAction");
        voice.speak("Searching");
        if (!searchStage.isShowing()) {
            if(!searchStage.getModality().equals(Modality.APPLICATION_MODAL)) searchStage.initModality(Modality.APPLICATION_MODAL);
            if(!searchStage.getStyle().equals(StageStyle.UTILITY)) searchStage.initStyle(StageStyle.UTILITY);
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/ScrollSearch.fxml"));
                searchStage.setScene(new Scene(root));
                searchStage.setTitle("Search");
                searchStage.setResizable(false);
                searchStage.showAndWait();
                if (ControllerSearch.searchSucceed) {
                    clearView();
                    for (String s : ControllerSearch.searchResults) {
                        insertImage(s);
                        ControllerSearch.searchSucceed = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void removeAction(ActionEvent event) throws SQLException {
        voice.speak("Removing images");
        try {
            if (getSelectedImages().size() == 0) {
                throw new IllegalArgumentException("No images were chosen");
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation Dialog");
            confirm.setHeaderText("Are you sure you want to remove " + getSelectedImages().size() + " images?");
            confirm.setContentText("This action is not revertable!");
            Optional<ButtonType> result = confirm.showAndWait();
            if(result.get()==ButtonType.OK){
                for (String path : getSelectedImages()) {
                    databaseClient.removeImage(path);
                    Iterator albumIterator = albums.entrySet().iterator();
                    //iterates through albums
                    while(albumIterator.hasNext()){
                        Map.Entry albumEntry = (Map.Entry)albumIterator.next();
                        //if the image is in the album then it is removed
                        ((ArrayList<String>)albumEntry.getValue()).remove(path);
                        //if the last image was just removed, then the album is deleted
                        if(((ArrayList<String>)albumEntry.getValue()).isEmpty()){
                            albums.remove(albumEntry.getKey());
                        }
                    }
                    refreshImages();
                }
            }else{
                refreshImages();
            }

        } catch (IllegalArgumentException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("404: Image(s) not found");
            error.setHeaderText(null);
            error.setContentText("You need to select at least one image to remove");
            error.showAndWait();
        }
    }

    /**
     * Opens import window, once window closes, all pictures from database will get inserted into the UI
     */
    @FXML
    private void importAction(ActionEvent event) throws IOException {
        voice.speak("Importing");
        if (!importStage.isShowing()) {
            if(importStage.getModality() != Modality.APPLICATION_MODAL) importStage.initModality(Modality.APPLICATION_MODAL);
            if (!importStage.getStyle().equals(StageStyle.UTILITY)) importStage.initStyle(StageStyle.UTILITY);
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Import.fxml"));
            importStage.setScene(new Scene(root));
            importStage.setTitle("Import");
            importStage.setResizable(false);
            importStage.showAndWait();
            if (ControllerImport.isImportSucceed()) {
                if(ControllerPreferences.isTtsChecked()) voice.speak("Import succeeded");
                logger.log(Level.INFO, "Refreshing");
                refreshImages();
                ControllerImport.setImportSucceed(false);
            }

        }
    }

    /**
     * sort the pictures based on the selected value in the drop down
     */
    @FXML
    private void sortAction() throws SQLException, FileNotFoundException {
        //if size is selected
        voice.speak("Sorting");
        if (sortDropDown.getValue().toString().equalsIgnoreCase("Size")) {
            ArrayList<String> sortedList = getDatabaseClient().sort("File_size", ascending);
            clearView();
            for (String s : sortedList) {
                insertImage(s);
            }
        }
        //if filename is selected
        else if (sortDropDown.getValue().toString().equalsIgnoreCase("Filename")) {
            //this is just a way to get an arraylist with the paths, theres no use for the sort function here
            ArrayList<String> sortedList = getDatabaseClient().sort("File_size", ascending);
            sortedList.sort(Comparator.comparing(o -> o.substring(o.lastIndexOf("/"))));
            clearView();
            for (String s : sortedList) {
                insertImage(s);
            }
        }
        //if path or date is selected
        else {
            //ascending changes value every time this function is called
            ArrayList<String> sortedList = getDatabaseClient().sort(sortDropDown.getValue().toString(), ascending);
            clearView();
            for (String s : sortedList) {
                insertImage(s);
            }
            if (ascending) {
                ascending = false;
            } else {
                ascending = true;
            }
        }
    }

    /**
     * when the export button is clicked
     */
    @FXML
    private void exportAction() {
        voice.speak("Exporting");
        if(!getSelectedImages().isEmpty()) {
            Stage exportStage = new Stage();
            if (!exportStage.isShowing()) {
                try {
                    TextInputDialog dialog = new TextInputDialog("");
                    dialog.setTitle("Name picker");
                    dialog.setHeaderText("Enter name for pdf:");
                    dialog.setContentText("Please enter name for pdf:");
                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()){
                        exportPDF(result.get());
                    }
                    else{
                        selectedImages.clear();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            refreshImages();
        }
        else{
            new Alert(Alert.AlertType.WARNING, "You need to select some images to export").showAndWait();
        }
    }

    private boolean exportPDF(String inputText) throws IOException {
        File f = new File("/"+inputText + ".txt");
        try {
            if(inputText.trim().equals("")) throw new IOException("Invalid filename inputted");
            //this throws error is filename is invalid
            System.out.println(inputText);
            f.getCanonicalPath();
            //chooses album location after selecting name
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Choose folder for album");
            //the directory that the file chooser starts in
            File defaultDirectory = new File("/");
            chooser.setInitialDirectory(defaultDirectory);
            File selectedDirectory = chooser.showDialog(null);
            //gets the filename from the user and formats it correctly
            if(ImageExport.exportToPdf(selectedDirectory.getPath() +"/"+ inputText + ".pdf",getSelectedImages())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "To the directory" + selectedDirectory.getPath() +"\n" + "With the filename: " + inputText + ".pdf");
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle("Success");
                alert.setHeaderText("Your album was exported successfully");
                alert.showAndWait();
                return true;
            }
            else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Something went wrong when attempting to save your selected");
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle("Something went wrong");
                alert.setHeaderText("Unfortunately we were unable to export your album");
                alert.showAndWait();
                return false;
            }
        }
        catch (IOException e) {
            new Alert(Alert.AlertType.WARNING, "You need to pick a valid filename for your album").showAndWait();
        }
        return false;
    }


    /**
     * Closes application, and closes connections to database. Cannot close if other windows are open
     */
    @FXML
    private void quitAction() {
        try {
            voice.speak("Closing application");
            Thread.sleep(1500);
            logger.log(Level.WARNING, "Closing application");
            getDatabaseClient().closeApplication();
            Platform.exit();
            System.exit(0);
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "Could not close application / delete table");
        }
    }

    /**
     * when go library is clicked, refreshes the images
     */
    @FXML
    protected void goToLibrary() {
        voice.speak("Going to library");
        //when this uses selectedImages.clear it causes a bug with the albums, clearing them as well
        //selectedImages = new ArrayList<String>();
        clearSelectedImages();
        refreshImages();
    }

    /**
     * Shows the about stage
     *
     * @param actionEvent auto generated
     * @throws IOException
     */
    @FXML
    public void helpAction(ActionEvent actionEvent) throws IOException {
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
            ArrayList paths = getDatabaseClient().getColumn("Path");
            clearView();
            for (Object obj : paths) {
                //the view is cleared, so there's no use checking if the image has been added as there are no added photos to start with
                if (obj != null) {
                    insertImage((String) obj);
                }
            }
        } catch (FileNotFoundException | SQLException e) {
            //TODO change this to logger
            System.out.println(e.getLocalizedMessage());
        }
    }

    /**
     * Insert image into the gridpane
     * @param path to image object
     */
    private void insertImage(String path) throws FileNotFoundException {
        int row = getNextRow();
        int coloumn = getNextColumn();
        ImageView image = importImage(path);
        Pane p = new Pane();
        p.setStyle("-fx-border-color: black; -fx-background-color: white");
        pictureGrid.add(p, coloumn, row);
        pictureGrid.add(image, coloumn, row);
        pictureGrid.setHalignment(image, HPos.CENTER);
        photoCount++;
    }

    /**
     * Add rows on the bottom of the gridpane
     */
    private void addEmptyRow() {
        double initialGridHeight = 185;
        double gridHeight = initialGridHeight;
        RowConstraints con = new RowConstraints();
        con.setPrefHeight(gridHeight);
        pictureGrid.getRowConstraints().add(con);
    }

    /**
     * Take a path, and create a ImageView that fits to a space in the grid
     *
     * @param path to image
     */
    private ImageView importImage(String path) throws FileNotFoundException {
        Image image = new Image(new FileInputStream(path));
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
        imageView.setOnMouseClicked(onImageClickedEvent(imageView, image, path));
        return imageView;
    }

    /**
     * EventHandler for mouseclicks on images
     * in the event of an control click then big image is shown
     * normal clicks tags the image and adds it to selected images
     *
     * @param imageView the image that you want to view or mark when clicked
     * @param image     the image that the imageview should display
     * @param path      the local path to the image on the user's machine
     * @return eventhandler that marks the images as directed
     */
    private javafx.event.EventHandler<? super javafx.scene.input.MouseEvent> onImageClickedEvent(ImageView imageView, Image image, String path) {
        return (EventHandler<MouseEvent>) event -> {
            //Ctrl click
            if (event.isControlDown()) {
                imageView.setImage(image);
                try {
                    showBigImage(imageView, path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //Single click
                selectImage(imageView, image, path);
                showMetadata(null);
                if (getSelectedImages().size() != 1) {
                    pathDisplay.clear();
                    metadataVbox.getChildren().clear();
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
            //buff is the tinted
            BufferedImage buff = SwingFXUtils.fromFXImage(image, null);
            tint(buff);
            imageView.setImage(SwingFXUtils.toFXImage(buff, null));
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
     * @throws IOException
     */
    private void showBigImage(ImageView imageView, String path) throws IOException {
        voice.speak("Magnifying image");
        clearSelectedImages();
        setPathBuffer(path);
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

    //for every 5th picture, the coloumn will reset. Gives the coloumn of the next imageview

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
     * displays the metadata of each image in the sidebar
     *
     * @param imagePath the path to the image from which you are getting the metadata
     */
    public void showMetadata(String imagePath) {
        String path;
        if (!metadataVbox.getChildren().isEmpty()) {
            metadataVbox.getChildren().clear();
        }
        if (imagePath != null) {
            path = imagePath;
        } else if (!getSelectedImages().isEmpty()) {
            path = getSelectedImages().get(0);
        } else {
            return;
        }
        if (path == null || getSelectedImages().size() > 1) {
            return;
        }
        int i = 0;

        for (String s : getDatabaseClient().getMetaDataFromDatabase(path)) {
            switch (i) {
                case 0:
                    metadataVbox.getChildren().add(new Label("Path :" + s));
                    if (!(this instanceof ControllerBigImage)) pathDisplay.setText("Path :" + s);
                    break;
                case 1:
                    break;
                case 2:
                    metadataVbox.getChildren().add(new Label("File size :" + s));
                    break;
                case 3:
                    metadataVbox.getChildren().add(new Label("Date :" + s.substring(0, 4) + "/" + s.substring(4, 6) + "/" + s.substring(6)));
                    break;
                case 4:
                    metadataVbox.getChildren().add(new Label("Height :" + s));
                    break;
                case 5:
                    metadataVbox.getChildren().add(new Label("Width :" + s));
                    break;
                case 6:
                    metadataVbox.getChildren().add(new Label("GPS Latitude :" + s));
                    break;
                case 7:
                    metadataVbox.getChildren().add(new Label("GPS Longitude :" + s));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + i);
            }
            i++;
        }

    }

    /**
     * When the user clicks on goToMap under library
     * Checks all the added photos for valid gps data, and places the ones with valid data on the map
     *
     * @throws IOException
     * @throws SQLException
     */
    public void goToMap() throws IOException, SQLException {
        if (!worldStage.isShowing() && worldStage.getModality() != Modality.APPLICATION_MODAL) {
            worldStage.initModality(Modality.APPLICATION_MODAL);
        }
        voice.speak("Showing map");
        ArrayList paths = getDatabaseClient().getColumn("Path");
        //do this by checking ration of long at latitiude according to image pixel placing
        //add them to the worldmap view with event listener to check when they're clicked
        for (int i = 0; i < getDatabaseClient().getColumn("GPS_Longitude").size(); i++) {
            Double latitude = Double.parseDouble(getDatabaseClient().getMetaDataFromDatabase((String) paths.get(i))[6]);
            Double longitude = Double.parseDouble(getDatabaseClient().getMetaDataFromDatabase((String) paths.get(i))[7]);
            //if both are not equal to zero, maybe this should be changed to an or
            if (longitude != 0 && latitude != 0) {
                getLocations().put((String) paths.get(i), "" + latitude + "," + longitude);
            }
        }

        Parent root = FXMLLoader.load(getClass().getResource("/Views/WorldMap.fxml"));
        worldStage.setScene(new Scene(root));
        worldStage.setTitle("Map");
        worldStage.setResizable(false);
        worldStage.showAndWait();

        for(String s : ControllerMap.getSavedToDisk()){
            new File(s).delete();
        }
        ControllerMap.emptySavedToDisk();


        if (ControllerMap.getClickedImage() != null) {
            showBigImage(ControllerMap.getClickedImage(), ControllerMap.getClickedImage().getId());
        }
    }

    /**
     * when save to album is clicked
     * @param actionEvent auto-generated
     * @throws IOException
     */
    public void saveAlbumAction(ActionEvent actionEvent) throws IOException {
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
                        if (albums.containsKey(result.get())) {
                            new Alert(Alert.AlertType.WARNING, "That album name already exists").showAndWait();
                            clearSelectedImages();
                        }
                        else {
                            refreshImages();
                            albums.put(result.get(), new ArrayList<>());
                            ArrayList<String> tempArray = new ArrayList<>();
                            for (String s : getSelectedImages()) {
                                albums.get(result.get()).add(s);
                                tempArray.add(s);
                            }
                            clearSelectedImages();
                            Collections.copy(albums.get(result.get()), tempArray);
                        }
                    }
                    else {
                        new Alert(Alert.AlertType.WARNING, "You cant save an album using a blank name").showAndWait();
                        refreshImages();
                    }
                }
                else {
                    refreshImages();
                }
            }
            else {
                new Alert(Alert.AlertType.WARNING, "You need to select some images to save to an album").showAndWait();
            }
            clearSelectedImages();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * When view albums is clicked
     *
     * @param actionEvent auto-generated
     * @throws IOException
     */
    public void viewAlbums(ActionEvent actionEvent) throws IOException {
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
                clearView();
                ControllerViewAlbums.setAlbumSelected(false);
                for (String s : getSelectedImages()) {
                    insertImage(s);
                }
            } else {
                refreshImages();
            }
        }
    }

    /**
     * tints the selected images blue
     *
     * @param image the Bufferedimage that you want to tint
     */
    //TODO take in image as parameter, and convert to buffered image inside the method
    //TODO check if any of the other methods on stackoverflow tint quicker
    private static void tint(BufferedImage image) {
        //if colourblind
        if (ControllerPreferences.isColourChecked()) {
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if ((x < 2 * image.getWidth() / 3 && x > image.getHeight() / 3) || (y < 2 * image.getHeight() / 3 && y > image.getHeight() / 3)) {
                        Color black = new Color(0, 0, 0);
                        image.setRGB(x, y, black.getRGB());
                    }
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
    }

    /**
     * speaks when hovering over the menu
     *
     * @param event event that led to this being called, e.g hovering over or clicking on menu
     */
    public void TextToSpeakOnMenu(Event event) {
        voice.speak(((MenuItem) event.getSource()).getText());
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

    public void addToAlbumAction(ActionEvent actionEvent) {
        logger.log(Level.INFO, "adding to album");
        if(!getSelectedImages().isEmpty()) {
            voice.speak("Adding to album");
            if (!addToAlbumStage.isShowing()) {
                if (!addToAlbumStage.getModality().equals(Modality.APPLICATION_MODAL))
                    addToAlbumStage.initModality(Modality.APPLICATION_MODAL);
                if (!addToAlbumStage.getStyle().equals(StageStyle.UTILITY)) addToAlbumStage.initStyle(StageStyle.UTILITY);
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/Views/SelectAlbums.fxml"));
                    addToAlbumStage.setScene(new Scene(root));
                    addToAlbumStage.setTitle("Search");
                    addToAlbumStage.setResizable(false);
                    addToAlbumStage.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            new Alert(Alert.AlertType.WARNING, "You need to select some images to add to the albums").showAndWait();
        }
        refreshImages();
    }
}

