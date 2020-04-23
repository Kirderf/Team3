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

/**
 * This class is a controller that handles actions made by the user when
 * interacting with the signup stage.
 */
public class ControllerSignup implements Initializable {

    @FXML
    private TextField usernameFieldS;

    @FXML
    private TextField passwordFieldS;
    private EchoClient echoClient = EchoClient.getInstance();

    /**
     * Gets a Parent object with the Signup.fxml loaded.
     *
     * @return Parent
     * @throws IOException if the FXML isn't found
     */
    public static Parent getContent() throws IOException {
        return FXMLLoader.load(ControllerSignup.class.getResource("/Views/SignUp.fxml"));
    }

    /**
     * This method is called when a stage using this controller is
     * created. Sets up the text fields so pressing 'ENTER' will
     * trigger the {@link ControllerSignup#signupAction()} method.
     *
     * @param location  auto-generated
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
     * This method is called if the user chooses to login instead,
     * it loads the login stage's FXML.
     *
     * @throws IOException throws if the FXML can't be found
     */
    @FXML
    void gotoLogin() throws IOException {
        usernameFieldS.getScene().setRoot(ControllerLogin.getContent());
    }

    /**
     * This method is called when the user wants to finalize their sign up.
     * It first checks if the user is connected to the server, and displays an
     * error alert if they aren't. If connected, the database attempts to create a user
     * with the given login details, and displays and error if the username is taken or
     * the username/passsword is invalid.
     */
    @FXML
    void signupAction() {
        if (!echoClient.ping()) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            ((Stage) error.getDialogPane().getScene().getWindow()).getIcons().add(ControllerMain.appIcon);
            error.setTitle("Error when logging in");
            error.setHeaderText(null);
            error.setContentText("There was an error when attempting login, no connection to server");
            error.showAndWait();
        } else {
            if (ControllerMain.getDatabaseClient().newUser(usernameFieldS.getText(), passwordFieldS.getText())) {
                ControllerMain.setLoggedin(true);
                ((Stage) usernameFieldS.getScene().getWindow()).close();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                ((Stage) error.getDialogPane().getScene().getWindow()).getIcons().add(ControllerMain.appIcon);
                error.setTitle("Error when logging in");
                error.setHeaderText(null);
                error.setContentText("There was an error when attempting registration, that username is taken, or you have used non-ascii characters in your username/password ");
                error.showAndWait();
            }
        }
    }
}
