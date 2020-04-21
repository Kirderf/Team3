package controller;

import backend.util.EchoClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerSignup implements Initializable {

    @FXML
    private TextField usernameFieldS;

    @FXML
    private TextField passwordFieldS;
    private EchoClient echoClient = EchoClient.getInstance();

    public static Parent getContent() throws IOException {
        return FXMLLoader.load(ControllerSignup.class.getResource("/Views/SignUp.fxml"));
    }

    /**
     * When the signup window is shown
     * @param location auto-generated
     * @param resources auto-generated
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        passwordFieldS.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                signupAction();
            }
        });
        usernameFieldS.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                signupAction();
            }
        });
    }

    /**
     * When the user clicks login instead
     * @throws IOException
     */
    @FXML
    void gotoLogin() throws IOException {
        usernameFieldS.getScene().setRoot(ControllerLogin.getContent());
    }

    /**
     * when the user clicks sign up
     */
    @FXML
    void signupAction() {
        if (!echoClient.ping()) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error when logging in");
            error.setHeaderText(null);
            error.setContentText("There was an error when attempting login, no connection to server");
            error.showAndWait();
            return;
        } else {
            if (ControllerMain.getDatabaseClient().newUser(usernameFieldS.getText(), passwordFieldS.getText())) {
                ControllerMain.setLoggedin(true);
                ((Stage) usernameFieldS.getScene().getWindow()).close();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error when logging in");
                error.setHeaderText(null);
                error.setContentText("There was an error when attempting registration, that username is taken, or you have used non-ascii characters in your username/password ");
                error.showAndWait();
            }
        }
    }
}
