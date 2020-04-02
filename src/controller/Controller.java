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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * Controller for the FXML defined code.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class Controller implements Initializable {

    /** default zoom value. */
    private static final int ZOOM_DEFAULT = 14;
    @FXML
    public MapView mapView;

    /** the MapView containing the map */

    public Controller() {
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
        mapView.initialize(Configuration.builder()
                .projection(projection)
                .showZoomControls(true)
                .build());
        Marker markerKaSoccer = new Marker(getClass().getResource("/sloth.png"), 0, 0).setPosition(new Coordinate(0.3,0.3))
                .setVisible(false);
        Marker markerKaStation =
                Marker.createProvided(Marker.Provided.RED).setPosition(new Coordinate(0.0,0.0)).setVisible(false);
        MapLabel labelKaCastle = new MapLabel("castle", 10, -10).setVisible(false).setCssClass("green-label");
        markerKaStation.attachLabel(labelKaCastle);

        mapView.addMarker(markerKaStation);
        markerKaStation.setVisible(true);
        mapView.addMarker(markerKaSoccer);
        mapView.setZoom(ZOOM_DEFAULT);
        mapView.setCenter(new Coordinate(0.0,0.0));

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMapAndControls();
    }
}
