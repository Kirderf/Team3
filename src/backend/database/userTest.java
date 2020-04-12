package backend.database;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class userTest extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Views/Login.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.setOnCloseRequest((event -> {
                Platform.exit();
                System.exit(0);
            }));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
/*
public class userTest {
    public static void main(String[] args) throws UnsupportedEncodingException {
        UserDAO userDAO = new UserDAO("testuser","feil");
        System.out.println(userDAO.verifyPassword("feil"));
        System.out.println(userDAO.verifyPassword("pass1"));
    }
}
*/