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

import com.sothawo.mapjfx.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;


/**
 * Controller for the FXML defined code.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class ControllerMap implements Initializable {

    private ArrayList<Marker> markers = new ArrayList();


    public static ArrayList<Path> savedToDisk = new ArrayList<>();
    private static final Coordinate coordKarlsruheHarbour = new Coordinate(49.015511, 8.323497);
    private static final Coordinate coordKarlsruheSoccer = new Coordinate(49.020035, 8.412975);
    //ratio is preserved
    private final int thumbnailHeight = 75;

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
        Double[] longLat = new Double[2];
        //iterates through hashmap with pictures that have valid gps data
        while(hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)hmIterator.next();

            String longLatString = (String)mapElement.getValue();
            longLat[0] = Double.parseDouble(longLatString.split(",")[0]);
            longLat[1] = Double.parseDouble(longLatString.split(",")[1]);
            System.out.println(mapElement.getKey().toString());
            System.out.println((mapElement.getKey().toString().replaceAll("/","\\\\")));
            String url = (mapElement.getKey().toString().replaceAll("/","\\\\"));
            String output = resize(url, thumbnailHeight*2, thumbnailHeight);
            Path outputPath = Paths.get(output);
            savedToDisk.add(outputPath);
            File file = new File(outputPath.toString());
            URL outputUrl = file.toURL();
            markers.add(new Marker(outputUrl, -20, -20).setPosition(new Coordinate(longLat[0],longLat[1])).setVisible(false));
        }
    }
    public static ArrayList<Path> getSavedToDisk() {
        return savedToDisk;
    }

    public static void emptySavedToDisk() {
        savedToDisk = new ArrayList<Path>();
    }


    public static String resize(String inputImagePath, int scaledWidth, int scaledHeight)throws IOException {
        // reads input image
        Image image = new Image(new File(inputImagePath).toURI().toURL().toExternalForm(),scaledWidth,scaledHeight,true,false);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image,null);
        String outputImagePath = inputImagePath.substring(0,inputImagePath.replaceAll("\\\\","/").lastIndexOf("/")).replaceAll("\\\\","/") +"/"+ Calendar.getInstance().getTimeInMillis() + inputImagePath.substring(inputImagePath.lastIndexOf("."));
        Path outputPath = Paths.get(outputImagePath);
        ImageIO.write(bImage, "png", new File(outputPath.toString()));
        return outputPath.toString();
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
        for(Marker m : markers){
            mapView.addMarker(m);
            m.setVisible(true);
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMapAndControls();
    }
}
