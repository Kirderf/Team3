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

import backend.Log;
import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MarkerEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private static final Coordinate coordKarlsruheHarbour = new Coordinate(49.015511, 8.323497);
    //ratio is preserved
    private final int thumbnailHeight = 75;
    private static ImageView clickedImage;

    /** default zoom value. */
    private static final int ZOOM_DEFAULT = 14;
    @FXML
    public MapView mapView;

    /** the MapView containing the map */

    public ControllerMap() throws IOException {
        //used to iterate through the images
        //TODO does this need to be a static hashmap? can it be a parameter in some way
        Iterator hmIterator = ControllerMain.getLocations().entrySet().iterator();
        //double array with longitude first, then latitude
        Double[] latLong = new Double[2];
        //iterates through hashmap with pictures that have valid gps data
        while(hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)hmIterator.next();

            String latLongString = (String)mapElement.getValue();
            //latitude
            latLong[0] = Double.parseDouble(latLongString.split(",")[0]);
            //longitude
            latLong[1] = Double.parseDouble(latLongString.split(",")[1]);
            //String with absolute path to image with valid gps data
            String url = (mapElement.getKey().toString().replaceAll("/","\\\\"));
            //resizes image to be used as a thumbnail on map
            String output = resize(url, thumbnailHeight);
            //arraylist which is later used to delete saved images
            savedToDisk.add(output);
            File file = new File(output);
            URL outputUrl = file.toURL();
            //add marker and path to file to markers hashmap
            markers.put((new Marker(outputUrl, -20, -20).setPosition(new Coordinate(latLong[0],latLong[1])).setVisible(false)),(String)mapElement.getKey());
        }

    }
    public static ArrayList<String> getSavedToDisk() {
        return savedToDisk;
    }
    public static ImageView getClickedImage(){
        return clickedImage;
    }
    public static void setClickedImage(ImageView i){
        clickedImage = i;
    }

    public static void emptySavedToDisk() {
        savedToDisk = new ArrayList<String>();
    }

    public void addEventListeners(){
        Iterator markerIterator = markers.entrySet().iterator();
        while(markerIterator.hasNext()){
            Map.Entry markerEntry = (Map.Entry)markerIterator.next();
            mapView.addEventHandler(MarkerEvent.MARKER_CLICKED, event -> {
                try {
                    //formats string to full size version of image clicked on
                    File file = new File(markers.get(event.getMarker()).replaceAll("/","\\\\"));
                    //need to do it this way to get an image from an absolute path
                    Image imageForFile = new Image(file.toURI().toURL().toExternalForm());
                    ImageView imageView = new ImageView(imageForFile);
                    imageView.setId((String)markerEntry.getValue());
                    clickedImage = imageView;
                    closeStage();
                } catch (Exception e) {
                    logger.logNewFatalError("ControllerMap addEventListeners " + e.getLocalizedMessage());
                }
            });
        }
    }
    private void closeStage(){
        Stage stage = (Stage) mapView.getScene().getWindow();
        stage.close();
    }

    public static String resize(String inputImagePath, int scaledHeight)throws IOException {
        // reads input image
        //requestedWidth is just a placeholder, simply needs to be bigger than height
        Image image = new Image(new File(inputImagePath).toURI().toURL().toExternalForm(),scaledHeight*2,scaledHeight,true,false);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image,null);
        //formats string to have \\ instead of /
        //the path to the image is temporary, so the name of the image is given by current time in milliseconds
        String outputImagePath = inputImagePath.substring(0,inputImagePath.replaceAll("\\\\","/").lastIndexOf("/")).replaceAll("\\\\","/") +"/"+ Calendar.getInstance().getTimeInMillis() + inputImagePath.substring(inputImagePath.lastIndexOf("."));
        //writes the thumbnail to the disk so that it can be read by marker creator
        ImageIO.write(bImage, "png", new File(outputImagePath));
        return outputImagePath;
    }


    /**
     * called after the fxml is loaded and all objects are created. This is not called initialize any more,
     * because we need to pass in the projection before initializing.
     *
     *     the projection to use in the map.
     */
    public void initMapAndControls() {
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
        mapView.setCenter(coordKarlsruheHarbour);

    }
    public void afterMapIsInitialized(){
        Iterator markerIterator = markers.entrySet().iterator();
        //iterates through all the added markers
        while(markerIterator.hasNext()){
            Map.Entry markerEntry = (Map.Entry) markerIterator.next();
            //adds the marker to the map
            mapView.addMarker((Marker)markerEntry.getKey());
            ((Marker) markerEntry.getKey()).setVisible(true);
        }
        //adds event listeners to all markers
        addEventListeners();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMapAndControls();
    }
}
