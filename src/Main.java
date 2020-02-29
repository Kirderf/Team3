import backend.Database;
import backend.DatabaseClient;
import controller.ControllerMain;
import controller.ControllerImport;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.sql.SQLException;


public class Main extends Application {

    /**
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Main.fxml"));
            primaryStage.setTitle("Hello World");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();
            primaryStage.setOnCloseRequest((event -> {
                if (ControllerMain.importStage.isShowing()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setContentText("Remember to close all other windows before exiting UwU");
                    alert.showAndWait();
                    event.consume();
                } else {
                    try {
                        ControllerMain.databaseClient.closeApplication();
                        System.out.println("closes");
                        primaryStage.close();
                        System.exit(0);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
