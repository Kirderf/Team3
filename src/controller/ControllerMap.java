/*
 Copyright 2015-2020 Peter-Josef Meisch (pj.meisch@sothawo.com)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package controller;

import backend.util.Log;
import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MarkerEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


/**
 * Controller for the FXML defined code.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class ControllerMap implements Initializable {
    private static final Log logger = new Log();
    private HashMap<Marker,String> markers = new HashMap<>();
    private static ArrayList<String> savedToDisk = new ArrayList<>();
    /**
     * the location that the map starts at
     */
    private static final Coordinate mapStartingLocation = new Coordinate(0.0, 0.0);
    private static ImageView clickedImage;
    /** default zoom value. */
    private static final int ZOOM_DEFAULT = 2;
    /** the MapView containing the map */
    @FXML
    public MapView mapView;


    public ControllerMap() {
    }

    /**
     * Places the markers on the mapView
     * @throws IOException calls the resize function which writes a file to disk
     */
    private void placeMarkers() throws IOException {
        //used to iterate through the images
        Iterator<Map.Entry<String, String>> hmIterator = ControllerMain.getLocations().entrySet().iterator();
        //double array with longitude first, then latitude
        Double[] longLat = new Double[2];
        //iterates through hashmap with pictures that have valid gps data
        while(hmIterator.hasNext()) {
            Map.Entry<String, String> mapElement = hmIterator.next();

            String latLongString = mapElement.getValue();
            //longitude
            longLat[0] = Double.parseDouble(latLongString.split(",")[0]);
            //latitude
            longLat[1] = Double.parseDouble(latLongString.split(",")[1]);
            //String with absolute path to image with valid gps data
            String url = FilenameUtils.normalize(mapElement.getKey());
            //resizes image to be used as a thumbnail on map
            //ratio is preserved
            String output = resize(url);
            //arraylist which is later used to delete saved images
            savedToDisk.add(output);
            File file = new File(output);
            URL outputUrl = file.toURI().toURL();
            //add marker and path to file to markers hashmap
            markers.put((new Marker(outputUrl, -20, -20).setPosition(new Coordinate(longLat[0], longLat[1])).setVisible(false)), mapElement.getKey());
        }

    }

    /**
     * gets the list of images saved to disk
     * @return arraylist of the images that have been saved to the disk
     */
    static ArrayList<String> getSavedToDisk() {
        return savedToDisk;
    }

    /**
     * gets the image that was clicked on the map
     * @return the full imageView of the thumbnail that was clicked on the image
     */
    static ImageView getClickedImage(){
        return clickedImage;
    }

    /**
     * sets the clicked image
     * @param i ImageView you want to set the clickedImage to
     */
    private static void setClickedImage(ImageView i){
        clickedImage = i;
    }

    /**
     * empties the arraylist containing the path to the images that were saved to disk
     */
    static void emptySavedToDisk() {
        savedToDisk = new ArrayList<>();
    }

    /**
     * adds the eventListener concering clicking markers to the map
     */
    private void addEventListeners(){
        //single event listener for all markers
        mapView.addEventHandler(MarkerEvent.MARKER_CLICKED, event -> {
            try {
                event.consume();
                //formats string to full size version of image clicked on
                //markers is a hashmap with the marker as key and the path to the full image as value
                File file = new File(FilenameUtils.normalize(markers.get(event.getMarker())));
                //need to do it this way to get an image from an absolute path
                Image imageForFile = new Image(file.toURI().toURL().toExternalForm());
                ImageView imageView = new ImageView(imageForFile);
                //sets the id of the imageview to the path to the corrensponding full image
                imageView.setId(markers.get(event.getMarker()));
                setClickedImage(imageView);
                //closes the stage
                closeStage();
            } catch (Exception e) {
                logger.logNewFatalError("ControllerMap addEventListeners " + e.getLocalizedMessage());
            }
        });
    }

    /**
     * Closes the map stag
     */
    private void closeStage(){
        Stage stage = (Stage) mapView.getScene().getWindow();
        stage.close();
    }

    /**
     * resizes the image to be used as a thumbnail on the map
     * @param inputImagePath the path to the image that will be used as a thumbnail on the map
     * @return the path to the thumbnail that was saved to the disk
     * @throws IOException error in writing to disk
     */
    private static String resize(String inputImagePath) throws IOException {
        //the new height that will be assigned to the image, scaledWidth is not needed as the ration is preserved
        int scaledHeight = 75;
        // reads input image
        //requestedWidth is just a placeholder, simply needs to be bigger than height
        Image image = new Image(new File(inputImagePath).toURI().toURL().toExternalForm(),scaledHeight*2,scaledHeight,true,false);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image,null);
        //formats string to have \\ instead of /
        //the path to the image is temporary, so the name of the image is given by current time in milliseconds
        File file = new File(inputImagePath);
        //making sure the filename is different
        String outputImagePath = file.getAbsolutePath().substring(0,file.getAbsolutePath().lastIndexOf(File.separator)+1) + System.currentTimeMillis() +FilenameUtils.EXTENSION_SEPARATOR_STR+ FilenameUtils.getExtension(file.getAbsolutePath());
        //writes the thumbnail to the disk so that it can be read by marker creator
        ImageIO.write(bImage, "png", new File(outputImagePath));
        //the path to the file that was written
        return outputImagePath;
    }


    /**
     * called after the fxml is loaded and all objects are created. This is not called initialize any more,
     * because we need to pass in the projection before initializing.
     *
     *     the projection to use in the map.
     */
    private void initMapAndControls() throws IOException {
        //Projection.WGS_84 or Projection.WEB_MERCATOR
        Projection projection = Projection.WGS_84;
        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                afterMapIsInitialized();
            }
        });

        mapView.initialize(Configuration.builder()
                .projection(projection)
                .showZoomControls(true)
                .build());

        mapView.setZoom(ZOOM_DEFAULT);
        mapView.setCenter(mapStartingLocation);
        placeMarkers();
    }

    /**
     * is run after the map is initialized markers and event listeners are added
     */
    private void afterMapIsInitialized(){
        //iterates through all the added markers
        for (Map.Entry<Marker, String> markerStringEntry : markers.entrySet()) {
            //adds the marker to the map
            mapView.addMarker(markerStringEntry.getKey());
            (markerStringEntry.getKey()).setVisible(true);
        }
        //adds event listeners to all markers
        addEventListeners();
    }

    /**
     * initializes the map
     * @param location auto-generated
     * @param resources anto-generated
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            //initializes
            initMapAndControls();
        } catch (IOException e) {
            logger.logNewFatalError("ControllerMap initialize IOException" + e.getLocalizedMessage());
        }
    }
}
