package controller;

import backend.DatabaseClient;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
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
    public static HashMap<String, String> locations = new HashMap<>();
    public static DatabaseClient databaseClient = new DatabaseClient();
    public static Stage importStage = new Stage();
    public static Stage searchStage = new Stage();
    public static Stage exportStage = new Stage();
    public static Stage worldStage = new Stage();
    public static Stage albumStage = new Stage();
    protected static Image imageBuffer;
    protected static String pathBuffer;
    public static boolean loadedFromAnotherLocation = false;
    public static ArrayList<String> selectedImages = new ArrayList<String>();
    private static boolean ascending = true;
    private final double initialGridHeight = 185;
    public static HashMap<String, ArrayList<String>> albums = new HashMap<String, ArrayList<String>>();
    //thinking this will be used to check if it's the same image that's being clicked twice in a row
    private static String clickedImage = "";
    long time1 = 0;
    long time2 = 0;
    long diff = 0;
    @FXML
    private ScrollPane metadataScroll;
    @FXML
    private ScrollPane tagsScroll;
    @FXML
    private GridPane pictureGrid;
    @FXML
    private ComboBox<?> sortDropDown;
    @FXML
    private TextArea pathDisplay;
    @FXML
    private VBox gridVbox;
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
        logger.log(Level.INFO, "Initializing");
        //this is required, as disabling the textfield in the fxml file made the path way too light to see
        pathDisplay.setEditable(false);
        pictureGrid.setAlignment(Pos.CENTER);
        if (loadedFromAnotherLocation) {
            refreshImages();
            loadedFromAnotherLocation = false;
        }
    }

    @FXML
    private void searchAction(ActionEvent event) {
        logger.log(Level.INFO, "SearchAction");
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
     *
     * @param event user has clicked this item
     */
    @FXML
    private void importAction(ActionEvent event) {
        if (!importStage.isShowing()) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/Import.fxml"));
                importStage.setScene(new Scene(root));
                importStage.setTitle("Import");
                importStage.setResizable(false);
                importStage.showAndWait();
                if (ControllerImport.importSucceed) {
                    logger.log(Level.INFO, "Refreshing");
                    refreshImages();
                    ControllerImport.importSucceed = false;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }


    /**
     * sort the pictures based on the selected value in the drop down
     */
    @FXML
    private void sortAction(ActionEvent event) throws SQLException, FileNotFoundException {
        //if size is selected
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
            Collections.sort(sortedList, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.substring(o1.lastIndexOf("/")).compareTo(o2.substring(o2.lastIndexOf("/")));
                }
            });
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
    private void exportAction(ActionEvent event) {
        if (!exportStage.isShowing()) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/Export.fxml"));
                exportStage.setScene(new Scene(root));
                exportStage.setTitle("Export");
                exportStage.setResizable(false);
                exportStage.showAndWait();
                //exportSucceed is a static variable in controllerExport
                if (ControllerExport.exportSucceed) {
                    System.out.println("Exporting ");
                    refreshImages();
                    ControllerExport.exportSucceed = false;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        selectedImages.clear();
    }

    /**
     * Closes application, and closes connections to database. Cannot close if other windows are open
     *
     * @param event item is clicked
     */
    @FXML
    private void quitAction(ActionEvent event) {
        try {
            logger.log(Level.WARNING, "Closing application");
            databaseClient.closeApplication();
            Platform.exit();
            System.exit(0);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "Could not close application / delete table");
        }
    }

    @FXML
    protected void goToLibrary() {
        //pictureGrid.getScene().setRoot(FXMLLoader.load(getClass().getResource("/Views/Main.fxml")));
        //clears the selected images when you press the library button
        selectedImages.clear();
        refreshImages();
    }

    @FXML
    public void helpAction(ActionEvent actionEvent) {
        System.out.println(selectedImages.toString());
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
        databaseClient.clearPaths();
    }

    /**
     * Refresh Start UI
     */
    protected void refreshImages() {
        try {
            ArrayList paths = databaseClient.getColumn("Path");
            ArrayList addedPaths = DatabaseClient.getAddedPaths();
            clearView();
            for (Object obj : paths) {
                if (obj != null && !addedPaths.contains((String) obj)) {
                    DatabaseClient.getAddedPaths().add((String) obj);
                    insertImage((String) obj);
                }
            }
        } catch (FileNotFoundException | SQLException e) {
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
                if (selectedImages.size() == 0 || selectedImages.size() > 1) {
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
            tint(buff, Color.blue);
            imageView.setImage(SwingFXUtils.toFXImage(buff, null));
        } else {
            selectedImages.remove(path);
            imageView.setImage(image);
        }
    }

    private void showBigImage(ImageView imageView, String path) throws IOException {
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
        if (path == null) {
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
            }
            i++;
        }

    }

    /**
     * When the user clicks on goToMap under library
     * Checks all the added photos for valid gps data, and places the ones with valid data on the map
     *
     * @param actionEvent
     * @throws IOException
     * @throws SQLException
     */
    public void goToMap(ActionEvent actionEvent) throws IOException, SQLException {
        ArrayList<Double> longitude = databaseClient.getColumn("GPS_Longitude");
        ArrayList<Double> latitude = databaseClient.getColumn("GPS_Latitude");
        ArrayList paths = databaseClient.getColumn("Path");
        //do this by checking ration of long at latitiude according to image pixel placing
        //add them to the worldmap view with event listener to check when they're clicked
        for (int i = 0; i < databaseClient.getColumn("GPS_Longitude").size(); i++) {
            //if both are not equal to zero, maybe this should be changed to an or
            if (longitude.get(i) != 0 && latitude.get(i) != 0) {
                locations.put((String) paths.get(i), "" + longitude.get(i) + "," + latitude.get(i));
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

    /**
     * tints the selected images blue
     *
     * @param image the image that you want to tint
     * @param color colour, this should be blue
     */
    private static void tint(BufferedImage image, Color color) {
        //stolen from https://stackoverflow.com/a/36744345
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color pixelColor = new Color(image.getRGB(x, y), true);
                int r = (pixelColor.getRed() + color.getRed()) / 2;
                int g = (pixelColor.getGreen() + color.getGreen()) / 2;
                int b = (pixelColor.getBlue() + color.getBlue()) / 2;
                int a = pixelColor.getAlpha();
                int rgba = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, rgba);
            }
        }
    }

    public void saveAlbumAction(ActionEvent actionEvent) throws IOException {
        Stage albumNameStage = new Stage();
        if (!albumNameStage.isShowing()) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/AlbumNamePicker.fxml"));
                albumNameStage.setScene(new Scene(root));
                albumNameStage.setTitle("Save album");
                albumNameStage.setResizable(false);
                albumNameStage.showAndWait();
                //exportSucceed is a static variable in controllerExport
                if (!ControllerAlbumNamePicker.savedName.equals("")) {
                    refreshImages();
                    ArrayList<String> tempAlbum = new ArrayList<>();
                    //deep copy of selectedImages
                    for(String s : selectedImages){
                        tempAlbum.add(s);
                    }
                    albums.put(ControllerAlbumNamePicker.savedName,tempAlbum);
                    selectedImages.clear();
                    ControllerAlbumNamePicker.savedName = "";
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        selectedImages.clear();

    }

    public void viewAlbums(ActionEvent actionEvent) throws IOException {
        Iterator albumIterator = albums.entrySet().iterator();
        // Iterate through the hashmap
        // and add some bonus marks for every student
        Parent root = FXMLLoader.load(getClass().getResource("/Views/ViewAlbums.fxml"));
        albumStage.setScene(new Scene(root));
        albumStage.setTitle("Albums");
        albumStage.setResizable(false);
        albumStage.showAndWait();
        clearView();
        if(ControllerViewAlbums.albumSelected){
            ControllerViewAlbums.albumSelected = false;
            for(String s : selectedImages){
                insertImage(s);
            }
        }
    }
}

