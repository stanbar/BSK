package com.milbar.gui;

import com.milbar.gui.abstracts.factories.LoggerFactory;
import com.milbar.logic.exceptions.LoginException;
import com.milbar.logic.exceptions.RegisterException;
import com.milbar.logic.exceptions.UnexpectedWindowEventCall;
import com.milbar.logic.login.LoginManager;
import com.milbar.logic.login.wrappers.SessionToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginWindowController extends JavaFXController implements JavaFXWindowsListener {
    
    private static Logger log = LoggerFactory.getLogger(LoginWindowController.class);
    
    private MainWindowController parentController;
    private Set<String> openedWindows = new HashSet<>();
    
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
        log.log(Level.INFO, "User entered username {0}", userNameField.getText());
    }
    
    @FXML
    public void passwordEntered() {
        //todo delete this on release
        log.log(Level.INFO, "User entered password {0}", passwordField.getText());
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
                handleUserLoginEvent(userNameField.getText(), passwordField.getText());
            } catch (LoginException e) {
                log.log(Level.SEVERE, e.getMessage());
                errorLabel.setText(e.getMessage());
            }
        } else
            errorLabel.setText("There are empty fields.");
    }
    
    private void handleUserLoginEvent(String username, String password) throws LoginException {
        SessionToken sessionToken = loginManager.login(username, password);
        parentController.setSessionToken(sessionToken);
        loginCancelled = false;
        closeWindow();
    }
    
    @FXML
    public void registerButtonClicked() {
        lockLogin();
        openRegisterWindow();
    }
    
    private void openRegisterWindow() {
        if (openedWindows.add(RegisterWindow.class.getSimpleName())) {
            try {
                RegisterWindow registerWindow = new RegisterWindow(this);
            } catch (IOException e) {
                e.printStackTrace();
                writeToErrorLabel("Failed to open register window.");
            }
        }
        else
            writeToErrorLabel("Register window is already open!");
    }
    
    private void writeToErrorLabel(String msg) {
        errorLabel.setText(msg);
        errorLabel.setTextFill(Color.RED);
    }
    
    private void lockLogin() {
        loginAllowed = false;
    }
    
    private void unlockLogin() {
        loginAllowed = true;
    }
    
    @Override
    public synchronized void closeWindow() {
        if (loginCancelled)
            parentController.windowClosed(LoginWindow.class.getCanonicalName());
        
        stage.close();
    }
    
    @Override
    public void setParentController(JavaFXController parentController) {
        this.parentController = (MainWindowController)parentController;
    }
    
    private boolean areCredentialsEntered() {
        return userNameField.getLength() > 0 && passwordField.getLength() > 0;
    }
    
    private void refreshInputData() {
        userNameEntered();
        passwordEntered();
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    private boolean isLoginAllowed() {
        return loginAllowed;
    }
    
    void registerNewUser(String username, String password) throws RegisterException {
        loginManager.register(username, password);
    }
    
    @Override
    public void windowClosed(String callerClassName) {
        if (!openedWindows.remove(callerClassName))
            throw new UnexpectedWindowEventCall("Class name: " + callerClassName);
        
        unlockLogin();
    }
}