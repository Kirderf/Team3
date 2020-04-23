import backend.util.Log;
import backend.util.Text_To_Speech;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Start extends Application {
    private static final Log logger = new Log();

    /**
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Main.fxml"));
            primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("squareLogo.png")));
            primaryStage.setTitle("The Greatest Bestests Awesomest Photo Program That Ever Was!!11 AGAINST covid-19");
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(800);
            primaryStage.setScene(new Scene(root));
            logger.logNewInfo("Showing app");
            primaryStage.setOnCloseRequest((event -> {
                logger.logNewInfo("Closing application");
                Platform.exit();
                System.exit(0);
            }));
            primaryStage.show();
        } catch (Exception e) {
            logger.logNewFatalError("Start start() " + e.getLocalizedMessage());
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
