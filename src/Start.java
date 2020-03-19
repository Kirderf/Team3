import backend.Text_To_Speech;
import controller.ControllerMain;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Start extends Application {
    private static final Logger logger = Logger.getLogger(Start.class.getName());
    private static Text_To_Speech voice;

    /**
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Main.fxml"));
            primaryStage.setTitle("The Greatest Bestests Awesomest Photo Program That Ever Was!!11 AGAINST covid-19");
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.setScene(new Scene(root));
            ControllerMain.importStage.initModality(Modality.APPLICATION_MODAL);
            ControllerMain.importStage.initStyle(StageStyle.UTILITY);
            ControllerMain.searchStage.initModality(Modality.APPLICATION_MODAL);
            ControllerMain.searchStage.initStyle(StageStyle.UTILITY);
            logger.log(Level.INFO, "Showing app");
            primaryStage.show();
            primaryStage.setOnCloseRequest((event -> {
                logger.log(Level.INFO, "Closing application");
                try {
                    ControllerMain.databaseClient.closeApplication();
                    Platform.exit();
                    System.exit(0);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        voice = new Text_To_Speech();
        voice.speak("お早う御座います");
        launch(args);
    }
}
