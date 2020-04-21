package controller;

import backend.util.EchoClient;
import backend.util.Log;
import backend.util.SaveLogin;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class controls the actions made by the user when
 * interacting with the login stage.
 */
public class ControllerLogin implements Initializable {
    Log logger = new Log();
    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private CheckBox rememberMe;

    private SaveLogin saveLogin;
    private EchoClient echoClient = EchoClient.getInstance();

    /**
     * Gets a Parent object with the Login.fxml loaded.
     *
     * @return Parent
     * @throws IOException if the FXML isn't found
     */
    static Parent getContent() throws IOException {
        return FXMLLoader.load(ControllerLogin.class.getResource("/Views/Login.fxml"));
    }

    /**
     * This method is called when a stage using this controller is
     * created. Handles the login stage's fields and loads the user's
     * login information if it's saved.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    loginAction();
                } catch (IOException e) {
                    logger.logNewFatalError("ControllerLogin initialize " + e.getLocalizedMessage());
                }
            }
        });
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    loginAction();
                } catch (IOException e) {
                    logger.logNewFatalError("ControllerLogin initialize " + e.getLocalizedMessage());
                }
            }
        });
        try {
            saveLogin = new SaveLogin();
            if (saveLogin.isRemembered()) {
                rememberMe.setSelected(true);
                String[] user = saveLogin.getUser();
                usernameField.setText(user[0]);
                passwordField.setText(user[1]);
            }
        } catch (IOException e) {
            logger.logNewFatalError("ControllerLogin initialize " + e.getLocalizedMessage());
        }
    }

    /**
     * This method is called if the user chooses to sign up,
     * it loads the sign up stage's FXML.
     *
     * @throws IOException if the FXML can't be found
     */
    @FXML
    void gotoSignup() throws IOException {
        saveLogin.close();
        usernameField.getScene().setRoot(ControllerSignup.getContent());
    }

    /**
     * This method is called when the user presses the login button.
     * Checks if the user is connected to the server, then attempts to login with
     * the user's entered login informatio.
     *
     * @throws IOException if the login details are read from .txt file, but can't be found
     */
    @FXML
    void loginAction() throws IOException {
        if (!echoClient.ping()) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error when logging in");
            error.setHeaderText(null);
            error.setContentText("There was an error when attempting login, no connection to server");
            error.showAndWait();
            return;
        } else {
            if (ControllerMain.getDatabaseClient().login(usernameField.getText(), passwordField.getText())) {
                ControllerMain.setLoggedin(true);
                if (rememberMe.isSelected()) {
                    saveLogin.saveUser(usernameField.getText(), passwordField.getText());
                } else {
                    saveLogin.deleteSaveData();
                }
                saveLogin.close();

                ((Stage) usernameField.getScene().getWindow()).close();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error when logging in");
                error.setHeaderText(null);
                error.setContentText("There was an error when attempting login, incorrect username or password");
                error.showAndWait();
            }
        }
    }


}
