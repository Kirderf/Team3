package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ControllerSignup {

    @FXML
    private TextField usernameFieldS;

    @FXML
    private TextField passwordFieldS;

    @FXML
    void gotoLogin(ActionEvent event) throws IOException {
        usernameFieldS.getScene().setRoot(ControllerLogin.getContent());
    }

    @FXML
    void signupAction(ActionEvent event){

    }

    public String getUsername(){
        return usernameFieldS.getText();
    }

    public String getPassword() {
        return passwordFieldS.getText();
    }

    public static Parent getContent() throws IOException {
        return FXMLLoader.load(ControllerSignup.class.getResource("/Views/SignUp.fxml"));
    }
}
