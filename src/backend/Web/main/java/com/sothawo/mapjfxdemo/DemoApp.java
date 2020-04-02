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
package backend.Web.main.java.com.sothawo.mapjfxdemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Demo application for the mapjfx component.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class DemoApp extends Application {

    /** Logger for the class */

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String fxmlFile = "/Views/DemoApp.fxml";
        Parent rootNode = FXMLLoader.load(getClass().getResource(fxmlFile));

        Scene scene = new Scene(rootNode);
        primaryStage.setTitle("sothawo mapjfx demo application");
        primaryStage.setScene(scene);
        primaryStage.show();
        /*
        for(Path p : Controller.getSavedToDisk()){
            new File(p.toString()).delete();
        }
        Controller.emptySavedToDisk();
         */
    }
}
