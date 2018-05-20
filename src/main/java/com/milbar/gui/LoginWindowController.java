package com.milbar.gui;

import com.milbar.gui.abstracts.factories.LoggerFactory;
import com.milbar.logic.exceptions.LoginException;
import com.milbar.logic.login.LoginManager;
import com.milbar.logic.login.wrappers.UserCredentials;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginWindowController extends JavaFXController {
    
    private static Logger log = LoggerFactory.getLogger(LoginWindowController.class);
    
    
    private MainWindowController parentController;
    private String username, password;
    private Stage stage;
    private boolean loginCancelled = false, loginAllowed = true;
    private LoginManager loginManager = new LoginManager();
    
    @FXML
    private TextField userNameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    public PasswordField passwordFieldRepeat;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    public void userNameEntered() {
        username = userNameField.getText();
        log.log(Level.INFO, "User entered username {0}", username);
    }
    
    @FXML
    public void passwordEntered() {
        password = passwordField.getText();
        
        //todo delete this on release
        log.log(Level.INFO, "User entered password {0}", password);
    }
    
    @FXML
    public void cancelButtonClicked() {
        loginCancelled = true;
        closeWindow();
    }
    
    @FXML
    synchronized public void loginButtonClicked() {
        if (!isLoginAllowed()) {
            errorLabel.setText("Logging in is blocked right now.");
            return;
        }
        refreshInputData();
        if (areCredentialsEntered()) {
            try {
                handleUserLoginEvent();
            } catch (LoginException e) {
                log.log(Level.SEVERE, e.getMessage());
                errorLabel.setText(e.getMessage());
            }
        } else
            errorLabel.setText("There are empty fields.");
    }
    
    private void handleUserLoginEvent() throws LoginException {
        //todo commented outdated code, need to update to a new login manager
//        UserCredentials userCredentials = loginManager.getUserCredentials(username, password);
//        parentController.loginUser(userCredentials);
//        closeWindow();
    }
    
    @FXML
    public void registerButtonClicked() {
        lockLogin();
    }
    
    private void lockLogin() {
        loginAllowed = false;
    }
    
    private void unlockLogin() {
        loginAllowed = true;
    }
    
    @Override
    public synchronized void closeWindow() {
        if (loginCancelled){
            parentController.windowClosed(LoginWindow.class.getCanonicalName());
        }
        stage.close();
    }
    
    @Override
    public void setParentController(JavaFXController parentController) {
        this.parentController = (MainWindowController)parentController;
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
    
    public boolean isLoginAllowed() {
        return loginAllowed;
    }
    
    public void registerNewUser(String username, String password) {
        //TODO parentController.registerNewUser();
    }
}