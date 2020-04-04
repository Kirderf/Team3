package controller;


import backend.Log;
import backend.Text_To_Speech;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.NumberStringConverter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
//TODO make remove work, make viewAlbum work, make search work
public class ControllerBigImage extends ControllerMain implements Initializable {
    private static final Log logger = new Log();

    private Stage addTagStage = new Stage();
    private Stage importStage = new Stage();
    private Stage addToAlbumStage = new Stage();
    private Text_To_Speech voice = Text_To_Speech.getInstance();
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
        setBigImage(getImageBuffer());
        showMetadata();
        showTags();
        textField.setEditable(false);
        bigImgDataSplitPane.setDividerPositions(getSplitPanePos());
    }

    @FXML
    /**
     * when go to library is pressed
     */
    private void goToLibrary(ActionEvent event) throws IOException {
        voice.speak("Going to library");
        setSplitPanePos(bigImgDataSplitPane.getDividerPositions()[0]);
        bigImage.getScene().setRoot(FXMLLoader.load(getClass().getResource("/Views/Main.fxml")));

    }

    @FXML
    @Override
    /**
     * When view albums is clicked
     *
     * @param actionEvent auto-generated
     * @throws IOException
     */
    protected void viewAlbums(ActionEvent actionEvent) throws IOException {
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
            goToLibrary(actionEvent);
            Platform.runLater(() -> {
                if (!getSelectedImages().isEmpty()) {
                    clearView();
                    ControllerViewAlbums.setAlbumSelected(false);
                    for (String s : getSelectedImages()) {
                        try {
                            insertImage(s);
                        } catch (FileNotFoundException e) {
                            logger.logNewFatalError("ControllerBigImage viewAlbums " + e.getLocalizedMessage());
                        }
                    }
                } else {
                    refreshImages();
                }
            });

        }
    }

    @Override
    @FXML
    protected void saveAlbumAction(ActionEvent event) throws IOException {
        super.saveAlbumAction(event);
        addToSelectedImages(pathBuffer);
    }

    @Override
    protected void showBigImage(ImageView imageView, String path) {
        clearSelectedImages();
        addToSelectedImages(path);
        showMetadata();
        showTags();
        setBigImage(imageView.getImage());
    }

    @Override
    protected void refreshImages() {
    }

    @FXML
    /**
     * add tag is clicked
     */
    private void addTagAction(ActionEvent event){
        voice.speak("Tagging");
        if(!addTagStage.isShowing()){
            try{
                Parent root = FXMLLoader.load(getClass().getResource("/Views/Tagging.fxml"));
                addTagStage.setScene(new Scene(root));
                addTagStage.setTitle("Tagging");
                addTagStage.setResizable(false);
                addTagStage.setOnCloseRequest(event1 -> ControllerTagging.bufferTags.clear());
                addTagStage.showAndWait();
            }catch (Exception exception){
                exception.printStackTrace();
                logger.logNewFatalError("ControllerBigImage addTagAction " + exception.getLocalizedMessage());
            }
        }
    }


    @Override
    @FXML
    protected void removeAction(ActionEvent event) throws SQLException, IOException {
        super.removeAction(event);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    goToLibrary(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    /**
     * when import image is clicked
     */
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
     * @param image the image that you want to show in fullscreen
     */
    private void setBigImage(Image image) {
        bigImage.setPreserveRatio(true);
        bigImage.fitWidthProperty().bind(bigImageGrid.widthProperty());
        bigImage.fitHeightProperty().bind(imageVbox.heightProperty());
        bigImage.setImage(image);
        textField.setText(getSelectedImages().get(getSelectedImages().size()-1));
    }

    private void showMetadata() {
        if(selectedImages.isEmpty()) return;
        String path = getSelectedImages().get(getSelectedImages().size()-1);
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
    private void showTags(){
        if(selectedImages.isEmpty()) return;
        String path = getSelectedImages().get(getSelectedImages().size()-1);
        tagVbox.getChildren().clear();
        String[] tags = getDatabaseClient().getTags(path).split(",");
        tagVbox.getChildren().add(new Label("Tags:"));
        for (int i = 0; i<tags.length;i++) {
            tagVbox.getChildren().add(new Label(tags[i]));
        }
    }


}
