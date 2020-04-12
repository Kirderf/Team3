package controller;

import backend.util.Log;
import backend.util.Text_To_Speech;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ControllerBigImage extends ControllerMain implements Initializable {
    private static final Log logger = new Log();
    private Stage addTagStage = new Stage();
    private Stage importStage = new Stage();
    private Text_To_Speech voice = Text_To_Speech.getInstance();
    private static String imagePath;
    @FXML
    private ImageView bigImage;
    @FXML
    private VBox metadataVbox;
    @FXML
    private GridPane bigImageGrid;

    @FXML
    private TextField textField;

    @FXML
    private VBox imageVbox;
    @FXML
    private VBox tagVbox;
    @FXML
    private SplitPane bigImgDataSplitPane;

    /**
     * Run 1 time once the window opens
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setImagePath(getPathBuffer());
        setBigImage(getImageBuffer());
        showMetadata();
        showTags();
        textField.setEditable(false);
        bigImgDataSplitPane.setDividerPositions(getSplitPanePos());
    }
    public static String getImagePath(){
        return imagePath;
    }
    public static void setImagePath(String s){
        imagePath = s;
    }
    @FXML
    /**
     * when go to library is pressed
     */
    private void goToLibrary(ActionEvent event) throws IOException {
        voice.speak("Going to library");
        clearSelectedImages();
        setSplitPanePos(bigImgDataSplitPane.getDividerPositions()[0]);
        bigImage.getScene().setRoot(FXMLLoader.load(getClass().getResource("/Views/Main.fxml")));
    }

    @Override
    /**
     * Saves image to an album
     *
     * @param event
     * @throws IOException
     */
    @FXML
    protected void saveAlbumAction(ActionEvent event) throws IOException {
        super.saveAlbumAction(event);
        addToSelectedImages(getPathBuffer());
    }

    /**
     * Reinsert a image into view
     *
     * @param imageView the image that is to be shown
     * @param path      the path to the image
     */
    @Override
    protected void showBigImage(ImageView imageView, String path) {
        clearSelectedImages();
        addToSelectedImages(path);
        showMetadata();
        showTags();
        setBigImage(imageView.getImage());
    }

    @Override
    /**
     * Cannot refresh in bigimage view
     */
    protected void refreshImages() {
    }

    @Override
    /**
     * When view albums is clicked
     *
     * @param actionEvent auto-generated
     * @throws IOException
     */
    @FXML
    protected void viewAlbums(ActionEvent actionEvent) throws IOException {
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
            ControllerViewAlbums.setAlbumSelected(false);
            if (!getSelectedImages().isEmpty()) {
                setSplitPanePos(bigImgDataSplitPane.getDividerPositions()[0]);
                bigImage.getScene().setRoot(FXMLLoader.load(getClass().getResource("/Views/Main.fxml")));
            }
        }

    }

    @FXML
    /**
     * add tag is clicked
     */
    private void addTagAction(ActionEvent event) {
        voice.speak("Tagging");
        if (!addTagStage.isShowing()) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/Tagging.fxml"));
                addTagStage.setScene(new Scene(root));
                addTagStage.setTitle("Tagging");
                addTagStage.setResizable(false);
                addTagStage.setOnCloseRequest(event1 -> ControllerTagging.bufferTags.clear());
                addTagStage.showAndWait();
                showTags();
            } catch (Exception exception) {
                exception.printStackTrace();
                logger.logNewFatalError("ControllerBigImage addTagAction " + exception.getLocalizedMessage());
            }
        }
    }


    /**
     * Remove image that is being shown
     * @param event
     * @throws SQLException
     * @throws IOException
     * @return boolean true if something is deleted or false if nothing is deleted.
     */
    @FXML
    protected boolean removeAction(ActionEvent event) throws SQLException, IOException {
        if(super.removeAction(event)) {
            goToLibrary(event);
            return true;
        }
        return false;
    }

    @Override
    /**
     * When the search button is clicked
     */
    @FXML
    protected void searchAction(ActionEvent event) throws IOException {
        //this has to be different form the controllermain search
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
                    ControllerSearch.setSearchSucceed(false);
                    clearSelectedImages();
                    //when going to library these selected images are shown
                    for (String s : ControllerSearch.getSearchResults()) {
                        addToSelectedImages(s);
                    }
                    setSplitPanePos(bigImgDataSplitPane.getDividerPositions()[0]);
                    bigImage.getScene().setRoot(FXMLLoader.load(getClass().getResource("/Views/Main.fxml")));
                }
            } catch (Exception e) {
                logger.logNewFatalError("ControllerBigImage searchAction " + e.getLocalizedMessage());
            }
        }
    }

    @Override
    /**
     * when import image is clicked
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
        }
    }

    /**
     * opens a image to fullscreen view
     *
     * @param image the image that you want to show in fullscreen
     */
    private void setBigImage(Image image) {
        bigImage.setPreserveRatio(true);
        bigImage.fitWidthProperty().bind(bigImageGrid.widthProperty());
        bigImage.fitHeightProperty().bind(imageVbox.heightProperty());
        bigImage.setImage(image);
        textField.setText(getSelectedImages().get(getSelectedImages().size() - 1));
    }

    private void showMetadata() {
        super.showMetadata(getPathBuffer());
    }

    private void showTags() {
        super.showTags(getPathBuffer());
    }

}
