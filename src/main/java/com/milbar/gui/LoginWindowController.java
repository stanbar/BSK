package com.milbar.gui;

import com.milbar.logic.exceptions.LoginException;
import com.milbar.logic.login.LoginManager;
import com.milbar.logic.login.UserCredentials;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginWindowController extends JavaFXController {
    
    private MainWindowController parentController;
    private String username, password;
    private Stage stage;
    private boolean loginCancelled = false;
    private LoginManager loginManager = new LoginManager();
    
    @FXML
    private TextField userNameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    public void userNameEntered() {
        username = userNameField.getText();
    }
    
    @FXML
    public void passwordEntered() {
        password = passwordField.getText();
    }
    
    @FXML
    public void cancelButtonClicked() {
        loginCancelled = true;
        closeWindow();
    }
    
    @FXML
    synchronized public void loginButtonClicked() {
        refreshInputData();
        if (areCredentialsEntered()) {
            try {
                UserCredentials userCredentials = loginManager.getUserCredentials(username, password);
                parentController.loginUser(userCredentials);
                closeWindow();
            } catch (LoginException e) {
                errorLabel.setText(e.getMessage());
            }
        }
        else
            errorLabel.setText("There are empty fields.");
    }
    
    @Override
    public synchronized void closeWindow() {
        if (loginCancelled){
            parentController.windowClosed(LoginWindow.class.getSimpleName());
        }
        stage.close();
    }
    
    private boolean areCredentialsEntered() {
        return isStringNotEmpty(username) && isStringNotEmpty(password);
    }
    
    private boolean isStringNotEmpty(String input) {
        return input != null && input.length() > 0;
    }
    
    private void refreshInputData() {
        userNameEntered();
        passwordEntered();
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    void setParentController(MainWindowController parentController) {
        this.parentController = parentController;
    }
}