package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ControllerLogin {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    void gotoSignup(ActionEvent event) throws IOException {
        usernameField.getScene().setRoot(ControllerSignup.getContent());
    }

    @FXML
    void loginAction(ActionEvent event) {
    }

    public String getUsername(){
        return usernameField.getText();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public static Parent getContent() throws IOException {
        return FXMLLoader.load(ControllerLogin.class.getResource("/Views/Login.fxml"));
    }
}
