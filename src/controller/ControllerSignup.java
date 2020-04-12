package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
        if(ControllerMain.getDatabaseClient().newUser(usernameFieldS.getText(),passwordFieldS.getText())){
            ControllerMain.setLoggedin(true);
            ((Stage) usernameFieldS.getScene().getWindow()).close();
        }
        else{
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error when logging in");
            error.setHeaderText(null);
            error.setContentText("There was an error when attempting registration, that username is taken, or you have used non-ascii characters in your username/password ");
            error.showAndWait();
        }
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
