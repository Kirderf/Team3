import backend.util.Text_To_Speech;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
            logger.log(Level.INFO, "Showing app");
            primaryStage.setOnCloseRequest((event -> {
                logger.log(Level.INFO, "Closing application");
                try {
                    // ControllerMain.getDatabaseClient().closeApplication();
                    Platform.exit();
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        voice = new Text_To_Speech();
        voice.startup("お早う御座います");
        launch(args);
    }
}
