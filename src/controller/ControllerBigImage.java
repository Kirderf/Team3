package controller;

import backend.util.Log;
import backend.util.Text_To_Speech;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
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

public class ControllerBigImage extends ControllerMain implements Initializable {
    private static final Log logger = new Log();
    @FXML
    Label homeLabel;
    private Stage addTagStage = new Stage();
    private Stage importStage = new Stage();
    private Text_To_Speech voice = Text_To_Speech.getInstance();
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

    public static void setImagePath() {
    }

    /**
     * Run 1 time once the window opens
     *
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setImagePath();
        setBigImage(getImageBuffer());
        super.showMetadata();
        super.showTags();
        textField.setEditable(false);
        bigImgDataSplitPane.setDividerPositions(getSplitPanePos());
    }

    /**
     * when home button is clicked
     * @throws IOException reads fxml file
     */
    @FXML
    protected void goToLibrary() throws IOException {
        voice.speak("Going to library");
        clearSelectedImages();
        setSplitPanePos(bigImgDataSplitPane.getDividerPositions()[0]);
        bigImage.getScene().setRoot(FXMLLoader.load(getClass().getResource("/Views/Main.fxml")));
    }

    /**
     * Saves image to an album
     *
     * @param event
     * @throws IOException
     */
    @Override
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

    /**
     * Cannot refresh in bigimage view
     */
    @Override
    protected void refreshImages() {
    }

    /**
     * When view albums is clicked
     *
     * @param actionEvent auto-generated
     * @throws IOException
     */
    @Override
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

    /**
     * add tag is clicked
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
                showTags();
            } catch (Exception exception) {
                exception.printStackTrace();
                logger.logNewFatalError("ControllerBigImage addTagAction " + exception.getLocalizedMessage());
            }
        }
    }


    /**
     * Remove image that is being shown
     *
     * @param event
     * @return boolean true if something is deleted or false if nothing is deleted.
     * @throws SQLException
     * @throws IOException
     */
    @FXML
    protected boolean removeAction(ActionEvent event) throws IOException, SQLException {
        if (super.removeAction(event)) {
            goToLibrary();
            return true;
        }
        return false;
    }

    /**
     * When the search button is clicked
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

    /**
     * when import image is clicked
     */
    @Override
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
