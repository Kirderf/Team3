package controller;

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

public class ControllerLogin implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    public static Parent getContent() throws IOException {
        return FXMLLoader.load(ControllerLogin.class.getResource("/Views/Login.fxml"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                loginAction();
            }
        });
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                loginAction();
            }
        });

    }

    @FXML
    void gotoSignup() throws IOException {
        usernameField.getScene().setRoot(ControllerSignup.getContent());
    }

    @FXML
    void loginAction() {
        if (ControllerMain.getDatabaseClient().login(usernameField.getText(), passwordField.getText())) {
            ControllerMain.setLoggedin(true);
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
