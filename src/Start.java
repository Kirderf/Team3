import backend.Log;
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
    private static final Log logger = new Log();
    private static Text_To_Speech voice;

    /**
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Main.fxml"));
            primaryStage.setTitle("The Greatest Bestests Awesomest Photo Program That Ever Was!!11 AGAINST covid-19");
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(800);
            primaryStage.setScene(new Scene(root));
            logger.logNewInfo("Showing app");
            primaryStage.setOnCloseRequest((event -> {
                logger.logNewInfo("Closing application");
                try {
                    ControllerMain.getDatabaseClient().closeApplication();
                    Platform.exit();
                    System.exit(0);
                } catch (SQLException e) {
                    logger.logNewFatalError("Start start() " + e.getLocalizedMessage());
                }
            }));
            primaryStage.show();
        } catch (Exception e) {
            logger.logNewFatalError("Start start() " + e.getLocalizedMessage());
        }
    }

    public static void main(String[] args) {
        voice = Text_To_Speech.getInstance();
        voice.startup("お早う御座います");
        launch(args);
    }
}
