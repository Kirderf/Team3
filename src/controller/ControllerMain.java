package controller;

import backend.DatabaseClient;
import backend.Text_To_Speech;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
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

    public static Text_To_Speech voice;
    public static HashMap<String, String> locations = new HashMap<>();
    public static DatabaseClient databaseClient = new DatabaseClient();
    public static Stage importStage = new Stage();
    public static Stage searchStage = new Stage();
    public static Stage exportStage = new Stage();
    public static Stage worldStage = new Stage();
    public static Stage albumStage = new Stage();
    public static Stage aboutStage = new Stage();
    public static Stage errorStage = new Stage();
    public static Stage addTagStage = new Stage();
    private Stage preferenceStage = new Stage();
    private Stage albumNameStage = new Stage();

    public static ArrayList<String> selectedImages = new ArrayList<>();
    public static HashMap<String, ArrayList<String>> albums = new HashMap<>();
    protected static Image imageBuffer;
    protected static String pathBuffer;
    protected static boolean loadedFromAnotherLocation = false;
    private static boolean ascending = true;
    private final double initialGridHeight = 185;
    public Menu fileButton;
    private ArrayList<String> displayedImages = new ArrayList<>();
    @FXML
    public MenuItem about;
    @FXML
    private GridPane pictureGrid;
    @FXML
    private ComboBox<?> sortDropDown;
    @FXML
    private TextArea pathDisplay;
    @FXML
    private VBox metadataVbox;

    private int photoCount = 0;
    private int rowCount = 0;
    private int columnCount = 0;

    /**
     * Run 1 time once the window opens
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //when i have the modality anywhere else i get an illegalstateexception
        errorStage.initModality(Modality.APPLICATION_MODAL);
        preferenceStage.initModality(Modality.APPLICATION_MODAL);
        voice = new Text_To_Speech();
        logger.log(Level.INFO, "Initializing");
        //this is required, as disabling the textfield in the fxml file made the path way too light to see
        pathDisplay.setEditable(false);
        pictureGrid.setAlignment(Pos.CENTER);
        if (loadedFromAnotherLocation) {
            refreshImages();
            loadedFromAnotherLocation = false;
        }

    }

    public ArrayList<String> getSelectedImages(){
        return selectedImages;
    }
    public void addToSelectedImages(String s){
        selectedImages.add(s);
    }
    @FXML
    private void searchAction() {
        logger.log(Level.INFO, "SearchAction");
        voice.speak("Searching");
        if (!searchStage.isShowing()) {
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

    /**
     * Opens import window, once window closes, all pictures from database will get inserted into the UI
     */
    @FXML
    protected void importAction(ActionEvent event) throws IOException {
        voice.speak("Importing");
        if (!importStage.isShowing()) {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Import.fxml"));
            importStage.setScene(new Scene(root));
            importStage.setTitle("Import");
            importStage.setResizable(false);
            importStage.showAndWait();
            if (ControllerImport.isImportSucceed()) {
                voice.speak("Import succeeded");
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
            ArrayList<String> sortedList = databaseClient.sort("File_size", ascending);
            clearView();
            for (String s : sortedList) {
                insertImage(s);
            }
        }
        //if location is selected
        else if (sortDropDown.getValue().toString().equalsIgnoreCase("Location")) {
            //TODO make this work
            //I am thinking that we will sort based on the sum of latitude and longitude for the moment
        } else if (sortDropDown.getValue().toString().equalsIgnoreCase("Filename")) {
            //this is just a way to get an arraylist with the paths, theres no use for the sort function here
            ArrayList<String> sortedList = databaseClient.sort("File_size", ascending);
            sortedList.sort(Comparator.comparing(o -> o.substring(o.lastIndexOf("/"))));
            clearView();
            for (String s : sortedList) {
                insertImage(s);
            }
        }
        //if path or date is selected
        else {
            //ascending changes value every time this function is called
            ArrayList<String> sortedList = databaseClient.sort(sortDropDown.getValue().toString(), ascending);
            clearView();
            for (String s : sortedList) {
                insertImage(s);
            }
        }
    }

    @FXML
    private void exportAction() {
        voice.speak("Exporting");
        if (!exportStage.isShowing()) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/Export.fxml"));
                exportStage.setScene(new Scene(root));
                exportStage.setTitle("Export");
                exportStage.setResizable(false);
                exportStage.showAndWait();
                //exportSucceed is a static variable in controllerExport
                if (ControllerExport.isExportSucceed()) {
                   if(this.getClass() == ControllerMain.class){
                       refreshImages();
                   }
                    ControllerExport.setExportSucceed(false);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        selectedImages.clear();
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
            databaseClient.closeApplication();
            Platform.exit();
            System.exit(0);
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "Could not close application / delete table");
        }
    }

    @FXML
    protected void goToLibrary() {
        voice.speak("Going to library");
        //when this uses selectedImages.clear it causes a bug with the albums, clearing them as well
        //selectedImages = new ArrayList<String>();
        selectedImages.clear();
        refreshImages();
    }

    @FXML
    public void helpAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Views/About.fxml"));
        voice.speak("Help");
        aboutStage.setScene(new Scene(root));
        aboutStage.setTitle("About");
        aboutStage.setResizable(false);
        aboutStage.showAndWait();
    }

    /**
     * Clears all rows on the gridView
     */
    private void clearView() {
        Node node = pictureGrid.getChildren().get(0); // to retain gridlines
        rowCount = 0;
        columnCount = 0;
        photoCount = 0;
        if (pictureGrid != null) {
            pictureGrid.getChildren().clear();
            pictureGrid.getChildren().add(0, node);
        }
    }

    /**
     * Refresh Start UI
     */
    protected void refreshImages() {
        try {
            ArrayList paths = databaseClient.getColumn("Path");
            clearView();
            for (Object obj : paths) {
                //the view is cleared, so there's no use checking if the image has been added as there are no added photos to start with
                if (obj != null ) {
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
     *
     * @param path to image object
     */
    private void insertImage(String path) throws FileNotFoundException {
        int row = getNextRow();
        int coloumn = getNextColumn();
        pictureGrid.add(importImage(path), coloumn, row);
        photoCount++;
    }

    /**
     * Add rows on the bottom of the gridpane
     */
    private void addEmptyRow() {
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
        imageView.fitHeightProperty().bind(pictureGrid.getRowConstraints().get(0).prefHeightProperty());
        imageView.fitWidthProperty().bind(pictureGrid.widthProperty().divide(5));
        imageView.setOnMouseClicked(onImageClickedEvent(imageView, image, path));
        return imageView;
    }

    /**
     * EventHandler for mouseclicks on images
     *
     * @param imageView
     * @param image
     * @param path
     * @return
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
                setSelectedImages(imageView, image, path);
                showMetadata(null);
                if (selectedImages.size() != 1) {
                    pathDisplay.clear();
                    metadataVbox.getChildren().clear();
                }
            }

        };
    }

    private void setSelectedImages(ImageView imageView, Image image, String path) {
        if (!selectedImages.contains(path)) {
            selectedImages.add(path);
            //buff is the tinted
            BufferedImage buff = SwingFXUtils.fromFXImage(image, null);
            tint(buff);
            imageView.setImage(SwingFXUtils.toFXImage(buff, null));
        } else {
            selectedImages.remove(path);
            imageView.setImage(image);
        }
    }

    private void showBigImage(ImageView imageView, String path) throws IOException {
        voice.speak("Magnifying image");
        selectedImages.clear();
        pathBuffer = path;
        imageBuffer = imageView.getImage();
        Scene scene = pictureGrid.getScene();
        scene.setRoot(FXMLLoader.load(getClass().getResource("/Views/BigImage.fxml")));
    }

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

    public void showMetadata(String imagePath) {
        String path;
        if (!metadataVbox.getChildren().isEmpty()) {
            metadataVbox.getChildren().clear();
        }
        if (imagePath != null) {
            path = imagePath;
        } else if (selectedImages.size() != 0) {
            path = selectedImages.get(0);
        } else {
            return;
        }
        if (path == null || selectedImages.size() > 1) {
            return;
        }
        int i = 0;

        for (String s : databaseClient.getMetaDataFromDatabase(path)) {
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
        voice.speak("Showing map");
        ArrayList paths = databaseClient.getColumn("Path");
        //do this by checking ration of long at latitiude according to image pixel placing
        //add them to the worldmap view with event listener to check when they're clicked
        for (int i = 0; i < databaseClient.getColumn("GPS_Longitude").size(); i++) {
            Double longitude = Double.parseDouble(databaseClient.getMetaDataFromDatabase((String)paths.get(i))[7]);
            Double latitude = Double.parseDouble(databaseClient.getMetaDataFromDatabase((String)paths.get(i))[6]);
            //if both are not equal to zero, maybe this should be changed to an or
            if (longitude != 0 && latitude != 0) {
                locations.put((String) paths.get(i), "" + longitude + "," + latitude);
            }
        }
        Parent root = FXMLLoader.load(getClass().getResource("/Views/WorldMap.fxml"));
        worldStage.setScene(new Scene(root));
        worldStage.setTitle("Map");
        worldStage.setResizable(false);
        worldStage.showAndWait();
        if (ControllerMap.clickedImage != null) {
            showBigImage(ControllerMap.clickedImage, ControllerMap.clickedImage.getId());
        }
    }

    public void saveAlbumAction(ActionEvent actionEvent) throws IOException {
        voice.speak("Creating album");
        if (!albumNameStage.isShowing()) {
            try {
                if(selectedImages.size()==0) {
                    throw new IllegalArgumentException("You need to select more than one image for your album");
                }
                Parent root = FXMLLoader.load(getClass().getResource("/Views/AlbumNamePicker.fxml"));
                albumNameStage.setScene(new Scene(root));
                albumNameStage.setTitle("Save album");
                albumNameStage.setResizable(false);
                //disables back stage
                albumNameStage.showAndWait();
                //exportSucceed is a static variable in controllerExport
                if (!ControllerAlbumNamePicker.savedName.trim().equals("")) {
                    if (albums.containsKey(ControllerAlbumNamePicker.savedName)) {
                        throw new IllegalArgumentException("That name already exists");
                    } else {
                        refreshImages();
                        albums.put(ControllerAlbumNamePicker.savedName, new ArrayList<>());
                        ArrayList<String> tempArray = new ArrayList<>();
                        for (String s : selectedImages) {
                            albums.get(ControllerAlbumNamePicker.savedName).add(s);
                            tempArray.add(s);
                        }
                        selectedImages.clear();
                        Collections.copy(albums.get(ControllerAlbumNamePicker.savedName), tempArray);
                        ControllerAlbumNamePicker.savedName = "";
                    }
                }
            } catch(IllegalArgumentException e){
                //TODO add logger
                Parent root = FXMLLoader.load(getClass().getResource("/Views/AlbumNameError.fxml"));
                errorStage.setScene(new Scene(root));
                errorStage.setTitle("Albums");
                errorStage.setResizable(false);
                errorStage.setAlwaysOnTop(true);
                errorStage.showAndWait();
                albumNameStage.setAlwaysOnTop(false);
            } catch (Exception exception) {
                //TODO change to logger
                exception.printStackTrace();
            }
        }
        selectedImages.clear();
    }

    public void viewAlbums(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Views/ViewAlbums.fxml"));
        albumStage.setScene(new Scene(root));
        albumStage.setTitle("Albums");
        albumStage.setResizable(false);
        albumStage.showAndWait();
        if (ControllerViewAlbums.isAlbumSelected()) {
            if(!selectedImages.isEmpty()) {
                clearView();
                ControllerViewAlbums.setAlbumSelected(false);
                for (String s : selectedImages) {
                    insertImage(s);
                }
            }
            else{
                refreshImages();
            }
        }
    }

    /**
     * tints the selected images blue
     *
     * @param image the image that you want to tint
     */
    //TODO check if any of the other methods on stackoverflow tint quicker
    private static void tint(BufferedImage image) {
        //stolen from https://stackoverflow.com/a/36744345
        //if colourblind
        if(ControllerPreferences.isColourChecked()){
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if((x<2*image.getWidth()/3&&x>image.getHeight()/3)||(y<2*image.getHeight()/3&&y>image.getHeight()/3)) {
                        Color black = new Color(0, 0, 0);
                        image.setRGB(x, y, black.getRGB());
                    }
                }
            }
        }
        else {
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

    public void TextToSpeakOnMenu(Event event) {
        voice.speak(((Menu) event.getSource()).getText());
    }

    public void prefrencesAction(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Views/Preferences.fxml"));
        preferenceStage.setScene(new Scene(root));
        preferenceStage.setTitle("Albums");
        preferenceStage.setResizable(false);
        preferenceStage.showAndWait();
    }
}

