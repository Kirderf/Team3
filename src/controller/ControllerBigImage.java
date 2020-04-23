package controller;

import backend.util.Log;
import backend.util.Text_To_Speech;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * This class is a controller that handles actions made by
 * the user when in the "Big Image" view, i.e. the
 * fullscreen view reached when clicking on an image.
 */
public class ControllerBigImage extends ControllerMain implements Initializable {
    private static final Log logger = new Log();
    private Stage addTagStage = new Stage();
    private Stage albumStage = new Stage();
    private Text_To_Speech voice = Text_To_Speech.getInstance();
    @FXML
    private Label homeLabel;
    @FXML
    private ImageView bigImage;
    @FXML
    private Menu buttonHome;
    @FXML
    private GridPane bigImageGrid;
    @FXML
    private TextField textField;

    @FXML
    private VBox imageVbox;
    @FXML
    private SplitPane bigImgDataSplitPane;

    /**
     * This method is called when a scene is created using this controller.
     * In this case, it gets the path to the image the user has clicked on,
     * finds the corresponding image, metadata and tags, then shows
     * these in the "Big Image" view.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.setAllIcons(ControllerMain.appIcon);
        addTagStage.getIcons().add(ControllerMain.appIcon);
        setBigImage(getImageBuffer());
        super.showData();
        textField.setEditable(false);
        bigImgDataSplitPane.setDividerPositions(getSplitPanePos());
    }

    /**
     * This method is called when the home button is clicked, and takes
     * the user back to the library/home menu.
     *
     * @throws IOException reads fxml file
     */
    @Override
    @FXML
    protected void goToLibrary() throws IOException {
        voice.speak("Going to library");
        clearSelectedImages();
        setSplitPanePos(bigImgDataSplitPane.getDividerPositions()[0]);
        bigImage.getScene().setRoot(FXMLLoader.load(getClass().getResource("/Views/Main.fxml")));
    }

    /**
     * Saves the image to a new album
     */
    @Override
    @FXML
    protected void saveAlbumAction(ActionEvent event) {
        super.saveAlbumAction(event);
        addToSelectedImages(getPathBuffer());
        showData();

    }

    /**
     * Reinsert the image into view
     *
     * @param imageView the image that is to be shown
     * @param path      the path to the image
     */
    @Override
    protected void showBigImage(ImageView imageView, String path) {
        clearSelectedImages();
        addToSelectedImages(path);
        showData();
        setBigImage(imageView.getImage());
    }

    /**
     * This method is called when the 'View Albums' button is pressed, and
     * generates the album stage where the user can view their existing albums.
     *
     * @param actionEvent auto-generated
     * @throws IOException if an fxml is not found
     */
    @Override
    @FXML
    protected void viewAlbums(ActionEvent actionEvent) throws IOException {
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
            ControllerViewAlbums.setAlbumSelected(false);
            if (!getSelectedImages().isEmpty()) {
                setSplitPanePos(bigImgDataSplitPane.getDividerPositions()[0]);
                bigImage.getScene().setRoot(FXMLLoader.load(getClass().getResource("/Views/Main.fxml")));
            }
        }

    }

    /*
     * This method is called when the 'Edit tags' button is pressed, and
     * generates the Tagging stage where the user can edit the tags attached
     * to the image.
     */
    @FXML
    private void addTagAction() {
        voice.speak("Tagging");
        if (!addTagStage.isShowing()) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Views/Tagging.fxml"));
                addTagStage.setScene(new Scene(root));
                addTagStage.setTitle("Tagging");
                addTagStage.setResizable(false);
                addTagStage.setOnCloseRequest(event1 -> ControllerTagging.bufferTags.clear());
                addTagStage.showAndWait();
                showData();
            } catch (Exception exception) {
                logger.logNewFatalError("ControllerBigImage addTagAction " + exception.getLocalizedMessage());
            }
        }
    }


    /**
     * This method is called when the user chooses 'Remove' from the
     * file menu, and removes the shown image from the database.
     *
     * @param event the button being pressed
     * @return boolean true if something is deleted or false if nothing is deleted.
     */
    @FXML
    protected boolean removeAction(ActionEvent event) throws IOException {
        if (super.removeAction(event)) {
            goToLibrary();
            return true;
        }
        return false;
    }

    /**
     * This method is called when the 'Search' item is chosen from the Search menu, and
     * opens the search stage where the user can search after images using chosen criteria.
     *
     * @param event the button being pressed
     */
    @Override
    @FXML
    protected void searchAction(ActionEvent event) {
        //this has to be different form the controllermain search
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
                    ControllerSearch.setSearchSucceed(false);
                    clearSelectedImages();
                    //when going to library these selected images are shown
                    if (ControllerSearch.getSearchResults().size() != 0) {
                        for (String s : ControllerSearch.getSearchResults()) {
                            addToSelectedImages(s);
                        }
                    } else {
                        Alert a = new Alert(Alert.AlertType.WARNING);
                        ((Stage)a.getDialogPane().getScene().getWindow()).getIcons().add(appIcon);
                        a.setHeaderText(null);
                        a.setContentText("No images found matching the search criteria, showing all images instead.");
                        a.showAndWait();
                    }
                    setSplitPanePos(bigImgDataSplitPane.getDividerPositions()[0]);
                    bigImage.getScene().setRoot(FXMLLoader.load(getClass().getResource("/Views/Main.fxml")));
                }
            } catch (Exception e) {
                logger.logNewFatalError("ControllerBigImage searchAction " + e.getLocalizedMessage());
            }
        }
    }


    /*
     * opens an image to fullscreen view
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

}
