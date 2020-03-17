import backend.Text_To_Speech;
import controller.ControllerMain;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.net.URL;
import java.util.logging.Logger;
import sun.audio.AudioData;
import sun.audio.AudioDataStream;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;


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


    public static void main(String[] args) throws IOException {
        voice = new Text_To_Speech();
        voice.speak("お早う御座います");
        playBackGroundMusic();
        launch(args);
    }

    public static void playBackGroundMusic(){
        InputStream music = Start.class.getResourceAsStream("yeet.wav");
        try{
            //music = new FileInputStream(file);
            AudioStream audios = new AudioStream(music);
            AudioPlayer.player.start(audios);
            
        }catch(Exception e){
            backend.Log logger = new backend.Log("log.log");
            logger.logNewFatalError(e.getLocalizedMessage());
        }
    }
}
