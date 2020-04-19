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
     * gets the fxml file for the login
     *
     * @return returns loader loading login.fxml
     * @throws IOException
     */
    static Parent getContent() throws IOException {
        return FXMLLoader.load(ControllerLogin.class.getResource("/Views/Login.fxml"));
    }

    /**
     * when the program first is shown
     *
     * @param location
     * @param resources
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
     * if the user clicks the button to go to signup instead
     *
     * @throws IOException
     */
    @FXML
    void gotoSignup() throws IOException {
        saveLogin.close();
        usernameField.getScene().setRoot(ControllerSignup.getContent());
    }

    /**
     * when the user clicks login
     *
     * @throws IOException if the login details are read from .txt file
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
        }
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
