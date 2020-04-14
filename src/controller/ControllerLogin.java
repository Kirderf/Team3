package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
        if(ControllerMain.getDatabaseClient().login(usernameField.getText(),passwordField.getText())){
            ControllerMain.setLoggedin(true);
            ((Stage) usernameField.getScene().getWindow()).close();
        }
        else{
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error when logging in");
            error.setHeaderText(null);
            error.setContentText("There was an error when attempting login, incorrect username or password");
            error.showAndWait();
        }
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
